package dev.celestiacraft.steam_letters.common.boiler;

import dev.celestiacraft.steam_letters.mixin.boiler.BoilerDataMixin;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * {@link BoilerDataMixin} 与蒸汽流体处理器之间的桥接接口.
 * <p>
 * 由 {@link BoilerDataMixin} 实现, 供 {@link SteamBoilerFluidHandler} 把注入的蒸汽交回锅炉,
 * 并把锅炉持有的蒸汽缓冲暴露给引擎与泵.
 * <p>
 * 原版大锅炉不再自己产汽: 它只在收到外部蒸汽时抬升热量等级, 并把收到的蒸汽存在缓冲里等设备抽走.
 */
public interface SteamBoilerData {
	/**
	 * 把外部灌入的蒸汽存进锅炉的蒸汽缓冲.
	 * <p>
	 * 蒸汽输入模式以缓冲里的蒸汽为热源, 等级由它支撑; 中断供汽后锅炉靠存量继续运行, 抽空才降级.
	 *
	 * @param level  普通蒸汽为 9, 过热蒸汽为 18
	 * @param amount 本次送达的蒸汽流体量(mB)
	 * @return 实际进入缓冲的量(mB), 缓冲满或与缓冲中的蒸汽类型冲突时为 0
	 */
	int sl$notifySteamInput(int level, int amount);

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
	 * @return 缓冲是否已满. 满时外部蒸汽注入会被拒收
	 */
	boolean sl$isSteamBufferFull();
}