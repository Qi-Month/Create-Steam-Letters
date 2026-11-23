package dev.celestiacraft.steam_letters.api.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;
import java.util.function.BooleanSupplier;

@AllArgsConstructor
@Getter
public class SlRecipePending {
	private final RecipeHolder<?> holder;
	private final List<BooleanSupplier> conditions;

	public boolean passes() {
		for (BooleanSupplier condition : conditions) {
			if (!condition.getAsBoolean()) {
				return false;
			}
		}

		return true;
	}
}