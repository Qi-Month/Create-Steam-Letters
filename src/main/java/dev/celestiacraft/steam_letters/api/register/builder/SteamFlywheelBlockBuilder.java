package dev.celestiacraft.steam_letters.api.register.builder;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.api.machine.SteamFlywheelSpec;
import dev.celestiacraft.steam_letters.common.block.flywheel.SteamFlywheelBlock;
import dev.celestiacraft.steam_letters.common.block.flywheel.SteamFlywheelBlockEntity;
import dev.celestiacraft.steam_letters.common.register.SlBlockEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public class SteamFlywheelBlockBuilder<T extends SteamFlywheelBlock, P> extends BlockBuilder<T, P> {
	private final SteamSpecParts parts;

	protected SteamFlywheelBlockBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, SteamSpecParts parts, NonNullBiFunction<BlockBehaviour.Properties, SteamFlywheelSpec, T> factory) {
		super(owner, parent, name, callback, (properties) -> {
			return factory.apply(properties, parts.build());
		}, () -> {
			return BlockBehaviour.Properties.of();
		});
		this.parts = parts;
	}

	public static <T extends SteamFlywheelBlock, P> SteamFlywheelBlockBuilder<T, P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullBiFunction<BlockBehaviour.Properties, SteamFlywheelSpec, T> factory) {
		SteamFlywheelBlockBuilder<T, P> builder = new SteamFlywheelBlockBuilder<>(owner, parent, name, callback, new SteamSpecParts(), factory);
		builder.defaultBlockstate().defaultLoot().defaultLang();
		return builder;
	}

	/**
	 * 飞轮的材质名, 决定贴图与方块模型.
	 */
	public SteamFlywheelBlockBuilder<T, P> material(ResourceLocation material) {
		parts.material = material;
		return this;
	}

	/**
	 * 替换方块实体类型, 不调用时回退到内置的飞轮类型.
	 */
	public SteamFlywheelBlockBuilder<T, P> blockEntityType(Supplier<BlockEntityType<? extends SteamFlywheelBlockEntity>> blockEntityType) {
		parts.blockEntityType = blockEntityType;
		return this;
	}

	public static class SteamSpecParts {
		public ResourceLocation material = SlApis.loadResource("bronze");
		public Supplier<BlockEntityType<? extends SteamFlywheelBlockEntity>> blockEntityType = () -> {
			return SlBlockEntities.STEAM_FLYWHEEL.get();
		};

		public SteamFlywheelSpec build() {
			return new SteamFlywheelSpec(material, blockEntityType);
		}
	}
}