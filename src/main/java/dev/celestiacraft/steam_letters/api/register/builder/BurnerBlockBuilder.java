package dev.celestiacraft.steam_letters.api.register.builder;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import dev.celestiacraft.steam_letters.api.machine.SteamEngineSpec;
import dev.celestiacraft.steam_letters.common.block.burner.AbstractBurnerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 燃烧室的注册 Builder.
 * <p>
 * 燃烧室只用到热量上限与温度上限, 容量与产出保持默认值.
 */
public class BurnerBlockBuilder<T extends AbstractBurnerBlock, P> extends SteamSpecBlockBuilder<T, P> {
	private BurnerBlockBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullBiFunction<BlockBehaviour.Properties, SteamEngineSpec, T> factory) {
		super(owner, parent, name, callback, new SpecParts(), factory);
	}

	/**
	 * 建立一个燃烧室 Builder, 并挂上 Registrate 的默认方块状态, 掉落表与语言文件.
	 */
	public static <T extends AbstractBurnerBlock, P> BurnerBlockBuilder<T, P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullBiFunction<BlockBehaviour.Properties, SteamEngineSpec, T> factory) {
		BurnerBlockBuilder<T, P> builder = new BurnerBlockBuilder<>(owner, parent, name, callback, factory);
		builder.defaultBlockstate().defaultLoot().defaultLang();
		return builder;
	}
}