package dev.celestiacraft.steam_letters.api.register.builder;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import dev.celestiacraft.steam_letters.api.machine.SteamEngineSpec;
import dev.celestiacraft.steam_letters.config.common.SteamBurnerConfig;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

/**
 * 带 {@link SteamEngineSpec} 参数的方块注册骨架.
 * <p>
 * Registrate 的 {@link BlockBuilder} 把方块工厂与初始属性都收在私有字段里, 子类无法改写,
 * 所以这里在构造时就把它包成「属性 -> 带 spec 的方块」, spec 由注册期收集到的参数现建.
 * <p>
 * 这些参数全部是 {@link IntSupplier} / {@link DoubleSupplier}, 值到运行时才读, 与配置文件加载顺序无关.
 */
public abstract class SteamSpecBlockBuilder<T extends Block, P> extends BlockBuilder<T, P> {
	private final SpecParts parts;

	protected SteamSpecBlockBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, SpecParts parts, NonNullBiFunction<BlockBehaviour.Properties, SteamEngineSpec, T> factory) {
		super(owner, parent, name, callback, (properties) -> {
			return factory.apply(properties, parts.build());
		}, () -> {
			return BlockBehaviour.Properties.of();
		});
		this.parts = parts;
	}

	/**
	 * 单个流体罐的容量, 单位 mB.
	 */
	public SteamSpecBlockBuilder<T, P> capacity(int capacity) {
		parts.capacity = () -> capacity;
		return this;
	}

	public SteamSpecBlockBuilder<T, P> capacity(IntSupplier capacity) {
		parts.capacity = capacity;
		return this;
	}

	/**
	 * 100% 效率下每 tick 的蒸汽产出量.
	 */
	public SteamSpecBlockBuilder<T, P> production(double production) {
		parts.production = () -> production;
		return this;
	}

	public SteamSpecBlockBuilder<T, P> production(DoubleSupplier production) {
		parts.production = production;
		return this;
	}

	/**
	 * 热量上限.
	 */
	public SteamSpecBlockBuilder<T, P> maxHeat(double maxHeat) {
		parts.maxHeat = () -> maxHeat;
		return this;
	}

	public SteamSpecBlockBuilder<T, P> maxHeat(DoubleSupplier maxHeat) {
		parts.maxHeat = maxHeat;
		return this;
	}

	/**
	 * 温度上限, 不设置时取配置项 steam_burner.max_temperature.
	 * <p>
	 * 本设计并不作用于蒸汽锅炉. 当前尚未添加实际用途
	 * <p>
	 * 本设计类似一个"燃烧温度"的设计, 可由其他设备读取, 为其提供恒温加热效果
	 */
	public SteamSpecBlockBuilder<T, P> temperature(double temperature) {
		parts.temperature = () -> temperature;
		return this;
	}

	public SteamSpecBlockBuilder<T, P> temperature(DoubleSupplier temperature) {
		parts.temperature = temperature;
		return this;
	}

	/**
	 * 注册期收集 spec 参数的容器.
	 */
	public static class SpecParts {
		private IntSupplier capacity = () -> 0;
		private DoubleSupplier production = () -> 0.0D;
		private DoubleSupplier maxHeat = () -> 0.0D;
		private DoubleSupplier temperature = () -> SteamBurnerConfig.MAX_TEMPERATURE.get();

		private SteamEngineSpec build() {
			return new SteamEngineSpec(capacity, production, maxHeat, temperature);
		}
	}
}