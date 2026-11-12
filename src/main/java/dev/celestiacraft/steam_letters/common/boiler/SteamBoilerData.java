package dev.celestiacraft.steam_letters.common.boiler;

import dev.celestiacraft.steam_letters.mixin.boiler.BoilerDataMixin;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * {@link BoilerDataMixin}与蒸汽流体处理器之间的桥接接口
 * <p>
 * 由{@link BoilerDataMixin}实现, 供蒸汽流体处理器将蒸汽 / 水的输入信息回传给锅炉,
 * 并把锅炉自产的蒸汽缓冲暴露给 {@link SteamBoilerFluidHandler}.
 */
public interface SteamBoilerData {
	/**
	 * 把外部灌入的蒸汽存进锅炉的蒸汽缓冲.
	 * <p>
	 * 蒸汽输入模式以缓冲里的蒸汽为热源, 等级由它支撑; 中断供汽后锅炉靠存量继续运行,
	 * 抽空才退回自产汽模式.
	 *
	 * @param level  普通蒸汽为 9, 过热蒸汽为 18
	 * @param amount 本次送达的蒸汽流体量(mB)
	 * @return 实际进入缓冲的量(mB), 缓冲满或与缓冲中的蒸汽类型冲突时为 0
	 */
	int sl$notifySteamInput(int level, int amount);

	/**
	 * 往锅炉的水罐里注水.
	 * <p>
	 * 与蒸汽不同, 水在锅炉里是有存量的: 注进来的水先存着, 产汽时按需扣,
	 * 因此中断注水不会立刻让等级塌掉.
	 *
	 * @param amount 请求注入的水量(mB)
	 * @return 实际接受的水量(mB)
	 */
	int sl$fillWater(int amount);

	/**
	 * @return 水罐当前存量(mB)
	 */
	int sl$getWaterStored();

	/**
	 * @return 水罐容量(mB), 0 表示没有水罐
	 */
	int sl$getWaterCapacity();

	/**
	 * @return 缓冲中当前的蒸汽, 空缓冲时为空堆
	 */
	FluidStack sl$peekSteam();

	/**
	 * 从缓冲中抽走蒸汽, 供引擎直抽与青铜蒸汽泵对外供汽使用.
	 *
	 * @param maxAmount 本次最多抽走多少(mB)
	 * @param simulate  为 true 时只计算不扣除
	 * @return 实际(或模拟)抽走的量
	 */
	int sl$drainSteam(int maxAmount, boolean simulate);

	/**
	 * @return 蒸汽缓冲容量(mB)
	 */
	int sl$getSteamCapacity();

	/**
	 * @return 缓冲是否已满. 满时锅炉停止产汽, 水继续存进罐里直到罐满
	 */
	boolean sl$isSteamBufferFull();

	/**
	 * @return 贴在本锅炉上的青铜蒸汽泵数量, 与蒸汽引擎一起占用名额
	 */
	int sl$getAttachedPumps();

	/**
	 * 覆写贴在本锅炉上的青铜蒸汽泵数量.
	 * <p>
	 * Create 只在锅炉方块变化与蒸汽引擎放置 / 破坏时重新评估锅炉结构, 往旁边放一台泵不会触发,
	 * 因此需要由外部按节拍补扫后写回.
	 *
	 * @param pumps 重新统计出的泵数量
	 */
	void sl$setAttachedPumps(int pumps);

	/**
	 * 本锅炉当前分给单个设备的名额份额, 取值 1.0 表示名额未满.
	 * <p>
	 * 蒸汽引擎与青铜蒸汽泵共用同一份名额, 因此分母是引擎数与泵数之和;
	 * 份额为 {@code min(1, 连续等级 / 设备数)}, 与 {@code getEngineEfficiency} 同一口径.
	 *
	 * @return 单个设备能拿到的份额(0~1)
	 */
	float sl$getQuotaShare();
}