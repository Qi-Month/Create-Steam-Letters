package dev.celestiacraft.steam_letters.common.recipe.type;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.foundation.codec.CreateCodecs;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 两种燃料配方共用的参数集: 在原版的加工配方参数之上多一个「是否过热」.
 * <p>
 * 1.21 的 {@link ProcessingRecipeParams} 把原来散在 readAdditional / writeAdditional 里的字段
 * 全收进 codec, 所以过热标记只能跟着 codec 一起定义, 否则写不到配方 JSON 里.
 */
@Setter
@Getter
public class FuelRecipeParams extends ProcessingRecipeParams {
	public static final MapCodec<FuelRecipeParams> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
		return instance.group(
				Codec.either(CreateCodecs.FLAT_SIZED_FLUID_INGREDIENT_WITH_TYPE, Ingredient.CODEC).listOf()
						.fieldOf("ingredients").forGetter((params) -> {
							return params.ingredients();
						}),
				Codec.either(FluidStack.CODEC, ProcessingOutput.CODEC_NEW).listOf()
						.optionalFieldOf("results", List.of()).forGetter((params) -> {
							return params.results();
						}),
				Codec.INT.optionalFieldOf("processing_time", 0)
						.forGetter((params) -> {
							return params.processingDuration();
						}),
				HeatCondition.CODEC.optionalFieldOf("heat_requirement", HeatCondition.NONE)
						.forGetter((params) -> {
							return params.requiredHeat();
						}),
				Codec.BOOL.optionalFieldOf(IBurnerFuelRecipe.SUPERHEATED_KEY, false)
						.forGetter((params) -> {
							return params.isSuperheated();
						})
		).apply(instance, FuelRecipeParams::read);
	});

	public static final StreamCodec<RegistryFriendlyByteBuf, FuelRecipeParams> STREAM_CODEC = StreamCodec.of(
			(buffer, params) -> {
				params.encode(buffer);
			},
			(buffer) -> {
				FuelRecipeParams params = new FuelRecipeParams();
				params.decode(buffer);
				return params;
			}
	);

	private boolean superheated;

	private static FuelRecipeParams read(
			List<Either<SizedFluidIngredient, Ingredient>> ingredients,
			List<Either<FluidStack, ProcessingOutput>> results,
			int duration, HeatCondition heat, boolean superheated
	) {
		FuelRecipeParams params = new FuelRecipeParams();

		ingredients.forEach((either) -> {
			either.ifRight(params.ingredients::add).ifLeft(params.fluidIngredients::add);
		});
		results.forEach((either) -> {
			either.ifRight(params.results::add).ifLeft(params.fluidResults::add);
		});

		params.processingDuration = duration;
		params.requiredHeat = heat;
		params.superheated = superheated;
		return params;
	}

	@Override
	protected void encode(@NotNull RegistryFriendlyByteBuf buf) {
		super.encode(buf);
		ByteBufCodecs.BOOL.encode(buf, superheated);
	}

	@Override
	protected void decode(@NotNull RegistryFriendlyByteBuf buf) {
		super.decode(buf);
		superheated = ByteBufCodecs.BOOL.decode(buf);
	}
}