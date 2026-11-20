package dev.celestiacraft.steam_letters.api.register.builder;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import dev.celestiacraft.steam_letters.api.machine.SteamEngineSpec;
import dev.celestiacraft.steam_letters.common.block.boiler.SteamBoilerBlock;
import dev.celestiacraft.steam_letters.common.block.boiler.SteamBoilerBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.DoubleSupplier;

/**
 * 蒸汽锅炉的注册 Builder.
 */
public class BoilerBlockBuilder<T extends SteamBoilerBlock, P> extends SteamSpecBlockBuilder<T, P> {
	private BoilerBlockBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullBiFunction<BlockBehaviour.Properties, SteamEngineSpec, T> factory) {
		super(owner, parent, name, callback, new SpecParts(), factory);
	}

	/**
	 * 大锅炉成型后每 1 级的蒸汽产量, 实际产量再乘以当前等级.
	 * <p>
	 * 只作用于多方块模式, 单方块仍读 {@link SteamSpecBlockBuilder#production(double)}.
	 * <p>
	 * 不写时沿用 bronze 的 {@link SteamBoilerBlockEntity#DEFAULT_PRODUCTION}.
	 * <p>
	 * 必须写在 {@code boiler(...)} 之后、{@code capacity(...)} 一类方法之前: 那些方法返回的是基类类型.
	 */
	public BoilerBlockBuilder<T, P> largeProduction(double production) {
		parts.largeProduction = () -> production;
		return this;
	}

	public BoilerBlockBuilder<T, P> largeProduction(DoubleSupplier production) {
		parts.largeProduction = production;
		return this;
	}

	/**
	 * 建立一个锅炉 Builder, 并挂上 Registrate 的默认方块状态, 掉落表与语言文件.
	 */
	public static <T extends SteamBoilerBlock, P> BoilerBlockBuilder<T, P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullBiFunction<BlockBehaviour.Properties, SteamEngineSpec, T> factory) {
		BoilerBlockBuilder<T, P> builder = new BoilerBlockBuilder<>(owner, parent, name, callback, factory);
		builder.parts.largeProduction = () -> SteamBoilerBlockEntity.DEFAULT_PRODUCTION;
		builder.defaultBlockstate().defaultLoot().defaultLang();
		return builder;
	}
}