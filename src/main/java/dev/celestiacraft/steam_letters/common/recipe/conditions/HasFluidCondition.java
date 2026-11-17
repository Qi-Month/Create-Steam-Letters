package dev.celestiacraft.steam_letters.common.recipe.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.Optional;

public class HasFluidCondition implements ICondition {
	public static final MapCodec<HasFluidCondition> CODEC;

	private final Optional<ResourceLocation> fluid;
	private final Optional<TagKey<Fluid>> tag;

	public HasFluidCondition(Optional<ResourceLocation> fluid, Optional<TagKey<Fluid>> tag) {
		if (fluid.isPresent() == tag.isPresent()) {
			throw new IllegalArgumentException("`has_fluid` requires exactly one of `fluid` or `tag` to be specified.");
		}

		this.fluid = fluid;
		this.tag = tag;
	}

	private HasFluidCondition(Fluid fluid) {
		this(Optional.of(BuiltInRegistries.FLUID.getKey(fluid)), Optional.empty());
	}

	private HasFluidCondition(TagKey<Fluid> tag) {
		this(Optional.empty(), Optional.of(tag));
	}

	static {
		CODEC = RecordCodecBuilder.mapCodec((instance) -> {
			return instance.group(
					ResourceLocation.CODEC.optionalFieldOf("fluid")
							.forGetter(HasFluidCondition::fluid),
					TagKey.codec(Registries.FLUID)
							.optionalFieldOf("tag")
							.forGetter(HasFluidCondition::tag)
			).apply(instance, HasFluidCondition::new);
		});
	}

	public static HasFluidCondition ofFluid(Fluid fluid) {
		return new HasFluidCondition(fluid);
	}

	public static HasFluidCondition ofFluid(ResourceLocation fluid) {
		return new HasFluidCondition(BuiltInRegistries.FLUID.get(fluid));
	}

	public static HasFluidCondition ofFluid(String fluid) {
		return new HasFluidCondition(BuiltInRegistries.FLUID.get(parse(fluid)));
	}

	public static HasFluidCondition ofTag(TagKey<Fluid> tag) {
		return new HasFluidCondition(tag);
	}

	public static HasFluidCondition ofTag(ResourceLocation tag) {
		return new HasFluidCondition(FluidTags.create(tag));
	}

	public static HasFluidCondition ofTag(String tag) {
		return new HasFluidCondition(FluidTags.create(parse(tag)));
	}

	public Optional<ResourceLocation> fluid() {
		return fluid;
	}

	public Optional<TagKey<Fluid>> tag() {
		return tag;
	}

	@Override
	public boolean test(IContext context) {
		return fluid.map(BuiltInRegistries.FLUID::containsKey)
				.orElseGet(() -> {
					return !context.getTag(tag.get()).isEmpty();
				});
	}

	@Override
	public MapCodec<? extends ICondition> codec() {
		return CODEC;
	}

	@Override
	public String toString() {
		return fluid.map((location) -> {
			return "has_fluid(\"" + location + "\")";
		}).orElseGet(() -> {
			return "has_fluid(\"" + tag.get().location() + "\")";
		});
	}

	private static ResourceLocation parse(String location) {
		ResourceLocation parsed = ResourceLocation.tryParse(location);

		if (parsed == null) {
			throw new IllegalArgumentException("Invalid resource location: " + location);
		}

		return parsed;
	}
}