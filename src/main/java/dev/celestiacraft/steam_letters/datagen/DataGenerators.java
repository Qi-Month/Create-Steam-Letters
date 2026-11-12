package dev.celestiacraft.steam_letters.datagen;

import com.tterrag.registrate.providers.ProviderType;
import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.datagen.language.English;
import dev.celestiacraft.steam_letters.datagen.recipe.SlRecipeProvider;
import dev.celestiacraft.steam_letters.datagen.recipe.type.SlFluidFuelRecipeGen;
import dev.celestiacraft.steam_letters.datagen.recipe.type.SlSolidFuelRecipeGen;
import dev.celestiacraft.steam_letters.datagen.recipe.type.create.*;
import dev.celestiacraft.steam_letters.datagen.tags.BlockTagsGen;
import dev.celestiacraft.steam_letters.datagen.tags.ItemTagsGen;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = CreateLettersSteam.MODID)
public class DataGenerators {
	@SubscribeEvent
	public static void onDatagen(GatherDataEvent event) {
		stopKubeJSBackgroundThread();

		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();
		ExistingFileHelper helper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
		boolean server = event.includeServer();

		if (server) {
			generator.addProvider(server, new SlRecipeProvider(output, provider));
			generator.addProvider(server, new SlMixingRecipeGen(output, provider));
			generator.addProvider(server, new SlPressRecipeGen(output, provider));
			generator.addProvider(server, new SlItemApplicationRecipeGen(output, provider));
			generator.addProvider(server, new SlSequencedAssemblyRecipeGen(output, provider));
			generator.addProvider(server, new SlCrushingRecipeGen(output, provider));
			generator.addProvider(server, new SlFluidFuelRecipeGen(output, provider));
			generator.addProvider(server, new SlSolidFuelRecipeGen(output, provider));
			generator.addProvider(server, new SlMillingRecipeGen(output, provider));
		}
	}

	/**
	 * Registrate 的数据生成器回调必须在 root provider 构造前登记,
	 * 而 root provider 由 {@code SlRegistrate.onData} 创建,
	 * 因此这里由子类在 {@code super.onData} 之前调用.
	 * <p>
	 * 事件监听顺序不受控, 放到事件回调里登记会触发
	 * {@code Cannot add data generator after construction of root generator}.
	 *
	 * @param event 数据生成事件
	 */
	public static void addRegistrateProviders(GatherDataEvent event) {
		if (event.includeClient()) {
			SlApis.REGISTRATE.addDataGenerator(ProviderType.LANG, English::addTranslations);
		}

		if (event.includeServer()) {
			SlApis.REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, ItemTagsGen::addItemTags);
			SlApis.REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, BlockTagsGen::addBlockTags);
		}
	}

	private static void stopKubeJSBackgroundThread() {
		if (!ModList.get().isLoaded(KubeJS.MOD_ID)) {
			return;
		}
		try {
			Class.forName("dev.latvian.mods.kubejs.util.KubeJSBackgroundThread")
					.getField("running")
					.setBoolean(null, false);
		} catch (Throwable ignored) {
		}
	}
}