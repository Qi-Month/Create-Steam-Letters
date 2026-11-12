package dev.celestiacraft.steam_letters.api;

import com.jesz.createdieselgenerators.CreateDieselGenerators;
import com.simibubi.create.Create;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.api.register.SlRegistrate;
import dev.celestiacraft.steam_letters.common.recipe.type.FluidFuelRecipe;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class SlApis {
	public static final SlRegistrate REGISTRATE = SlRegistrate.create(CreateLettersSteam.MODID)
			.defaultCreativeTab((ResourceKey<CreativeModeTab>) null)
			.setTooltipModifierFactory((item) -> {
				return new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
						.andThen(TooltipModifier.mapNull(KineticStats.create(item)));
			});

	@Info("1000mB")
	public static int getFuelAmount() {
		return FluidFuelRecipe.FUEL_AMOUNT;
	}

	public static ResourceLocation loadResource(String path) {
		return ResourceLocation.fromNamespaceAndPath(CreateLettersSteam.MODID, path);
	}

	@HideFromJS
	public static ResourceLocation loadCreate(String path) {
		return Create.asResource(path);
	}

	@HideFromJS
	public static ResourceLocation loadCDG(String path) {
		return CreateDieselGenerators.rl(path);
	}

	@HideFromJS
	public static ResourceLocation loadCommon(String path) {
		return ResourceLocation.fromNamespaceAndPath("c", path);
	}

	@HideFromJS
	public static void require(ProcessingRecipeBuilder<?, ?, ?> builder, TagKey<Item> tag, int count) {
		for (int i = 0; i < count; i++) {
			builder.require(tag);
		}
	}
}