package dev.celestiacraft.steam_letters.utils;

import net.minecraft.util.Mth;

/**
 * 以固定追赶速度逼近目标值的插值量, 用于客户端转动视觉.
 * <p>
 * 每 tick 调用 {@link #tick()} 推进一次, 渲染时用 {@link #get(float)} 取当前帧的插值结果.
 */
public class InterpolatedChasingValue {
	private static final float EPSILON = 2.4414062E-4F;

	private float value;
	private float lastValue;
	private float target;
	private float speed = 0.5F;

	public void tick() {
		float diff = target - value;
		if (Math.abs(diff) < EPSILON) {
			return;
		}
		lastValue = value;
		value += diff * speed;
	}

	public float get(float partialTicks) {
		return Mth.lerp(partialTicks, lastValue, value);
	}

	public float value() {
		return value;
	}

	public InterpolatedChasingValue withSpeed(float speed) {
		this.speed = speed;
		return this;
	}

	public InterpolatedChasingValue target(float target) {
		this.target = target;
		return this;
	}

	public InterpolatedChasingValue start(float value) {
		lastValue = this.value = value;
		target = value;
		return this;
	}
}