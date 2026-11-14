package dev.celestiacraft.steam_letters.common.block.boiler;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.celestiacraft.steam_letters.api.machine.ISteamEngineBlock;
import dev.celestiacraft.steam_letters.api.machine.SteamEngineSpec;
import dev.celestiacraft.steam_letters.api.machine.heat.IHeatSource;
import dev.celestiacraft.steam_letters.common.register.SlBlockEntities;
import dev.celestiacraft.steam_letters.config.common.SteamBoilerConfig;
import dev.celestiacraft.steam_letters.config.common.SteamConfig;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.List;

public abstract class AbstractBoilerBlockEntity extends SmartBlockEntity {
	public static final int TANK_INPUT = 0;
	public static final int TANK_OUTPUT = 1;

	private static final String NBT_INPUT_TANK = "InputTank";
	private static final String NBT_OUTPUT_TANK = "OutputTank";
	private static final String NBT_PENDING = "Pending";
	private static final String NBT_PRODUCING_SUPERHEATED = "ProducingSuperheated";

	@Getter
	protected final SteamEngineSpec spec;
	@Getter
	private final BoilerFluidTank inputTank;
	@Getter
	private final BoilerFluidTank outputTank;
	protected IFluidHandler fluidCapability;

	private double pending;

	/**
	 * 上次同步给客户端的两个罐的存量, 用于按档位触发同步.
	 */
	private int syncedInput;
	private int syncedOutput;

	/**
	 * 上一次 tick 是否在产过热蒸汽, 用来识别普通 / 过热产出的切换.
	 */
	private boolean producingSuperheated;

	protected AbstractBoilerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		spec = ((ISteamEngineBlock) state.getBlock()).getEngineSpec();
		int capacity = spec.getCapacity();

		inputTank = new BoilerFluidTank(this, capacity, BoilerFluidTank::isWater);
		outputTank = new BoilerFluidTank(this, capacity, this::isOutputSteam);

		fluidCapability = createFluidHandler();
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	@Override
	public void tick() {
		super.tick();
		if (level == null || level.isClientSide()) {
			return;
		}
		tickProduction();
		syncClientFluid();
	}

	/**
	 * 两个罐的存量跨过 250 mB 档位时同步给客户端, 供锅炉护目镜显示.
	 */
	private void syncClientFluid() {
		int input = inputTank.getFluidAmount();
		int output = outputTank.getFluidAmount();
		if (input / 250 == syncedInput / 250 && output / 250 == syncedOutput / 250) {
			return;
		}

		syncedInput = input;
		syncedOutput = output;
		sendData();
	}

	private void tickProduction() {
		double efficiency = getCurrentEfficiency();
		double production = spec.getProduction();

		boolean superheated = efficiency > 0.0D && production > 0.0D && isSuperheated();
		if (superheated != producingSuperheated) {
			// 普通产出与过热产出不共享余额, 切换时从 0 重新累计
			producingSuperheated = superheated;
			pending = 0.0D;
			setChanged();
		}

		if (efficiency <= 0.0D || production <= 0.0D) {
			pending = 0.0D;
			return;
		}

		if (superheated) {
			// 输出罐是单流体容器, 装着普通蒸汽时填不进过热蒸汽, 先让它消失
			clearNormalSteamFromOutput();
		}

		double produced = production * efficiency;
		if (superheated) {
			// 过热蒸汽价值更高, 因此产率按热量在过热区间里的位置打折
			produced *= SteamBoilerConfig.getSuperheatedProductionMultiplier(getRawHeatPercent());
		}
		pending += produced;

		int requested = (int) Math.floor(pending);
		if (requested <= 0) {
			return;
		}

		FluidStack steam = createOutputSteam(requested, superheated);
		if (steam.isEmpty()) {
			pending = 0.0D;
			return;
		}

		// 先模拟, 只有水够且输出罐真的放得下时才会执行, 因此永远不会多消耗水
		FluidStack drained = inputTank.drain(requested, IFluidHandler.FluidAction.SIMULATE);
		int accepted = outputTank.fill(steam, IFluidHandler.FluidAction.SIMULATE);
		int producible = Math.min(drained.getAmount(), accepted);

		if (producible <= 0) {
			if (outputHoldsOtherSteam(superheated)) {
				// 输出罐里压在另一种蒸汽上, 既灌不进去也抽不出来, 锅炉会彻底停摆到玩家把罐子抽空.
				// 停摆期间水照常烧掉但什么都不产出, 这是过热生产的代价; 罐里那种蒸汽本身原样保留.
				int wasted = drained.getAmount();
				if (wasted > 0) {
					inputTank.drain(wasted, IFluidHandler.FluidAction.EXECUTE);
				}
				pending = 0.0D;
				return;
			}
			// 被水或空间卡住: 余额只保留一个 tick 的量, 避免长时间停机后突然爆发
			pending = Math.min(pending, produced);
			return;
		}

		FluidStack used = inputTank.drain(producible, IFluidHandler.FluidAction.EXECUTE);
		outputTank.fill(createOutputSteam(used.getAmount(), superheated), IFluidHandler.FluidAction.EXECUTE);
		pending -= used.getAmount();
	}

	/**
	 * 输出罐里是否正压着「另一种」蒸汽: 产普通蒸汽时罐里是过热蒸汽, 或反之.
	 * <p>
	 * 单流体罐在这种状态下既灌不进也抽不出, 生产会完全停摆, 直到玩家把罐里的蒸汽抽走.
	 * 罐空着, 或罐里正是当前该产的那种蒸汽时返回 false.
	 */
	private boolean outputHoldsOtherSteam(boolean superheatedProduction) {
		FluidStack stored = outputTank.getFluid();
		if (stored.isEmpty()) {
			return false;
		}
		Fluid expected = superheatedProduction
				? SteamConfig.getOutputSuperheatedSteamFluid()
				: SteamConfig.getOutputSteamFluid();
		return expected != null && stored.getFluid() != expected;
	}

	/**
	 * 按当前状态产出普通蒸汽或过热蒸汽.
	 */
	protected FluidStack createOutputSteam(int amount, boolean superheated) {
		return superheated ? createSuperheatedSteam(amount) : createSteam(amount);
	}

	/**
	 * 让输出罐中的普通蒸汽消失, 腾出位置装过热蒸汽.
	 * <p>
	 * 销毁还是就地转换由 {@code do_overheated_steam_replace} 决定, 两种方式都不额外消耗水;
	 * 罐里已经是过热蒸汽时不做任何事, 因此每 tick 调用是安全的.
	 */
	private void clearNormalSteamFromOutput() {
		FluidStack stored = outputTank.getFluid();
		if (stored.isEmpty()) {
			return;
		}

		Fluid superheated = SteamConfig.getOutputSuperheatedSteamFluid();
		if (superheated == null || stored.getFluid() == superheated) {
			return;
		}

		int amount = stored.getAmount();
		if (SteamConfig.DO_OVERHEATED_STEAM_REPLACE.get()) {
			outputTank.setFluid(new FluidStack(superheated, amount));
			// setFluid 不经过 onContentsChanged, 这里补一次脏标记
			setChanged();
		} else {
			outputTank.drain(amount, IFluidHandler.FluidAction.EXECUTE);
		}
	}

	/**
	 * @return 正下方燃烧室的真实热量百分比, 过热状态下会超过 100
	 */
	protected double getRawHeatPercent() {
		IHeatSource source = getHeatSource();
		if (source == null) {
			return 0.0D;
		}
		double maxHeat = source.getMaxHeat();
		if (maxHeat <= 0.0D) {
			return 0.0D;
		}
		return source.getHeat() / maxHeat * 100.0D;
	}

	/**
	 * @return 正下方燃烧室是否处于过热状态
	 */
	protected boolean isSuperheated() {
		IHeatSource source = getHeatSource();
		return source != null && source.isSuperheated();
	}

	/**
	 * @return 当前运行效率, 0 表示不产汽
	 */
	protected abstract double getCurrentEfficiency();

	protected FluidStack createSteam(int amount) {
		Fluid fluid = SteamConfig.getOutputSteamFluid();
		return fluid == null ? FluidStack.EMPTY : new FluidStack(fluid, amount);
	}

	protected FluidStack createSuperheatedSteam(int amount) {
		Fluid fluid = SteamConfig.getOutputSuperheatedSteamFluid();
		return fluid == null ? FluidStack.EMPTY : new FluidStack(fluid, amount);
	}

	protected boolean isOutputSteam(FluidStack stack) {
		if (stack.isEmpty()) {
			return false;
		}

		Fluid fluid = stack.getFluid();
		Fluid steam = SteamConfig.getOutputSteamFluid();
		if (steam != null && fluid == steam) {
			return true;
		}

		Fluid superheated = SteamConfig.getOutputSuperheatedSteamFluid();
		return superheated != null && fluid == superheated;
	}

	/**
	 * @return 正下方的有效燃烧室, 没有则返回 null
	 */
	@Nullable
	protected IHeatSource getHeatSource() {
		if (level == null) {
			return null;
		}
		BlockEntity below = level.getBlockEntity(worldPosition.below());
		return below instanceof IHeatSource source ? source : null;
	}

	protected IFluidHandler createFluidHandler() {
		return new BoilerFluidHandler(this);
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		registerCapabilities(event, SlBlockEntities.SOLAR_BOILER.get());
	}

	/**
	 * 给指定方块实体类型注册流体能力, 供静态注册与 KubeJS 动态设备共用.
	 */
	public static <T extends AbstractBoilerBlockEntity> void registerCapabilities(RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, type, (boiler, direction) -> {
			return boiler.fluidCapability;
		});
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		tag.put(NBT_INPUT_TANK, inputTank.writeToNBT(registries, new CompoundTag()));
		tag.put(NBT_OUTPUT_TANK, outputTank.writeToNBT(registries, new CompoundTag()));
		tag.putDouble(NBT_PENDING, pending);
		tag.putBoolean(NBT_PRODUCING_SUPERHEATED, producingSuperheated);
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		if (tag.contains(NBT_INPUT_TANK)) {
			inputTank.readFromNBT(registries, tag.getCompound(NBT_INPUT_TANK));
		}
		if (tag.contains(NBT_OUTPUT_TANK)) {
			outputTank.readFromNBT(registries, tag.getCompound(NBT_OUTPUT_TANK));
		}
		pending = tag.getDouble(NBT_PENDING);
		producingSuperheated = tag.getBoolean(NBT_PRODUCING_SUPERHEATED);
	}
}