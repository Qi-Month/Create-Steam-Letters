package dev.celestiacraft.steam_letters.api.machine;

import dev.celestiacraft.steam_letters.config.common.SteamBurnerConfig;
import lombok.AllArgsConstructor;

import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

/**
 * 蒸汽设备在注册时写入的机制参数.
 * <p>
 * 全部用 {@link IntSupplier} / {@link DoubleSupplier} 承载, 而不是裸数值: 内置设备的数值来自配置文件,
 * 而配置在方块注册时尚未加载, 因此只保存取值方式, 真正读取发生在运行时.
 * KubeJS 等外部注册方则直接闭包成常量.
 * <p>
 * 数值为 0 表示该设备不使用这项参数 (例如固体燃烧室没有流体罐).
 * 参数的写入由注册侧的 Builder 负责, 本类只承载已经确定下来的取值方式.
 */
@AllArgsConstructor
public class SteamEngineSpec {
	private final IntSupplier capacity;
	private final DoubleSupplier production;
	private final DoubleSupplier maxHeat;
	private final DoubleSupplier maxTemperature;

	/**
	 * 温度上限未指定时取配置项 {@code steam_burner.max_temperature}.
	 */
	public SteamEngineSpec(IntSupplier capacity, DoubleSupplier production, DoubleSupplier maxHeat) {
		this(capacity, production, maxHeat, () -> SteamBurnerConfig.MAX_TEMPERATURE.get());
	}

	public SteamEngineSpec(int capacity, double production, double maxHeat) {
		this(() -> capacity, () -> production, () -> maxHeat);
	}

	/**
	 * @return 单个流体罐的容量 (mB), 0 表示该设备不含流体罐
	 */
	public int getCapacity() {
		return Math.max(0, capacity.getAsInt());
	}

	/**
	 * @return 100% 效率下每 tick 的基础蒸汽产出量 (mB/tick), 0 表示该设备不产汽
	 */
	public double getProduction() {
		return Math.max(0.0D, production.getAsDouble());
	}

	/**
	 * @return 热量上限, 0 表示该设备不产生热量
	 */
	public double getMaxHeat() {
		return Math.max(0.0D, maxHeat.getAsDouble());
	}

	/**
	 * @return 温度上限, 0 表示该设备不记录温度
	 */
	public double getMaxTemperature() {
		return Math.max(0.0D, maxTemperature.getAsDouble());
	}
}