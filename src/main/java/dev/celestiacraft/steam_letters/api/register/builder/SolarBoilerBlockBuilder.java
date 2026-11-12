package dev.celestiacraft.steam_letters.api.register.builder;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import dev.celestiacraft.steam_letters.api.machine.SteamEngineSpec;
import dev.celestiacraft.steam_letters.common.block.boiler.SolarBoilerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 太阳能锅炉的注册 Builder.
 */
public class SolarBoilerBlockBuilder<T extends SolarBoilerBlock, P> extends SteamSpecBlockBuilder<T, P> {
	private SolarBoilerBlockBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullBiFunction<BlockBehaviour.Properties, SteamEngineSpec, T> factory) {
		super(owner, parent, name, callback, new SpecParts(), factory);
	}

	/**
	 * 建立一个太阳能锅炉 Builder, 并挂上 Registrate 的默认方块状态, 掉落表与语言文件.
	 */
	public static <T extends SolarBoilerBlock, P> SolarBoilerBlockBuilder<T, P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullBiFunction<BlockBehaviour.Properties, SteamEngineSpec, T> factory) {
		SolarBoilerBlockBuilder<T, P> builder = new SolarBoilerBlockBuilder<>(owner, parent, name, callback, factory);
		builder.defaultBlockstate().defaultLoot().defaultLang();
		return builder;
	}
}