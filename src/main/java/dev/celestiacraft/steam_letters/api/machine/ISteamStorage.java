package dev.celestiacraft.steam_letters.api.machine;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.celestiacraft.steam_letters.api.SlTranKeys;
import dev.celestiacraft.steam_letters.api.machine.capability.SteamFluidHandler;
import dev.celestiacraft.steam_letters.client.lang.SlLang;
import dev.celestiacraft.steam_letters.config.common.SteamConfig;
import dev.celestiacraft.steam_letters.tags.SlFluidTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

/**
 * 通用蒸汽机器接口: 储存, 填充, 抽取, 消耗.
 *
 * <p>
 * 继承关系由机器决定: 实现类 {@code extends} 机械动力的对应 BE 类并实现本接口, 例如
 * {@code SteamMixerBlockEntity extends MechanicalMixerBlockEntity implements ISteamBasinMachine}.
 *
 * <p>
 * 接口无法持有实例状态, 实现类需持有 {@link FluidStack} 字段并实现 {@link #getSteamStack()} 与
 * {@link #setSteamStack(FluidStack)}.
 *
 * <p>
 * {@code getCapability} / {@code write} / {@code read} 在父类中已是具体方法, 接口的默认实现会被忽略
 * , 流体能力与持久化由实现类接入.
 */
public interface ISteamStorage {
	/**
	 * 当前蒸汽储量, 无蒸汽时返回 {@link FluidStack#EMPTY}.
	 */
	FluidStack getSteamStack();

	/**
	 * 写入蒸汽储量, 实现类需同时调用 {@code setChanged()}.
	 */
	void setSteamStack(FluidStack stack);

	/**
	 * 蒸汽容量, 单位 mB.
	 */
	int getSteamCapacity();

	/**
	 * 单次加工的蒸汽消耗量, 单位 mB, 以普通蒸汽计.
	 */
	int getSteamConsumption();

	/**
	 * 一次操作可处理的份数上限, 默认 1, 与机械动力原版相同.
	 *
	 * <p>
	 * 返回 0 或负数表示停止工作, 可接入 {@link #hasEnoughSteam()} 等条件.
	 */
	default int getParallelCount() {
		return 1;
	}

	/**
	 * 当前可用的并行数, 蒸汽不足时为 0.
	 */
	default int getParallel() {
		return hasEnoughSteam() ? getParallelCount() : 0;
	}

	/**
	 * 本次操作实际处理的份数, {@code available} 为可用输入份数, 0 表示不处理.
	 */
	default int getBatchSize(int available) {
		int parallel = getParallel();
		if (parallel <= 0 || available <= 0) {
			return 0;
		}
		return Math.min(parallel, available);
	}

	/**
	 * 可接受的流体, 默认仅{@link SlFluidTags#STEAM}和{@link SlFluidTags#SUPERHEATED_STEAM}.
	 */
	default boolean isValidSteam(FluidStack stack) {
		Fluid fluid = stack.getFluid();
		return !stack.isEmpty() && (fluid.defaultFluidState().is(SlFluidTags.STEAM) || fluid.defaultFluidState().is(SlFluidTags.SUPERHEATED_STEAM));
	}

	/**
	 * 是否为过热蒸汽.
	 */
	default boolean isSuperheatedSteam(FluidStack stack) {
		return !stack.isEmpty() && stack.getFluid().defaultFluidState().is(SlFluidTags.SUPERHEATED_STEAM);
	}

	/**
	 * 过热蒸汽的消耗倍数, 默认取自配置 {@code general.steam.superheated_steam_consumption_multiplier}.
	 */
	default double getSuperheatedSteamConsumptionMultiplier() {
		return SteamConfig.SUPERHEATED_STEAM_CONSUMPTION_MULTIPLIER.get();
	}

	/**
	 * 当前储液对应的单次加工消耗量, 过热蒸汽按 {@link #getSuperheatedSteamConsumptionMultiplier()} 折算.
	 */
	default int getCurrentSteamConsumption() {
		int consumption = getSteamConsumption();
		if (consumption <= 0 || !isSuperheatedSteam(getSteamStack())) {
			return consumption;
		}

		return (int) Math.max(1, Math.round(consumption * getSuperheatedSteamConsumptionMultiplier()));
	}

	/**
	 * 储量是否不少于 {@code amount}.
	 */
	default boolean hasSteam(int amount) {
		FluidStack steam = getSteamStack();
		return !steam.isEmpty() && steam.getAmount() >= amount;
	}

	/**
	 * 储量是否足够一次加工.
	 */
	default boolean hasEnoughSteam() {
		return hasSteam(getCurrentSteamConsumption());
	}

	/**
	 * 当前储量可支撑的加工次数.
	 */
	default int getSteamOperations() {
		FluidStack steam = getSteamStack();
		int consumption = getCurrentSteamConsumption();
		return steam.isEmpty() || consumption <= 0 ? 0 : steam.getAmount() / consumption;
	}

	/**
	 * 扣除蒸汽内部存储的蒸汽
	 *
	 * @param amount 扣除的量
	 */
	default void consumeSteam(int amount) {
		FluidStack steam = getSteamStack();
		if (steam.isEmpty() || amount <= 0) {
			return;
		}
		if (steam.getAmount() <= amount) {
			setSteamStack(FluidStack.EMPTY);
			return;
		}
		FluidStack left = steam.copy();
		left.shrink(amount);
		setSteamStack(left);
	}

	/**
	 * 扣除蒸汽内部存储的蒸汽
	 */
	default boolean consumeSteam() {
		if (!hasEnoughSteam()) {
			return false;
		}
		consumeSteam(getCurrentSteamConsumption());
		return true;
	}

	default int fillSteam(FluidStack stack, IFluidHandler.FluidAction action) {
		if (!isValidSteam(stack)) {
			return 0;
		}

		FluidStack steam = getSteamStack();
		if (!steam.isEmpty() && !FluidStack.isSameFluidSameComponents(steam, stack)) {
			return 0;
		}

		int accepted = Math.clamp(getSteamCapacity() - steam.getAmount(), 0, stack.getAmount());
		if (accepted <= 0) {
			return 0;
		}
		if (action.simulate()) {
			return accepted;
		}

		if (steam.isEmpty()) {
			setSteamStack(new FluidStack(stack.getFluid(), accepted));
		} else {
			FluidStack merged = steam.copy();
			merged.grow(accepted);
			setSteamStack(merged);
		}
		return accepted;
	}

	/**
	 * 抽取蒸汽.
	 *
	 * @return 实际抽出的蒸汽
	 */
	default FluidStack drainSteam(int maxDrain, IFluidHandler.FluidAction action) {
		FluidStack steam = getSteamStack();

		if (steam.isEmpty() || maxDrain <= 0) {
			return FluidStack.EMPTY;
		}

		int drained = Math.min(maxDrain, steam.getAmount());
		FluidStack result = new FluidStack(steam.getFluid(), drained);
		if (!action.simulate()) {
			consumeSteam(drained);
		}
		return result;
	}

	default IFluidHandler steamFluidHandler() {
		return new SteamFluidHandler(this);
	}

	/**
	 * 蒸汽量跨过 250 mB 档位时同步给客户端, 供护目镜显示.
	 */
	default void notifySteamChanged(int previousAmount) {
		if (!(this instanceof SmartBlockEntity blockEntity)) {
			return;
		}

		Level level = blockEntity.getLevel();
		if (level == null || level.isClientSide()) {
			return;
		}

		if (previousAmount / 250 != getSteamStack().getAmount() / 250) {
			blockEntity.sendData();
		}
	}

	/**
	 * 向护目镜追加蒸汽储量, 可加工次数与并行数.
	 */
	default void appendSteamGoggle(List<Component> tooltip) {
		SlLang.translate(SlTranKeys.GOGGLE_STEAM_MACHINE_STEAM)
				.style(ChatFormatting.GRAY)
				.text(ChatFormatting.DARK_GRAY, ": ")
				.add(Component.literal(getSteamStack().getAmount() + " / " + getSteamCapacity() + " mB")
						.withStyle(ChatFormatting.AQUA))
				.forGoggles(tooltip, 1);

		SlLang.translate(SlTranKeys.GOGGLE_STEAM_MACHINE_OPERATIONS)
				.style(ChatFormatting.GRAY)
				.text(ChatFormatting.DARK_GRAY, ": ")
				.add(Component.literal(String.valueOf(getSteamOperations()))
						.withStyle(getSteamOperations() > 0 ? ChatFormatting.GOLD : ChatFormatting.DARK_GRAY))
				.forGoggles(tooltip, 1);

		SlLang.translate(SlTranKeys.GOGGLE_STEAM_MACHINE_PARALLEL)
				.style(ChatFormatting.GRAY)
				.text(ChatFormatting.DARK_GRAY, ": ")
				.add(Component.literal(String.valueOf(getParallelCount()))
						.withStyle(ChatFormatting.AQUA))
				.forGoggles(tooltip, 1);
	}
}