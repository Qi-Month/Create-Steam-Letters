package dev.celestiacraft.steam_letters.api.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * 一条待注入的代码配方: 只存取法, 等注入时才构造, 因此登记时不要求注册表已就绪.
 */
@AllArgsConstructor
@Getter
public class SlRecipePending {
	private final Supplier<RecipeHolder<?>> factory;
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