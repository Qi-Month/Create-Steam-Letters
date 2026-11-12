package dev.celestiacraft.steam_letters.mixin.boiler;

import com.simibubi.create.content.fluids.tank.BoilerData;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import dev.celestiacraft.steam_letters.api.SlTranKeys;
import dev.celestiacraft.steam_letters.client.lang.SlLang;
import dev.celestiacraft.steam_letters.common.boiler.BoilerPumpScanner;
import dev.celestiacraft.steam_letters.common.boiler.SteamBoilerData;
import dev.celestiacraft.steam_letters.common.boiler.SteamBoilerFluidHandler;
import dev.celestiacraft.steam_letters.config.common.BoilerSteamConfig;
import dev.celestiacraft.steam_letters.config.common.SteamConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Locale;

/**
 * 使 Create 锅炉能够使用蒸汽运行:
 * <ul>
 * <li>锅炉的流体处理器除了原版的水之外, 还接受蒸汽(以及过热蒸汽)流体, </li>
 * <li>蒸汽输入会为锅炉提供"水"供应量(与水相同的消耗计算方式: 每级 10 mB/t),
 * 因此无需额外提供水, </li>
 * <li>在持续输入蒸汽时, 锅炉的热量等级会被强制设为 9(普通蒸汽)或 18(过热蒸汽),
 * 而不是读取锅炉下方的热源, 因此无需烈焰人燃烧室 / 营火, </li>
 * <li>护目镜面板会在原版信息的基础上显示蒸汽输入速率. </li>
 * </ul>
 * <p>
 * 此外还让锅炉自己产汽: 水与热源进入后按连续等级折算成蒸汽存进虚拟缓冲,
 * 引擎直抽与青铜蒸汽泵抽出都从同一个缓冲取汽, 泵同时占用与引擎相同的名额.
 * 完整推导见仓库内 {@code docs/大锅炉产汽算法.md}.
 * <p>
 * 所有目标均为 Create 自身的成员, 这些成员在重新混淆(reobfuscation)后仍会保留其名称,
 * 因此必须使用 {@code remap = false}(Create 是第三方模组, 不属于 Minecraft / Forge
 * 映射表的一部分).
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
	 * Create 的等级上限, 同时也是产汽等级的上限
	 */
	@Unique
	private static final double SL_MAX_LEVEL = 18.0D;
	/**
	 * Create 在被动热源(营火一类)下给每台引擎的基础效率, 与 BoilerData 内部常量保持一致
	 */
	@Unique
	private static final float SL_PASSIVE_EFFICIENCY = 0.125F;

	/**
	 * 0 = 没有蒸汽
	 * 9 = 普通蒸汽
	 * 18 = 过热蒸汽
	 */
	@Unique
	private int sl$steamHeatLevel;
	@Unique
	private int sl$steamGathered;

	/**
	 * 蒸汽输入速率(mB/t), 以与原版水供应相同的方式进行采样
	 */
	@Unique
	private float sl$steamSupply;
	@Unique
	private float[] sl$steamSupplyOverTime = new float[10];
	@Unique
	private int sl$steamSupplyIndex;

	/**
	 * 水罐存量(mB).
	 * <p>
	 * 锅炉激活后 Create 不存水, 水一流进来就被计进采样并消耗掉, 于是蒸汽缓冲灌满时
	 * 只能靠拒收来止住浪费, 而拒收又会让供水采样归零, 等级跟着塌掉. 存一份水在这里,
	 * 产汽时按需扣, 既不浪费水也不会让等级塌掉.
	 */
	@Unique
	private int sl$waterStored;
	/**
	 * 水罐容量(mB), 由结构尺寸算出并缓存, 供流体处理器判定是否注满
	 */
	@Unique
	private int sl$waterCapacity;
	/**
	 * 自产蒸汽的缓冲存量(mB)
	 */
	@Unique
	private int sl$steamBuffer;
	/**
	 * 缓冲中的蒸汽类型, 取值见 SL_STEAM_*
	 */
	@Unique
	private int sl$steamBufferType;
	/**
	 * 缓冲容量(mB), 由结构尺寸算出并缓存, 供流体处理器判定是否灌满
	 */
	@Unique
	private int sl$steamCapacity;
	/**
	 * 贴在锅炉上的青铜蒸汽泵数量, 与引擎一起占用名额
	 */
	@Unique
	private int sl$attachedPumps;
	/**
	 * 结构方块数的缓存, 供不持有控制器的场合(如设备侧查询名额)使用
	 */
	@Unique
	private int sl$boilerSize;

	@Shadow(remap = false)
	public boolean needsHeatLevelUpdate;

	@Shadow(remap = false)
	public boolean passiveHeat;

	@Shadow(remap = false)
	public int activeHeat;

	@Shadow(remap = false)
	public int attachedEngines;

	@Shadow(remap = false)
	public int attachedWhistles;

	@Shadow(remap = false)
	public float waterSupply;

	@Shadow(remap = false)
	private int maxHeatForWater;

	@Shadow(remap = false)
	private int maxHeatForSize;

	@Final
	@Shadow(remap = false)
	private static int waterSupplyPerLevel;

	@Final
	@Shadow(remap = false)
	static int SAMPLE_RATE;

	/**
	 * @author
	 * @reason
	 */
	@Overwrite(remap = false)
	public BoilerData.BoilerFluidHandler createHandler() {
		return new SteamBoilerFluidHandler((BoilerData) (Object) this);
	}

	/**
	 * 在原版底部热量扫描完成后, 只要当前有蒸汽供应, 就使用蒸汽热量覆盖原版热量
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
	 * 在每个供应量采样点, 以与原版水供应相同的方式采样蒸汽输入速率.
	 * <p>
	 * 这里只统计速率, 不参与热源判定: 蒸汽热量由缓冲存量支撑, 供汽中断后靠存量继续产热,
	 * 存量烧完才归零, 因此不能在采样窗口内没有输入时立刻把等级拉塌.
	 *
	 * @param entity
	 * @param info
	 */
	@Inject(
			method = "tick",
			at = @At(
					value = "FIELD",
					target = "Lcom/simibubi/create/content/fluids/tank/BoilerData;gatheredSupply:I",
					opcode = Opcodes.PUTFIELD
			),
			remap = false
	)
	private void sl$sampleSteamSupply(FluidTankBlockEntity entity, CallbackInfo info) {
		sl$steamSupplyOverTime[sl$steamSupplyIndex] = sl$steamGathered / (float) SAMPLE_RATE;
		sl$steamSupply = Math.max(sl$steamSupply, sl$steamSupplyOverTime[sl$steamSupplyIndex]);
		sl$steamSupplyIndex = (sl$steamSupplyIndex + 1) % sl$steamSupplyOverTime.length;

		if (sl$steamSupplyIndex == 0) {
			sl$steamSupply = 0;

			for (float sample : sl$steamSupplyOverTime) {
				sl$steamSupply = Math.max(sample, sl$steamSupply);
			}
		}

		sl$steamGathered = 0;
	}

	/**
	 * 在原版的结构扫描之后统计贴锅炉的青铜蒸汽泵.
	 * <p>
	 * 判定方式与 Create 统计蒸汽引擎完全一致: 从结构内的每个锅炉方块朝六个方向看,
	 * 该方向上是一个朝向背离锅炉的青铜泵, 也就是泵的背面(抽取侧)冲着锅炉.
	 * <p>
	 * 泵数变化同样算作结构变化, 需要把返回值顶成 true 让锅炉重新评估.
	 *
	 * @param controller
	 * @param returnable
	 */
	@Inject(method = "evaluate", at = @At("RETURN"), remap = false, cancellable = true)
	private void sl$evaluatePumps(FluidTankBlockEntity controller, CallbackInfoReturnable<Boolean> returnable) {
		// 结构尺寸一确定就把两个罐的容量算出来: 从没贴过引擎的锅炉不会 tick,
		// 若等到 tick 才算, 之前灌进来的蒸汽会被当成缓冲容量为 0 而拒收
		sl$boilerSize = Math.max(controller.getTotalTankSize(), 0);

		if (sl$boilerSize > 0) {
			sl$steamCapacity = sl$steamCapacity(sl$boilerSize);
			sl$waterCapacity = sl$waterCapacity(sl$boilerSize);
			sl$steamBuffer = Math.min(sl$steamBuffer, sl$steamCapacity);
			sl$waterStored = Math.min(sl$waterStored, sl$waterCapacity);
		}

		int pumps = sl$countPumps(controller);

		if (pumps == sl$attachedPumps) {
			return;
		}

		sl$attachedPumps = pumps;
		returnable.setReturnValue(true);
	}

	/**
	 * 统计贴锅炉的青铜蒸汽泵数量.
	 * <p>
	 * 之所以不只依赖 evaluate: Create 只在锅炉自身方块变化与蒸汽引擎的放置/破坏时调用
	 * {@code FluidTankBlock.updateBoilerState}, 往锅炉旁边放一台泵不会走到那里, 泵数就会一直停在 0,
	 * 名额也就摊不开. 因此 tick 里也要按节拍重扫一次.
	 *
	 * @param controller
	 * @return 贴在本锅炉上的青铜泵数量
	 */
	@Unique
	private int sl$countPumps(FluidTankBlockEntity controller) {
		return BoilerPumpScanner.count(controller);
	}

	@Override
	public void sl$setAttachedPumps(int pumps) {
		sl$attachedPumps = Math.max(pumps, 0);
	}

	/**
	 * 只挂泵不挂引擎时锅炉也必须是活的, 否则不会扫描热量与采样供水, 产汽无从谈起
	 *
	 * @param returnable
	 */
	@Inject(method = "isActive", at = @At("RETURN"), remap = false, cancellable = true)
	private void sl$keepActiveForPumps(CallbackInfoReturnable<Boolean> returnable) {
		if (returnable.getReturnValue()) {
			return;
		}

		// 只挂泵或正在靠灌入的蒸汽运行时锅炉也必须是活的, 否则不会跑 tick, 容量不会更新, 退出判定也不会生效
		if (sl$attachedPumps > 0 || sl$steamHeatLevel > 0) {
			returnable.setReturnValue(true);
		}
	}

	/**
	 * 每 tick 末尾结算产汽.
	 * <p>
	 * 挂在原版 tick 的尾部, 与 Create 的采样节拍共用同一个入口, 不引入独立 ticker.
	 * 本方法只在锅炉处于激活状态时被调用.
	 *
	 * @param controller
	 * @param info
	 */
	@Inject(method = "tick", at = @At("TAIL"), remap = false)
	private void sl$tickSteamProduction(FluidTankBlockEntity controller, CallbackInfo info) {
		Level level = controller.getLevel();

		if (level == null || level.isClientSide()) {
			return;
		}

		int boilerSize = controller.getTotalTankSize();
		sl$boilerSize = Math.max(boilerSize, 0);

		if (boilerSize <= 0) {
			sl$steamBuffer = 0;
			sl$waterStored = 0;
			sl$steamBufferType = SL_STEAM_NONE;
			sl$steamCapacity = 0;
			sl$waterCapacity = 0;
			return;
		}

		sl$steamCapacity = sl$steamCapacity(boilerSize);
		sl$waterCapacity = sl$waterCapacity(boilerSize);

		if (sl$steamBuffer > sl$steamCapacity) {
			sl$steamBuffer = sl$steamCapacity;
		}

		if (sl$waterStored > sl$waterCapacity) {
			sl$waterStored = sl$waterCapacity;
		}

		sl$updateSteamHeat();

		// activeHeat 只在 updateTemperature 里被覆盖, 结构重算会先把它清成 0,
		// 而重算只在 needsHeatLevelUpdate 时发生, 所以这里有偏差就补一次标记
		if (sl$steamHeatLevel > 0 && activeHeat != sl$steamHeatLevel) {
			needsHeatLevelUpdate = true;
		}

		sl$produceSteam(boilerSize);
	}

	/**
	 * 蒸汽输入模式靠缓冲存量续命, 存量见底才退回自产汽模式.
	 * <p>
	 * 输入蒸汽直接存进缓冲, 由接在上面的蒸汽引擎与青铜蒸汽泵抽走, 因此中断供汽后锅炉
	 * 仍能继续输出, 直到存量被抽空为止; 存量还在时等级不衰减.
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
	 * 按连续等级结算本 tick 的产汽, 水从锅炉自己的水罐里扣
	 *
	 * @param boilerSize 结构方块数
	 */
	@Unique
	private void sl$produceSteam(int boilerSize) {
		// 蒸汽输入模式: 锅炉只当热源与水源的中转, 不产汽也不扣水
		if (sl$steamHeatLevel > 0) {
			return;
		}

		int room = sl$steamCapacity - sl$steamBuffer;

		if (room <= 0 || sl$waterStored <= 0) {
			return;
		}

		double boilerLevel = sl$boilerLevel(boilerSize);

		if (boilerLevel <= 0.0D) {
			return;
		}

		boolean superheated = boilerLevel >= BoilerSteamConfig.SUPERHEATED_AT_LEVEL.get();
		int steamType = superheated ? SL_STEAM_SUPERHEATED : SL_STEAM_NORMAL;

		// 缓冲同一时刻只装一种蒸汽, 类型要变就得先把旧蒸汽抽空
		if (sl$steamBuffer > 0 && sl$steamBufferType != steamType) {
			return;
		}

		double waterMultiplier = superheated ? BoilerSteamConfig.SUPERHEATED_WATER_MULTIPLIER.get() : 1.0D;
		double steamPerWater = BoilerSteamConfig.STEAM_PER_WATER.get();
		double produced = boilerLevel * BoilerSteamConfig.BASE_CONSUMPTION.get();

		if (produced <= 0.0D || steamPerWater <= 0.0D) {
			return;
		}

		// 水罐里的水撑不起满产时按库存等比缩小, 免得产出的蒸汽多于烧掉的水
		double waterPerSteam = waterMultiplier / steamPerWater;
		int amount = Math.min(room, Mth.floor(Math.min(produced / waterMultiplier, sl$waterStored / waterPerSteam)));

		if (amount <= 0) {
			return;
		}

		sl$waterStored = Math.max(0, sl$waterStored - Mth.ceil(amount * waterPerSteam));
		sl$steamBuffer += amount;
		sl$steamBufferType = steamType;
	}

	/**
	 * 连续锅炉等级: 被热源, 体积, 供水三者夹住, 再截到 18.
	 * <p>
	 * 与 Create 的整数等级相比不取整, 因此体积 6 的锅炉得到 1.5 级而不是 1 级.
	 *
	 * @param boilerSize 结构方块数
	 * @return 连续等级, 同时等价于"能喂饱多少台满额引擎"
	 */
	@Unique
	private double sl$boilerLevel(int boilerSize) {
		// 供水这一项已经并进 activeHeat: 水罐见底时 getMaxHeatLevelForWaterSupply 归零,
		// updateTemperature 随之把 activeHeat 拉成 0, 这里不必再判一次水
		double level = Math.min(activeHeat, boilerSize / 4.0D);

		return Math.min(level, SL_MAX_LEVEL);
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
	 * @param boilerSize 结构方块数
	 * @return 水罐容量(mB)
	 */
	@Unique
	private int sl$waterCapacity(int boilerSize) {
		return (int) Math.max(0.0D, boilerSize * (double) BoilerSteamConfig.WATER_PER_BLOCK.get());
	}

	/**
	 * 把 Create 的供水等级顶满, 免得它把 activeHeat 拉下去.
	 * <p>
	 * 自产汽模式靠水罐里的存量: 水罐满之后管网会停注, 若仍按流入量计供水, 等级会在 50 tick 内塌到 0,
	 * 接入的引擎与泵就连缓冲里的存量蒸汽都用不上, 形成死锁.
	 * <p>
	 * 蒸汽输入模式没有供水这回事: 等级由缓冲里的蒸汽存量支撑, 而蒸汽不走 Create 的槽位,
	 * 它的流入量不会进供水采样. 若不在这里一并顶满, getActualHeat 会把等级压成 0, 引擎直接停摆.
	 *
	 * @param returnable
	 */
	@Inject(method = "getMaxHeatLevelForWaterSupply", at = @At("RETURN"), remap = false, cancellable = true)
	private void sl$supportWaterHeatLevel(CallbackInfoReturnable<Integer> returnable) {
		if (sl$steamHeatLevel > 0 || (sl$waterCapacity > 0 && sl$waterStored > 0)) {
			returnable.setReturnValue((int) SL_MAX_LEVEL);
		}
	}

	/**
	 * 在原版护目镜信息末尾追加一行蒸汽产率.
	 * <p>
	 * 蒸汽输入模式下锅炉不产汽, 此时保持原版面板不变.
	 *
	 * @param tooltip
	 * @param isPlayerSneaking
	 * @param boilerSize
	 * @param returnable
	 */
	@Inject(method = "addToGoggleTooltip", at = @At("RETURN"), remap = false)
	private void sl$addSteamProductionTooltip(List<Component> tooltip, boolean isPlayerSneaking, int boilerSize, CallbackInfoReturnable<Boolean> returnable) {
		if (sl$steamHeatLevel > 0) {
			return;
		}

		if (attachedEngines + attachedWhistles + sl$attachedPumps <= 0) {
			return;
		}

		double production = sl$boilerLevel(boilerSize) * BoilerSteamConfig.BASE_CONSUMPTION.get();

		SlLang.translate(SlTranKeys.GOGGLE_BOILER_STEAM_OUTPUT_RATE)
				.style(ChatFormatting.GRAY)
				.text(ChatFormatting.DARK_GRAY, ": ")
				.add(Component.literal(String.format(Locale.ROOT, "%.1f", production) + " mB/t")
						.withStyle(production > 0.0D ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY))
				.forGoggles(tooltip, 1);

		// 容量现场按体积算, 客户端不必等同步到 sl$waterCapacity 才有读数
		int waterCapacity = sl$waterCapacity(boilerSize);

		if (waterCapacity > 0) {
			SlLang.translate(SlTranKeys.GOGGLE_BOILER_WATER_STORED)
					.style(ChatFormatting.GRAY)
					.text(ChatFormatting.DARK_GRAY, ": ")
					.add(Component.literal(sl$waterStored + " / " + waterCapacity + " mB")
							.withStyle(sl$waterStored > 0 ? ChatFormatting.BLUE : ChatFormatting.DARK_GRAY))
					.forGoggles(tooltip, 1);
		}
	}

	/**
	 * 名额: 把 Create 引擎的效率改成显式份额, 让蒸汽泵与引擎平分名额.
	 * <p>
	 * Create 的蒸汽引擎不实际消耗流体, 应力上限完全由这里的效率决定, 所以只有覆写效率
	 * 才能让「名额满后加装蒸汽泵压低引擎输出」成立.
	 *
	 * @param boilerSize
	 * @param returnable
	 */
	@Inject(method = "getEngineEfficiency", at = @At("RETURN"), remap = false, cancellable = true)
	private void sl$applyStrictQuota(int boilerSize, CallbackInfoReturnable<Float> returnable) {
		if (sl$steamHeatLevel > 0 || !BoilerSteamConfig.STRICT_QUOTA.get()) {
			return;
		}

		int devices = attachedEngines + sl$attachedPumps;

		if (devices <= 0) {
			return;
		}

		// 被动热源(营火一类)的效率上限远低于 1, 名额摊薄只该按设备数分摊它, 不能把这块补回去
		if (passiveHeat) {
			returnable.setReturnValue(SL_PASSIVE_EFFICIENCY / devices);
			return;
		}

		returnable.setReturnValue((float) Math.min(1.0D, sl$boilerLevel(boilerSize) / devices));
	}

	@Inject(method = "write", at = @At("RETURN"), remap = false)
	private void sl$writeSteam(CallbackInfoReturnable<CompoundTag> returnable) {
		CompoundTag nbt = returnable.getReturnValue();
		nbt.putInt("SL_SteamHeat", sl$steamHeatLevel);
		nbt.putFloat("SL_SteamSupply", sl$steamSupply);
		nbt.putInt("SL_SteamBuffer", sl$steamBuffer);
		nbt.putInt("SL_SteamBufferType", sl$steamBufferType);
		nbt.putInt("SL_AttachedPumps", sl$attachedPumps);
		nbt.putInt("SL_WaterStored", sl$waterStored);
	}

	@Inject(method = "read", at = @At("RETURN"), remap = false)
	private void sl$readSteam(CompoundTag nbt, int boilerSize, CallbackInfo info) {
		sl$boilerSize = boilerSize;
		sl$steamHeatLevel = nbt.getInt("SL_SteamHeat");
		sl$steamSupply = nbt.getFloat("SL_SteamSupply");
		sl$steamBuffer = nbt.getInt("SL_SteamBuffer");
		sl$steamBufferType = nbt.getInt("SL_SteamBufferType");
		sl$attachedPumps = nbt.getInt("SL_AttachedPumps");
		sl$waterStored = nbt.getInt("SL_WaterStored");
		sl$steamCapacity = boilerSize > 0 ? sl$steamCapacity(boilerSize) : 0;
		sl$waterCapacity = boilerSize > 0 ? sl$waterCapacity(boilerSize) : 0;

		if (sl$steamBuffer > sl$steamCapacity) {
			sl$steamBuffer = sl$steamCapacity;
		}

		if (sl$waterStored > sl$waterCapacity) {
			sl$waterStored = sl$waterCapacity;
		}
	}

	/**
	 * 结构散架后缓冲区里的蒸汽一并消失, 不能让它们凭空留在一个已经不存在的锅炉上
	 *
	 * @param info
	 */
	@Inject(method = "clear", at = @At("RETURN"), remap = false)
	private void sl$clearSteam(CallbackInfo info) {
		sl$steamBuffer = 0;
		sl$steamBufferType = SL_STEAM_NONE;
		sl$steamCapacity = 0;
		sl$attachedPumps = 0;
		sl$boilerSize = 0;
		sl$waterStored = 0;
		sl$waterCapacity = 0;
	}

	@Override
	public int sl$notifySteamInput(int level, int amount) {
		if (level <= 0 || amount <= 0 || sl$steamCapacity <= 0) {
			return 0;
		}

		int type = level >= (int) SL_MAX_LEVEL ? SL_STEAM_SUPERHEATED : SL_STEAM_NORMAL;

		// 缓冲同一时刻只装一种蒸汽, 类型不符时先把它烧空再换
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
		sl$steamGathered += accepted;

		// 缓冲刚为空时等级跟随这次灌入的蒸汽, 否则只许抬高
		if (wasEmpty || level > sl$steamHeatLevel) {
			sl$steamHeatLevel = level;
			needsHeatLevelUpdate = true;
		}

		return accepted;
	}

	@Override
	public int sl$fillWater(int amount) {
		if (amount <= 0 || sl$waterCapacity <= 0) {
			return 0;
		}

		int accepted = Math.min(amount, sl$waterCapacity - sl$waterStored);

		if (accepted > 0) {
			sl$waterStored += accepted;
		}
		return accepted;
	}

	@Override
	public int sl$getWaterStored() {
		return sl$waterStored;
	}

	@Override
	public int sl$getWaterCapacity() {
		return sl$waterCapacity;
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

	@Override
	public int sl$getAttachedPumps() {
		return sl$attachedPumps;
	}

	@Override
	public float sl$getQuotaShare() {
		int devices = attachedEngines + sl$attachedPumps;

		if (devices <= 0) {
			return 1.0F;
		}

		return (float) Math.min(1.0D, sl$boilerLevel(sl$boilerSize) / devices);
	}
}