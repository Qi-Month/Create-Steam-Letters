package dev.celestiacraft.steam_letters.common.block.burner;

import dev.celestiacraft.steam_letters.common.recipe.type.SolidFuelRecipe;
import dev.celestiacraft.steam_letters.tags.SlItemTags;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * 固体燃烧室的方块实体.
 * <p>
 * 燃料匹配顺序: 自定义 {@code steam_letters:solid_fuel} 配方优先, 没命中才回退到原版熔炉燃料表;
 * 两者都不匹配的物品不能作为燃料. 配方只在「点燃下一份燃料」时查一次并缓存,
 * 不会每 tick 扫描配方表.
 */
public class SolidBurnerBlockEntity extends AbstractBurnerBlockEntity {
	private static final String NBT_FUEL = "Fuel";
	/**
	 * 燃料槽容量, 允许玩家一次性囤一小堆.
	 */
	public static final int FUEL_SLOT_LIMIT = 64;

	@Getter
	@Setter
	private ItemStack fuel = ItemStack.EMPTY;

	/**
	 * 上一次解析燃烧时间用的物品与结果, {@code cacheValid} 为 false 时表示还没有可用缓存.
	 */
	private ItemStack burnTimeKey = ItemStack.EMPTY;
	private int burnTimeValue;
	private boolean burnTimeSuperheated;
	private boolean burnTimeCacheValid;

	private LazyOptional<IItemHandler> itemCapability;

	public SolidBurnerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		itemCapability = LazyOptional.of(() -> new FuelItemHandler(this));
	}

	@Override
	public void onLoad() {
		super.onLoad();
		invalidateBurnTimeCache();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		invalidateBurnTimeCache();
	}

	@Override
	protected boolean tickFuel() {
		if (getBurnTime() > 0) {
			setBurnTime(getBurnTime() - 1);
			return true;
		}

		if (fuel.isEmpty()) {
			return false;
		}

		int time = resolveBurnTime(fuel);
		if (time <= 0) {
			return false;
		}

		// 槽里正常不会躺着容器燃料: 右键与管道都在进槽之前就抽干、把空容器交还了.
		// 这里兜住旧存档里已经躺在槽里的那一件, 抽干后把空容器丢在方块上方, 不能整件吃掉.
		ItemStack container = consumeContainerFuel(fuel, 1, false);
		if (!container.isEmpty()) {
			shrinkFuel();
			if (level != null) {
				Containers.dropItemStack(level, worldPosition.getX() + 0.5D, worldPosition.getY() + 1.0D, worldPosition.getZ() + 0.5D, container);
			}
			return true;
		}

		shrinkFuel();
		setBurnTime(time);
		setSuperheatedFuel(burnTimeSuperheated);
		setChanged();
		return true;
	}

	/**
	 * 燃料槽里消耗一件.
	 */
	private void shrinkFuel() {
		fuel.shrink(1);
		if (fuel.isEmpty()) {
			fuel = ItemStack.EMPTY;
		}
	}

	/**
	 * @return 该物品的燃烧时间 (tick), 0 表示不能作为燃料
	 */
	public int resolveBurnTime(ItemStack stack) {
		if (stack.isEmpty()) {
			return 0;
		}

		if (burnTimeCacheValid && !burnTimeKey.isEmpty() && ItemStack.isSameItemSameTags(burnTimeKey, stack)) {
			return burnTimeValue;
		}

		int time = 0;
		// 标签优先于配方字段: 进了 superheated_fuel 的物品一律按过热燃料算, 原版燃料也适用
		boolean superheated = stack.is(SlItemTags.SUPERHEATED_FUEL);
		if (level != null) {
			SolidFuelRecipe recipe = SolidFuelRecipe.find(level, stack);
			if (recipe != null) {
				time = recipe.getTime();
				superheated |= recipe.isSuperheated();
			} else if (!isFluidContainer(stack)) {
				// 带流体能力的容器默认不当燃料: 能反复灌装的罐子会把熔炉燃烧值变成无限燃料,
				// 平衡性留给数据包与 KubeJS, 想放行就自己写一条 solid_fuel 配方
				time = ForgeHooks.getBurnTime(stack, RecipeType.SMELTING);
			}
		}

		burnTimeKey = stack.copy();
		burnTimeValue = time;
		burnTimeSuperheated = superheated;
		burnTimeCacheValid = true;
		return time;
	}

	/**
	 * 物品是否带流体能力. 这类容器不按原版熔炉燃料值当固体燃料, 只有 solid_fuel 配方能放行.
	 */
	private static boolean isFluidContainer(ItemStack stack) {
		return FluidUtil.getFluidHandler(stack).isPresent();
	}

	public boolean isFuel(ItemStack stack) {
		return resolveBurnTime(stack) > 0;
	}

	public void invalidateBurnTimeCache() {
		burnTimeCacheValid = false;
		burnTimeKey = ItemStack.EMPTY;
		burnTimeValue = 0;
		burnTimeSuperheated = false;
	}

	/**
	 * 抽掉容器燃料里的流体, 把这件物品自身的燃烧时间记进燃烧钟.
	 * <p>
	 * 只走物品流体能力, 不看具体物品类型: 流体从 {@link IFluidHandlerItem} 抽走, 交还的空容器取自
	 * {@link IFluidHandlerItem#getContainer()}, 因此任何模组的容器都能适配. 燃烧时间仍按物品本身判定
	 * (自定义 {@code steam_letters:solid_fuel} 配方优先, 没命中回退到原版燃料表), 与容器里装了什么流体无关.
	 *
	 * @param count    处理的件数, 多件时燃烧时间按件数累加
	 * @param simulate 只算结果, 不改动任何状态
	 * @return 数量为 count 的空容器; 这件物品不是容器燃料时返回 {@link ItemStack#EMPTY}
	 */
	public ItemStack consumeContainerFuel(ItemStack stack, int count, boolean simulate) {
		if (count <= 0 || stack.isEmpty() || !isFuel(stack)) {
			return ItemStack.EMPTY;
		}

		// 能力绑定在单件上: 整叠取能力再抽干, 拿回的空容器也只有一件
		IFluidHandlerItem handler = FluidUtil.getFluidHandler(stack.copyWithCount(1)).orElse(null);
		if (handler == null) {
			return ItemStack.EMPTY;
		}

		FluidStack stored = handler.getFluidInTank(0);
		if (stored.isEmpty()) {
			return ItemStack.EMPTY;
		}

		// 抽的只是一份副本, 外面这叠原样不动
		if (handler.drain(stored.getAmount(), IFluidHandler.FluidAction.EXECUTE).isEmpty()) {
			return ItemStack.EMPTY;
		}

		ItemStack container = handler.getContainer();
		if (container.isEmpty() || ItemStack.isSameItemSameTags(container, stack)) {
			// 抽干之后还是同一件物品, 那就是凭空续燃料
			return ItemStack.EMPTY;
		}
		if (count > container.getMaxStackSize()) {
			// 空容器只能作为一叠剩余物交还, 超过一叠的数量交不出去
			return ItemStack.EMPTY;
		}

		if (!simulate) {
			int time = resolveBurnTime(stack);
			if (getBurnTime() <= 0) {
				setSuperheatedFuel(burnTimeSuperheated);
			}
			setBurnTime(getBurnTime() + time * count);
			setChanged();
		}

		return container.copyWithCount(count);
	}

	/**
	 * 把玩家手里的物品放进燃料槽.
	 *
	 * @return 是否真的放进去了一些
	 */
	public boolean insertFuel(ItemStack stack) {
		if (stack.isEmpty() || !isFuel(stack)) {
			return false;
		}

		if (fuel.isEmpty()) {
			int moved = Math.min(FUEL_SLOT_LIMIT, stack.getCount());
			fuel = stack.copyWithCount(moved);
			stack.shrink(moved);
		} else {
			if (!ItemStack.isSameItemSameTags(fuel, stack)) {
				return false;
			}
			int space = FUEL_SLOT_LIMIT - fuel.getCount();
			if (space <= 0) {
				return false;
			}
			int moved = Math.min(space, stack.getCount());
			fuel.grow(moved);
			stack.shrink(moved);
		}

		setChanged();
		return true;
	}

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
		if (capability == ForgeCapabilities.ITEM_HANDLER) {
			return itemCapability.cast();
		}
		return super.getCapability(capability, direction);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		itemCapability.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		itemCapability = LazyOptional.of(() -> new FuelItemHandler(this));
	}

	@Override
	protected void write(CompoundTag tag, boolean clientPacket) {
		super.write(tag, clientPacket);
		if (!fuel.isEmpty()) {
			tag.put(NBT_FUEL, fuel.save(new CompoundTag()));
		}
	}

	@Override
	protected void read(CompoundTag tag, boolean clientPacket) {
		super.read(tag, clientPacket);
		fuel = tag.contains(NBT_FUEL) ? ItemStack.of(tag.getCompound(NBT_FUEL)) : ItemStack.EMPTY;
	}

	@Override
	public void destroy() {
		super.destroy();
		if (level == null || fuel.isEmpty()) {
			return;
		}

		ItemStack dropped = fuel;
		fuel = ItemStack.EMPTY;
		Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), dropped);
	}
}