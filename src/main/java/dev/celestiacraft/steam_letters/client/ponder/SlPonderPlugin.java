package dev.celestiacraft.steam_letters.client.ponder;

import dev.celestiacraft.steam_letters.CreateLettersSteam;
import net.createmod.ponder.api.registration.*;
import net.createmod.ponder.api.scene.PonderStoryBoard;
import net.createmod.ponder.foundation.PonderIndex;
import net.createmod.ponder.foundation.PonderTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

public class SlPonderPlugin implements PonderPlugin {
	public static void register() {
		boolean registered = PonderIndex.streamPlugins()
				.anyMatch((plugin) -> {
					return plugin instanceof SlPonderPlugin;
				});

		if (!registered) {
			PonderIndex.addPlugin(new SlPonderPlugin());
		}
	}

	@Override
	public @NotNull String getModId() {
		return CreateLettersSteam.MODID;
	}

	@Override
	public void registerScenes(@NotNull PonderSceneRegistrationHelper<ResourceLocation> helper) {
		SlPonderScene.register(helper(helper));
	}

	@Override
	public void registerTags(@NotNull PonderTagRegistrationHelper<ResourceLocation> helper) {
		SlPonderTags.register(helper);

		// 场景条目的 highlightTags 就是它归属的 tag, 一并写进 tag 索引.
		// 只注册 tag 本身的话, tag 页面里只有那个当图标的物品, 场景对应的机器不会出现在列表里.
		for (Map.Entry<ResourceLocation, StoryBoardEntry> entry : PonderIndex.getSceneAccess().getRegisteredEntries()) {
			StoryBoardEntry storyBoard = entry.getValue();

			if (!getModId().equals(storyBoard.getNamespace())) {
				continue;
			}

			for (ResourceLocation tag : storyBoard.getTags()) {
				if (!PonderTag.Highlight.ALL.equals(tag)) {
					helper.addTagToComponent(storyBoard.getComponent(), tag);
				}
			}
		}
	}

	private PonderSceneRegistrationHelper<Item> helper(PonderSceneRegistrationHelper<ResourceLocation> helper) {
		return helper.withKeyFunction(BuiltInRegistries.ITEM::getKey);
	}

	public static void addPonderForBlocks(PonderSceneRegistrationHelper<Item> helper, Class<?> type, String scene, PonderStoryBoard board, ResourceLocation... tags) {
		addPonderForBlocks(helper, type::isInstance, scene, board, tags);
	}

	public static void addPonderForBlocks(PonderSceneRegistrationHelper<Item> helper, Predicate<Block> filter, String scene, PonderStoryBoard board, ResourceLocation... tags) {
		Set<Item> components = new LinkedHashSet<>();

		for (Block block : BuiltInRegistries.BLOCK) {
			if (!filter.test(block)) {
				continue;
			}

			Item item = block.asItem();
			if (item == Items.AIR) {
				continue;
			}

			components.add(item);
		}

		addScenes(helper, scene, board, tags, components);
	}

	public static void addPonderForItems(PonderSceneRegistrationHelper<Item> helper, Class<?> type, String scene, PonderStoryBoard board, ResourceLocation... tags) {
		Set<Item> components = new LinkedHashSet<>();

		for (Item item : BuiltInRegistries.ITEM) {
			if (type.isInstance(item)) {
				components.add(item);
			}
		}

		addScenes(helper, scene, board, tags, components);
	}

	private static void addScenes(PonderSceneRegistrationHelper<Item> helper, String scene, PonderStoryBoard board, ResourceLocation[] tags, Collection<Item> components) {
		if (components.isEmpty()) {
			return;
		}

		ResourceLocation location = helper.asLocation(scene);
		List<Item> targets = new ArrayList<>();

		for (Item item : components) {
			if (!hasStoryBoard(item, location)) {
				targets.add(item);
			}
		}

		if (targets.isEmpty()) {
			return;
		}

		helper.forComponents(targets)
				.addStoryBoard(scene, board, tags);
	}

	private static boolean hasStoryBoard(Item item, ResourceLocation scene) {
		ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
		SceneRegistryAccess access = PonderIndex.getSceneAccess();

		if (key == null) {
			return true;
		}

		for (Map.Entry<ResourceLocation, StoryBoardEntry> entry : access.getRegisteredEntries()) {
			if (entry.getKey().equals(key) && entry.getValue().getSchematicLocation().equals(scene)) {
				return true;
			}
		}

		return false;
	}
}