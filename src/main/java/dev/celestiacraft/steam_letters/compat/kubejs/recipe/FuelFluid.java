package dev.celestiacraft.steam_letters.compat.kubejs.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class FuelFluid {
	private final FluidStackJS fluid;
	private final ResourceLocation tag;

	private FuelFluid(FluidStackJS fluid, ResourceLocation tag) {
		this.fluid = fluid;
		this.tag = tag;
	}

	@HideFromJS
	public static @NotNull FuelFluid of(@Nullable Object from) {
		if (from instanceof FuelFluid fuel) {
			return fuel;
		}

		if (from instanceof JsonElement element) {
			return of(element.getAsString());
		}

		if (from instanceof FluidStackJS fluid) {
			return new FuelFluid(fluid, null);
		}

		if (from == null) {
			throw new IllegalArgumentException("fluid fuel can not be null");
		}

		String value = from.toString();
		if (!value.isEmpty() && value.charAt(0) == '#') {
			ResourceLocation tag = ResourceLocation.tryParse(value.substring(1));
			if (tag == null) {
				throw new IllegalArgumentException("invalid fluid tag '" + value + "'");
			}
			return new FuelFluid(null, tag);
		}

		return new FuelFluid(FluidStackJS.of(value), null);
	}

	public static @NotNull FuelFluid ofFluid(@NotNull FluidStackJS fluid) {
		return new FuelFluid(fluid, null);
	}

	public static @NotNull FuelFluid ofTag(@NotNull ResourceLocation tag) {
		return new FuelFluid(null, tag);
	}

	public boolean isTag() {
		return tag != null;
	}

	public @NotNull JsonElement toJson() {
		if (isTag()) {
			return new JsonPrimitive("#" + tag);
		}
		return new JsonPrimitive(fluid.getId());
	}
}