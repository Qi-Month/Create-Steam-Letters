package dev.celestiacraft.steam_letters.api.machine;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

public class SteamEngineDriveSpec {
	private final DoubleSupplier strength;
	private final DoubleSupplier speed;
	private final DoubleSupplier consumption;
	private final IntSupplier capacity;
	@Getter
	private final ResourceLocation material;

	private SteamEngineDriveSpec(DoubleSupplier strength, DoubleSupplier speed, DoubleSupplier consumption, IntSupplier capacity, ResourceLocation material) {
		this.strength = strength;
		this.speed = speed;
		this.consumption = consumption;
		this.capacity = capacity;
		this.material = material;
	}

	/**
	 * 普通蒸汽满负荷时的应力输出 (SU). 过热蒸汽在此基础上再乘过热输出倍率.
	 */
	public double getStrength() {
		return strength.getAsDouble();
	}

	/**
	 * 输出转速 (RPM). 从预热第一 tick 起就是该值, 不随预热进度变化.
	 */
	public double getSpeed() {
		return speed.getAsDouble();
	}

	/**
	 * 普通蒸汽满负荷时每 tick 消耗的蒸汽量 (mB/tick).
	 */
	public double getConsumption() {
		return consumption.getAsDouble();
	}

	/**
	 * 引擎自带蒸汽罐的容量 (mB).
	 */
	public int getCapacity() {
		return capacity.getAsInt();
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * 用一段配置代码直接建出 spec, 调用方不需要再手动写 build().
	 */
	public static SteamEngineDriveSpec of(Consumer<Builder> configurer) {
		Builder builder = builder();
		configurer.accept(builder);
		return builder.build();
	}

	public static class Builder {
		private DoubleSupplier strength = () -> 0.0D;
		private DoubleSupplier speed = () -> 0.0D;
		private DoubleSupplier consumption = () -> 0.0D;
		private IntSupplier capacity = () -> 0;
		private ResourceLocation material;

		public Builder strength(double value) {
			return strength(() -> value);
		}

		public Builder strength(DoubleSupplier supplier) {
			strength = supplier;
			return this;
		}

		public Builder speed(double speed) {
			return speed(() -> speed);
		}

		public Builder speed(DoubleSupplier supplier) {
			speed = supplier;
			return this;
		}

		public Builder consumption(double consumption) {
			return consumption(() -> consumption);
		}

		public Builder consumption(DoubleSupplier supplier) {
			consumption = supplier;
			return this;
		}

		public Builder capacity(int capacity) {
			return capacity(() -> capacity);
		}

		public Builder capacity(IntSupplier supplier) {
			capacity = supplier;
			return this;
		}

		/**
		 * 引擎的材质 id, 需与飞轮的材质一致才能配对.
		 */
		public Builder material(ResourceLocation material) {
			this.material = material;
			return this;
		}

		public SteamEngineDriveSpec build() {
			return new SteamEngineDriveSpec(strength, speed, consumption, capacity, material);
		}
	}
}