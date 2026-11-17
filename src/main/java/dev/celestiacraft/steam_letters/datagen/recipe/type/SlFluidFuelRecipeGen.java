package dev.celestiacraft.steam_letters.datagen.recipe.type;

import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.common.recipe.builder.FluidFuelRecipeGen;
import dev.celestiacraft.steam_letters.common.recipe.conditions.HasFluidCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.FluidTags;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.fluids.TinkerFluids;

import java.util.concurrent.CompletableFuture;

public class SlFluidFuelRecipeGen extends FluidFuelRecipeGen {
	public SlFluidFuelRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> future) {
		super(output, future, CreateLettersSteam.MODID);

		// 熔岩
		recipe("lava", (builder) -> {
			return builder.fluidTag(FluidTags.LAVA)
					.timeSeconds(30);
		});

		// 烈焰血
		recipe("compat/blazing_blood", (builder) -> {
			return builder.fluid(TinkerFluids.blazingBlood.get())
					.timeSeconds(60)
					.superheated()
					.whenModLoaded(TConstruct.MOD_ID);
		});

		// 油
		recipe("compat/oil", (builder) -> {
			return builder.fluidTag("c:oil")
					.timeSeconds(45)
					.withCondition(HasFluidCondition.ofTag("c:oil"));
		});

		// 原油 我就不信有哪个Mod捣乱俩Tag都加:(
		recipe("compat/crude_oil", (builder) -> {
			return builder.fluidTag("c:crude_oil")
					.timeSeconds(45)
					.withCondition(HasFluidCondition.ofTag("c:crude_oil"));
		});

		// 杂酚油
		recipe("compat/creosote", (builder) -> {
			return builder.fluidTag("c:creosote")
					.timeSeconds(40)
					.withCondition(HasFluidCondition.ofTag("c:creosote"));
		});

		// 生物柴油
		recipe("compat/biodiesel", (builder) -> {
			return builder.fluidTag("c:biodiesel")
					.time(20 * (60 + (40)))
					.superheated()
					.withCondition(HasFluidCondition.ofTag("c:biodiesel"));
		});

		// 生物乙醇
		recipe("compat/bioethanol", (builder) -> {
			return builder.fluidTag("c:bioethanol")
					.timeSeconds(60 * 2)
					.superheated()
					.withCondition(HasFluidCondition.ofTag("c:bioethanol"));
		});

		// 柴油
		recipe("compat/diesel", (builder) -> {
			return builder.fluidTag("c:diesel")
					.timeSeconds(20 + (60 * 3))
					.superheated()
					.withCondition(HasFluidCondition.ofTag("c:diesel"));
		});

		// 汽油
		recipe("compat/gasoline", (builder) -> {
			return builder.fluidTag("c:gasoline")
					.timeSeconds(20 + (60 * 3))
					.superheated()
					.withCondition(HasFluidCondition.ofTag("c:gasoline"));
		});

		// 种子油
		recipe("compat/plantoil", (builder) -> {
			return builder.fluidTag("c:plantoil")
					.timeSeconds(20 + 5)
					.withCondition(HasFluidCondition.ofTag("c:plantoil"));
		});

		// 乙醇
		recipe("compat/ethanol", (builder) -> {
			return builder.fluidTag("c:ethanol")
					.timeSeconds(20 * 10)
					.withCondition(HasFluidCondition.ofTag("c:ethanol"));
		});
	}
}