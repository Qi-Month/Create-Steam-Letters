package dev.celestiacraft.steam_letters.common.block.burner;

import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.common.recipe.type.FluidFuelRecipe;
import dev.celestiacraft.steam_letters.common.register.SlBlockEntities;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

/**
 * 流体燃烧室的方块实体.
 * <p>
 * 可燃性完全由 {@code steam_letters:fluid_fuel} 配方决定, 不参考流体的类型或属性;
 * 燃料按 tick 持续推进: 一份 {@link FluidFuelRecipe#FUEL_AMOUNT} mB 恰好铺满配方的
 * {@code time} tick, 所以每 tick 从罐里抽 {@code FUEL_AMOUNT / time} mB,
 * 一整份烧完的时间与配方声明的时长完全一致.
 * <p>
 * 罐里不足一整份时按同样速率继续烧, <b>有多少烧多少</b>, 烧空即停; 因此不存在
 * 凑不满一份就永远点不着的情况.
 */
public class FluidBurnerBlockEntity extends AbstractBurnerBlockEntity {
	/**
	 * 内置青铜流体燃烧室的默认燃料罐容量 (mB).
	 */
	public static final int DEFAULT_CAPACITY = 1000;

	private static final String NBT_TANK = "FuelTank";
	private static final String NBT_BURN_TOTAL = "BurnTotal";

	/**
	 * 诊断用的临时标志, 定位流体能力问题后删除.
	 */
	private static boolean queryLogged;

	@Getter
	private final FuelFluidTank fuelTank;

	/**
	 * 配方查找缓存, 以罐内流体为键.
	 */
	private FluidStack recipeKey = FluidStack.EMPTY;
	@Nullable
	private FluidFuelRecipe recipeValue;
	private boolean recipeCacheValid;

	/**
	 * 当前这一轮燃烧的名义总时长 (tick);
	 * 0 表示当前没有进行中的一轮. 需要持久化, 否则读档后本轮剩余时间无从恢复.
	 */
	private int burnTotalTime;

	protected IFluidHandler fluidCapability;

	public FluidBurnerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);

		int capacity = spec.getCapacity();
		if (capacity <= 0) {
			capacity = DEFAULT_CAPACITY;
		}

		fuelTank = new FuelFluidTank(this, capacity, this::isFuel);

		fluidCapability = fuelTank;
	}

	@Override
	public void onLoad() {
		super.onLoad();
		invalidateRecipeCache();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		invalidateRecipeCache();
	}

	@Override
	protected boolean tickFuel() {
		if (level == null) {
			return false;
		}

		if (getBurnTime() <= 0 && !beginRound()) {
			return false;
		}

		if (!consumeForCurrentTick()) {
			// 罐里连这一 tick 该耗的量都不够, 本轮到此为止
			setBurnTime(0);
			setChanged();
			return false;
		}

		// 一律走 setter, 字段本身由父类私有
		setBurnTime(getBurnTime() - 1);
		return true;
	}

	/**
	 * 开启新的一轮燃烧: 找配方, 记下这一轮的名义总时长.
	 * <p>
	 * 开轮不再一次性扣掉一整份燃料, 料按 tick 逐点从罐里扣, 速率见 {@link #consumeForCurrentTick()};
	 * 罐里剩多少就能烧多少, 不足一份也照烧, 只是会提前烧完.
	 */
	private boolean beginRound() {
		FluidStack stored = fuelTank.getFluid();
		if (stored.isEmpty()) {
			return false;
		}

		FluidFuelRecipe recipe = findRecipe(stored);
		if (recipe == null) {
			return false;
		}

		int total = recipe.getTime();
		if (total <= 0) {
			return false;
		}

		burnTotalTime = total;
		setBurnTime(total);
		setSuperheatedFuel(recipe.isSuperheated());
		setChanged();
		return true;
	}

	/**
	 * 扣掉本 tick 该消耗的燃料.
	 * <p>
	 * 速率是「一份 {@link FluidFuelRecipe#FUEL_AMOUNT} mB 铺满 processingTime tick」,
	 * 即每 tick 消耗 {@code FUEL_AMOUNT / time} mB. 该比值通常除不尽, 逐 tick 取整会让一整轮
	 * 短一截或多一截, 所以按累计值分摊: 第 k tick 的消耗取
	 * {@code floor(FUEL_AMOUNT * k / time) - floor(FUEL_AMOUNT * (k - 1) / time)},
	 * 单 tick 的消耗在 0 至 ceil(该比值) mB 之间浮动, 但一整轮的总量精确等于 FUEL_AMOUNT,
	 * 因此 1000mB 流体正好烧满 processingTime tick.
	 *
	 * @return 罐里燃料是否够本 tick 的消耗
	 */
	private boolean consumeForCurrentTick() {
		int total = burnTotalTime;
		if (total <= 0) {
			return false;
		}

		int elapsed = total - getBurnTime();
		int due = (int) (fuelBurnedAfter(elapsed + 1, total) - fuelBurnedAfter(elapsed, total));
		if (due <= 0) {
			return true;
		}
		if (fuelTank.getFluidAmount() < due) {
			return false;
		}

		return fuelTank.drain(due, IFluidHandler.FluidAction.EXECUTE).getAmount() >= due;
	}

	/**
	 * 一轮烧到第 ticks tick 时应当累计消耗的燃料量 (整数除法即为向下取整).
	 */
	private static long fuelBurnedAfter(int ticks, int total) {
		return (long) FluidFuelRecipe.FUEL_AMOUNT * ticks / total;
	}

	/**
	 * 该流体是否为有效燃料. 判定只走配方系统, 且不做缓存写入, 避免污染正在燃烧的流体的缓存.
	 */
	public boolean isFuel(FluidStack stack) {
		if (level == null || stack == null || stack.isEmpty()) {
			return false;
		}
		return FluidFuelRecipe.find(level, stack) != null;
	}

	@Nullable
	private FluidFuelRecipe findRecipe(FluidStack stack) {
		if (level == null || stack.isEmpty()) {
			return null;
		}
		if (recipeCacheValid && !recipeKey.isEmpty() && FluidStack.isSameFluidSameComponents(recipeKey, stack)) {
			return recipeValue;
		}

		recipeKey = stack.copy();
		recipeValue = FluidFuelRecipe.find(level, stack);
		recipeCacheValid = true;
		return recipeValue;
	}

	public void invalidateRecipeCache() {
		recipeCacheValid = false;
		recipeKey = FluidStack.EMPTY;
		recipeValue = null;
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		registerCapabilities(event, SlBlockEntities.FLUID_BURNER.get());
	}

	/**
	 * 给指定方块实体类型注册流体能力, 供静态注册与 KubeJS 动态设备共用.
	 */
	public static <T extends FluidBurnerBlockEntity> void registerCapabilities(RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, type, (burner, direction) -> {
			IFluidHandler handler = burner.fluidCapability;
			if (!queryLogged) {
				queryLogged = true;
				CreateLettersSteam.LOGGER.info("[SlCapabilities] fluid_burner queried direction={}, handler={}", direction, handler);
			}

			return handler;
		});
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		tag.put(NBT_TANK, fuelTank.writeToNBT(registries, new CompoundTag()));
		tag.putInt(NBT_BURN_TOTAL, burnTotalTime);
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		if (tag.contains(NBT_TANK)) {
			fuelTank.readFromNBT(registries, tag.getCompound(NBT_TANK));
		}
		burnTotalTime = tag.getInt(NBT_BURN_TOTAL);
		invalidateRecipeCache();
	}
}