package dev.celestiacraft.steam_letters.mixin.boiler;

import com.simibubi.create.content.fluids.tank.BoilerData;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import dev.celestiacraft.steam_letters.common.boiler.SteamBoilerData;
import dev.celestiacraft.steam_letters.common.boiler.SteamBoilerFluidHandler;
import dev.celestiacraft.steam_letters.config.common.BoilerSteamConfig;
import dev.celestiacraft.steam_letters.config.common.SteamConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 让 Create 大锅炉接受蒸汽驱动.
 * <ul>
 * <li>锅炉的流体处理器除原版的水之外还接受蒸汽与过热蒸汽, </li>
 * <li>灌入的蒸汽存进锅炉自己的缓冲, 缓冲里有蒸汽时锅炉热量被强制设为 9(普通蒸汽)或 18(过热蒸汽),
 * 于是接在上面的蒸汽引擎按这个等级运行, 不再看锅炉下方有没有热源, </li>
 * <li>蒸汽从缓冲里被引擎与青铜蒸汽泵抽走, 抽空后等级退回 0. </li>
 * </ul>
 * <p>
 * 锅炉本身不产汽: 水只走 Create 原本的供水采样, 蒸汽一律来自外部注入.
 * <p>
 * 目标成员都属于 Create, 重新混淆后名称不变, 因此统一使用 {@code remap = false}.
 */
@Mixin(BoilerData.class)
public class BoilerDataMixin implements SteamBoilerData {
	/**
	 * 缓冲为空
	 */
	@Unique
	private static final int SL_STEAM_NONE = 0;
	/**
	 * 缓冲中为普通蒸汽
	 */
	@Unique
	private static final int SL_STEAM_NORMAL = 1;
	/**
	 * 缓冲中为过热蒸汽
	 */
	@Unique
	private static final int SL_STEAM_SUPERHEATED = 2;
	/**
	 * Create 的等级上限, 也就是过热蒸汽对应的等级
	 */
	@Unique
	private static final double SL_MAX_LEVEL = 18.0D;

	/**
	 * 0 = 没有蒸汽
	 * 9 = 普通蒸汽
	 * 18 = 过热蒸汽
	 */
	@Unique
	private int sl$steamHeatLevel;
	/**
	 * 注入进来的蒸汽存量(mB)
	 */
	@Unique
	private int sl$steamBuffer;
	/**
	 * 缓冲中的蒸汽类型, 取值见 SL_STEAM_*
	 */
	@Unique
	private int sl$steamBufferType;
	/**
	 * 缓冲容量(mB), 按结构方块数算出
	 */
	@Unique
	private int sl$steamCapacity;

	@Shadow(remap = false)
	public boolean needsHeatLevelUpdate;

	@Shadow(remap = false)
	public boolean passiveHeat;

	@Shadow(remap = false)
	public int activeHeat;

	/**
	 * @author
	 * @reason
	 */
	@Overwrite(remap = false)
	public BoilerData.BoilerFluidHandler createHandler() {
		return new SteamBoilerFluidHandler((BoilerData) (Object) this);
	}

	/**
	 * 在原版底部热量扫描完成后, 只要缓冲里还有蒸汽, 就用蒸汽等级覆盖原版热量
	 *
	 * @param entity
	 * @param returnable
	 */
	@Inject(method = "updateTemperature", at = @At("RETURN"), remap = false, cancellable = true)
	private void sl$applySteamHeat(FluidTankBlockEntity entity, CallbackInfoReturnable<Boolean> returnable) {
		if (sl$steamHeatLevel <= 0) {
			return;
		}

		int prevActive = activeHeat;
		boolean prevPassive = passiveHeat;
		activeHeat = sl$steamHeatLevel;
		passiveHeat = false;

		if (prevActive != activeHeat || prevPassive != passiveHeat) {
			returnable.setReturnValue(true);
		}
	}

	/**
	 * 结构尺寸确定后重算缓冲容量, 让从没接过设备的锅炉也能判定注入上限
	 *
	 * @param controller
	 * @param returnable
	 */
	@Inject(method = "evaluate", at = @At("RETURN"), remap = false)
	private void sl$updateSteamCapacity(FluidTankBlockEntity controller, CallbackInfoReturnable<Boolean> returnable) {
		sl$steamCapacity = sl$steamCapacity(Math.max(controller.getTotalTankSize(), 0));
		sl$steamBuffer = Math.min(sl$steamBuffer, sl$steamCapacity);
	}

	/**
	 * 靠注入蒸汽运行时锅炉也必须是活的, 否则不会扫描热量, 等级设不上
	 *
	 * @param returnable
	 */
	@Inject(method = "isActive", at = @At("RETURN"), remap = false, cancellable = true)
	private void sl$keepActiveForSteam(CallbackInfoReturnable<Boolean> returnable) {
		if (returnable.getReturnValue()) {
			return;
		}

		if (sl$steamHeatLevel > 0) {
			returnable.setReturnValue(true);
		}
	}

	/**
	 * 每 tick 末尾维护蒸汽缓冲: 容量跟着结构走, 抽空后等级归零
	 *
	 * @param controller
	 * @param info
	 */
	@Inject(method = "tick", at = @At("TAIL"), remap = false)
	private void sl$tickSteamBuffer(FluidTankBlockEntity controller, CallbackInfo info) {
		if (controller.getLevel() == null || controller.getLevel().isClientSide()) {
			return;
		}

		int boilerSize = Math.max(controller.getTotalTankSize(), 0);
		sl$steamCapacity = sl$steamCapacity(boilerSize);

		if (sl$steamBuffer > sl$steamCapacity) {
			sl$steamBuffer = sl$steamCapacity;
		}

		sl$updateSteamHeat();

		// 等级只在 updateTemperature 里被覆盖, 而结构重算会先把它清成 0,
		// 所以热量与缓冲不符时补一次重算标记
		if (sl$steamHeatLevel > 0 && activeHeat != sl$steamHeatLevel) {
			needsHeatLevelUpdate = true;
		}
	}

	/**
	 * 缓冲见底就退回没有蒸汽的状态
	 */
	@Unique
	private void sl$updateSteamHeat() {
		if (sl$steamHeatLevel <= 0) {
			return;
		}

		if (sl$steamBuffer > 0) {
			return;
		}

		sl$steamBuffer = 0;
		sl$steamBufferType = SL_STEAM_NONE;
		sl$steamHeatLevel = 0;
		needsHeatLevelUpdate = true;
	}

	/**
	 * @param boilerSize 结构方块数
	 * @return 蒸汽缓冲容量(mB)
	 */
	@Unique
	private int sl$steamCapacity(int boilerSize) {
		double multiplier = BoilerSteamConfig.BUFFER_MULTIPLIER.get();
		return (int) Math.max(0.0D, boilerSize * 1000.0D * multiplier);
	}

	/**
	 * 靠蒸汽运行时没有供水这回事, 把 Create 的供水等级顶满, 免得它把 activeHeat 压成 0
	 *
	 * @param returnable
	 */
	@Inject(method = "getMaxHeatLevelForWaterSupply", at = @At("RETURN"), remap = false, cancellable = true)
	private void sl$supportWaterHeatLevel(CallbackInfoReturnable<Integer> returnable) {
		if (sl$steamHeatLevel > 0) {
			returnable.setReturnValue((int) SL_MAX_LEVEL);
		}
	}

	@Inject(method = "write", at = @At("RETURN"), remap = false)
	private void sl$writeSteam(CallbackInfoReturnable<CompoundTag> returnable) {
		CompoundTag nbt = returnable.getReturnValue();
		nbt.putInt("SL_SteamHeat", sl$steamHeatLevel);
		nbt.putInt("SL_SteamBuffer", sl$steamBuffer);
		nbt.putInt("SL_SteamBufferType", sl$steamBufferType);
	}

	@Inject(method = "read", at = @At("RETURN"), remap = false)
	private void sl$readSteam(CompoundTag nbt, int boilerSize, CallbackInfo info) {
		sl$steamHeatLevel = nbt.getInt("SL_SteamHeat");
		sl$steamBuffer = nbt.getInt("SL_SteamBuffer");
		sl$steamBufferType = nbt.getInt("SL_SteamBufferType");
		sl$steamCapacity = sl$steamCapacity(Math.max(boilerSize, 0));
		sl$steamBuffer = Math.min(sl$steamBuffer, sl$steamCapacity);
	}

	/**
	 * 结构散架后缓冲里的蒸汽一并消失, 不能留在一个已经不存在的锅炉上
	 *
	 * @param info
	 */
	@Inject(method = "clear", at = @At("RETURN"), remap = false)
	private void sl$clearSteam(CallbackInfo info) {
		sl$steamHeatLevel = 0;
		sl$steamBuffer = 0;
		sl$steamBufferType = SL_STEAM_NONE;
		sl$steamCapacity = 0;
	}

	@Override
	public int sl$notifySteamInput(int level, int amount) {
		if (level <= 0 || amount <= 0 || sl$steamCapacity <= 0) {
			return 0;
		}

		int type = level >= (int) SL_MAX_LEVEL ? SL_STEAM_SUPERHEATED : SL_STEAM_NORMAL;

		// 缓冲同一时刻只装一种蒸汽, 类型不符时先把它抽空再换
		if (sl$steamBuffer > 0 && sl$steamBufferType != type) {
			return 0;
		}

		int room = sl$steamCapacity - sl$steamBuffer;

		if (room <= 0) {
			return 0;
		}

		boolean wasEmpty = sl$steamBuffer <= 0;
		int accepted = Math.min(amount, room);
		sl$steamBuffer += accepted;
		sl$steamBufferType = type;

		// 缓冲刚为空时等级跟随这次灌入的蒸汽, 否则只许抬高
		if (wasEmpty || level > sl$steamHeatLevel) {
			sl$steamHeatLevel = level;
			needsHeatLevelUpdate = true;
		}

		return accepted;
	}

	@Override
	public FluidStack sl$peekSteam() {
		if (sl$steamBuffer <= 0) {
			return FluidStack.EMPTY;
		}

		Fluid fluid = sl$steamBufferType == SL_STEAM_SUPERHEATED
				? SteamConfig.getOutputSuperheatedSteamFluid()
				: SteamConfig.getOutputSteamFluid();

		return fluid == null
				? FluidStack.EMPTY
				: new FluidStack(fluid, sl$steamBuffer);
	}

	@Override
	public int sl$drainSteam(int maxAmount, boolean simulate) {
		if (maxAmount <= 0 || sl$steamBuffer <= 0) {
			return 0;
		}

		int drained = Math.min(maxAmount, sl$steamBuffer);

		if (!simulate) {
			sl$steamBuffer -= drained;

			if (sl$steamBuffer == 0) {
				sl$steamBuffer = 0;
				sl$steamBufferType = SL_STEAM_NONE;
			}
		}

		return drained;
	}

	@Override
	public int sl$getSteamCapacity() {
		return sl$steamCapacity;
	}

	@Override
	public boolean sl$isSteamBufferFull() {
		return sl$steamCapacity > 0 && sl$steamBuffer >= sl$steamCapacity;
	}
}