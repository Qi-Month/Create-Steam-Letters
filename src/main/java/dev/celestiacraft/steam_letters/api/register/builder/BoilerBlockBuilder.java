package dev.celestiacraft.steam_letters.api.register.builder;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import dev.celestiacraft.steam_letters.api.machine.SteamEngineSpec;
import dev.celestiacraft.steam_letters.common.block.boiler.SteamBoilerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 蒸汽锅炉的注册 Builder.
 */
public class BoilerBlockBuilder<T extends SteamBoilerBlock, P> extends SteamSpecBlockBuilder<T, P> {
	private BoilerBlockBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullBiFunction<BlockBehaviour.Properties, SteamEngineSpec, T> factory) {
		super(owner, parent, name, callback, new SpecParts(), factory);
	}

	/**
	 * 建立一个锅炉 Builder, 并挂上 Registrate 的默认方块状态, 掉落表与语言文件.
	 */
	public static <T extends SteamBoilerBlock, P> BoilerBlockBuilder<T, P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullBiFunction<BlockBehaviour.Properties, SteamEngineSpec, T> factory) {
		BoilerBlockBuilder<T, P> builder = new BoilerBlockBuilder<>(owner, parent, name, callback, factory);
		builder.defaultBlockstate().defaultLoot().defaultLang();
		return builder;
	}
}