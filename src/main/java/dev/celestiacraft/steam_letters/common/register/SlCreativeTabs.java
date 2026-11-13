package dev.celestiacraft.steam_letters.common.register;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.api.SlTranKeys;
import dev.celestiacraft.steam_letters.api.item.SteamBlockItem;
import dev.celestiacraft.steam_letters.common.item.SteamContainerItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.*;

public class SlCreativeTabs {
	public static final DeferredRegister<CreativeModeTab> TABS;

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BASIC;

	static {
		TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), CreateLettersSteam.MODID);

		BASIC = TABS.register("basic", () -> {
			return CreativeModeTab.builder()
					.icon(SlBlocks.STEAM_HAMMER::asStack)
					.title(Component.translatable(SlTranKeys.BASIC_TAB))
					.displayItems((parameters, output) -> {
						Set<Item> added = new HashSet<>();

						// 蒸汽生产: 锅炉, 燃料, 引擎与飞轮
						output.accept(SlBlocks.BRONZE_BOILER);
						output.accept(SlBlocks.BRONZE_SOLID_BURNER);
						output.accept(SlBlocks.BRONZE_FLUID_BURNER);
						output.accept(SlBlocks.BRONZE_SOLAR_BOILER);
						output.accept(SlBlocks.BRONZE_STEAM_ENGINE);
						output.accept(SlBlocks.BRONZE_STEAM_FLYWHEEL);

						// 机械
						output.accept(SlBlocks.STEAM_HAMMER);
						output.accept(SlBlocks.STEAM_MIXER);
						output.accept(SlBlocks.STEAM_SAW);
						output.accept(SlBlocks.BRONZE_STEAM_PUMP);

						// 基础设施
						output.accept(SlBlocks.BRONZE_BASIN);
						// output.accept(SlBlocks.BRONZE_DEPOT);
						output.accept(SlBlocks.ADVANCED_SPOUT);
						output.accept(SlBlocks.BRONZE_CASING);

						// 零件
						output.accept(SlItems.STEAM_MECHANISM);
						output.accept(SlItems.INCOMPLETE_STEAM_MECHANISM);
						output.accept(SlItems.BRONZE_WHISK);

						// 蒸汽容器: 空罐一个, 密封罐按装填物各给一个
						output.accept(SlItems.EMPTY_STEAM_CONTAINER);
						addFilledContainers(output, added, SlItems.SEALED_STEAM_CONTAINER);
						addDynamicDevices(output, added);

						// 材料
						output.accept(SlMaterials.BRONZE_INGOT);
						output.accept(SlMaterials.BRONZE_PLATE);
						output.accept(SlMaterials.BRONZE_BLOCK);
						output.accept(SlMaterials.TIN_INGOT);
						output.accept(SlMaterials.TIN_NUGGET);
						output.accept(SlMaterials.TIN_BLOCK);

						// 流体桶
						output.accept(SlFluids.STEAM.get().getBucket());
						output.accept(SlFluids.SUPERHEATED_STEAM.get().getBucket());
					})
					.build();
		});
	}

	private static void addFilledContainers(CreativeModeTab.Output output, Set<Item> added, ItemEntry<SteamContainerItem> entry) {
		ItemStack steam = entry.asStack();
		SteamContainerItem.writeFluid(steam, new FluidStack(
				SlFluids.STEAM.get(),
				SteamContainerItem.CAPACITY
		));
		added.add(steam.getItem());
		output.accept(steam);

		ItemStack superheated = entry.asStack();
		SteamContainerItem.writeFluid(superheated, new FluidStack(
				SlFluids.SUPERHEATED_STEAM.get(),
				SteamContainerItem.CAPACITY
		));
		added.add(superheated.getItem());
		output.accept(superheated);
	}

	private static void addDynamicDevices(CreativeModeTab.Output output, Set<Item> added) {
		List<Item> devices = new ArrayList<>();

		BuiltInRegistries.ITEM.forEach((item) -> {
			if (item instanceof SteamBlockItem && !added.contains(item)) {
				devices.add(item);
			}
		});

		devices.sort(Comparator.comparing((item) -> {
			return BuiltInRegistries.ITEM.getKey(item).toString();
		}));

		for (Item item : devices) {
			output.accept(item);
		}
	}

	public static void init(IEventBus bus) {
		TABS.register(bus);
	}
}