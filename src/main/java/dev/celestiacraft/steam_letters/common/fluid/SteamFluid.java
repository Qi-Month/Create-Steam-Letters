package dev.celestiacraft.steam_letters.common.fluid;

import com.tterrag.registrate.builders.FluidBuilder;
import dev.celestiacraft.steam_letters.tags.SlFluidTags;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.joml.Vector3f;

import java.util.function.Supplier;

@Getter
public class SteamFluid extends FluidType {
	private Vector3f fogColor;
	private Supplier<Float> fogDistance;
	private final ResourceLocation stillTexture;
	private final ResourceLocation flowingTexture;
	private final int tintColor;

	public SteamFluid(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture, int tintColor) {
		super(properties.density(-10)
				.viscosity(1)
				.canPushEntity(false)
				.canConvertToSource(false)
				.canDrown(true));
		this.stillTexture = stillTexture;
		this.flowingTexture = flowingTexture;
		this.tintColor = tintColor;
	}

	public static FluidBuilder.FluidTypeFactory create(int fogColor, Supplier<Float> fogDistance, int tintColor) {
		return (properties, still, flowing) -> {
			SteamFluid fluidType = new SteamFluid(properties, still, flowing, tintColor);
			fluidType.fogColor = new Vector3f(
					((fogColor >> 16) & 0xFF) / 255.0f,
					((fogColor >> 8) & 0xFF) / 255.0f,
					(fogColor & 0xFF) / 255.0f
			);
			fluidType.fogDistance = fogDistance;
			return fluidType;
		};
	}

	@Override
	public int getTemperature(FluidStack stack) {
		if (!ModList.get().isLoaded("gtceu")) {
			return 114514;
		}

		if (stack.is(SlFluidTags.STEAM)) {
			return 373;
		}

		if (stack.is(SlFluidTags.SUPERHEATED_STEAM)) {
			return 573;
		}

		return super.getTemperature(stack);
	}
}