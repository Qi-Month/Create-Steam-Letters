package dev.celestiacraft.steam_letters.api.machine.heat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * 热源接口 —— 由燃烧室实现.
 * <p>
 * 锅炉不维护任何重复的热量数据, 只通过 {@link Level#getBlockEntity(BlockPos)}
 * 读取正下方燃烧室的当前状态. 只有一格位移, 因此不走 Capability 通道.
 */
public interface IHeatSource {
	/** 当前热量, 刻度与 {@link #getMaxHeat()} 一致. */
	double getHeat();

	/** 热量上限, 由注册参数 maxHeat 决定. */
	double getMaxHeat();

	/** 是否正在消耗燃料. 燃料耗尽后仍可能有残余热量, 但此时不再算作燃烧中. */
	boolean isBurningFuel();

	/**
	 * 热源当前是否处于过热状态.
	 * <p>
	 * 进入过热状态需要同时满足两个条件: 当前这份燃料是过热燃料, 且热量已经越过
	 * {@link #getMaxHeat()}. 热量低于上限时, 即便烧的是过热燃料也仍算普通状态.
	 * <p>
	 * 锅炉据此决定产普通蒸汽还是过热蒸汽. 非燃烧室热源不具备过热能力, 默认 false.
	 */
	default boolean isSuperheated() {
		return false;
	}

	/** 当前热量占上限的比例, 已钳位到 [0, 1]. 上限非法时返回 0. */
	default double getHeatRatio() {
		double max = getMaxHeat();
		if (max <= 0.0D) {
			return 0.0D;
		}
		return Math.clamp(getHeat() / max, 0.0D, 1.0D);
	}
}