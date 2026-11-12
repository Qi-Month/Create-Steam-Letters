package dev.celestiacraft.steam_letters.api;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import dev.celestiacraft.steam_letters.CreateLettersSteam;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.createmod.catnip.config.ConfigBase;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleSupplier;

public class SlStress extends ConfigBase {
	private static final Object2DoubleMap<ResourceLocation> DEFAULT_IMPACTS = new Object2DoubleOpenHashMap<>();
	private static final Object2DoubleMap<ResourceLocation> DEFAULT_CAPACITIES = new Object2DoubleOpenHashMap<>();

	protected final Map<ResourceLocation, ModConfigSpec.ConfigValue<Double>> capacities = new HashMap<>();
	protected final Map<ResourceLocation, ModConfigSpec.ConfigValue<Double>> impacts = new HashMap<>();

	private static final ModConfigSpec.Builder CONFIG_BUILDER = new ModConfigSpec.Builder();

	public static Pair<SlStress, ModConfigSpec> createConfig() {
		Pair<SlStress, ModConfigSpec> pair = CONFIG_BUILDER.configure((builder) -> {
			SlStress stress = new SlStress();
			stress.registerAll(builder);
			return stress;
		});
		pair.getLeft().specification = pair.getRight();
		return pair;
	}

	@Override
	public void registerAll(ModConfigSpec.Builder builder) {
		builder.comment(".", Comments.su, Comments.impact)
				.push("impact");
		DEFAULT_IMPACTS.forEach((id, value) -> {
			impacts.put(id, builder.define(id.getPath(), value));
		});
		builder.pop();

		builder.comment(".", Comments.su, Comments.capacity)
				.push("capacity");
		DEFAULT_CAPACITIES.forEach((id, value) -> {
			capacities.put(id, builder.define(id.getPath(), value));
		});
		builder.pop();
	}

	@Override
	public @NotNull String getName() {
		return "stressValues";
	}

	@Nullable
	public DoubleSupplier getImpact(Block block) {
		ModConfigSpec.ConfigValue<Double> value = impacts.get(RegisteredObjectsHelper.getKeyOrThrow(block));
		return value == null ? null : value::get;
	}

	@Nullable
	public DoubleSupplier getCapacity(Block block) {
		ModConfigSpec.ConfigValue<Double> value = capacities.get(RegisteredObjectsHelper.getKeyOrThrow(block));
		return value == null ? null : value::get;
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setNoImpact() {
		return setImpact(0);
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setImpact(double value) {
		return (builder) -> {
			assertFromCreateD2D(builder);
			DEFAULT_IMPACTS.put(SlApis.loadResource(builder.getName()), value);
			return builder;
		};
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setCapacity(double value) {
		return (builder) -> {
			assertFromCreateD2D(builder);
			DEFAULT_CAPACITIES.put(SlApis.loadResource(builder.getName()), value);
			return builder;
		};
	}

	private static void assertFromCreateD2D(BlockBuilder<?, ?> builder) {
		if (!builder.getOwner().getModid().equals(CreateLettersSteam.MODID)) {
			String name = CreateLettersSteam.CLASS_NAME;
			throw new IllegalStateException("Non-%s blocks cannot be added to %s config."
					.formatted(name, name));
		}
	}

	private static class Comments {
		static String su = "[in Stress Units]";
		static String impact = "Configure the individual stress impact of mechanical blocks. Note that this cost is doubled for every speed increase it receives.";
		static String capacity = "Configure how much stress a source can accommodate for.";
	}
}