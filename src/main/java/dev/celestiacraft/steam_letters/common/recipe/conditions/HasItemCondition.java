package dev.celestiacraft.steam_letters.common.recipe.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.Optional;

public class HasItemCondition implements ICondition {
	public static final MapCodec<HasItemCondition> CODEC;

	private final Optional<ResourceLocation> item;
	private final Optional<TagKey<Item>> tag;

	public HasItemCondition(Optional<ResourceLocation> item, Optional<TagKey<Item>> tag) {
		if (item.isPresent() == tag.isPresent()) {
			throw new IllegalArgumentException("`has_item` requires exactly one of `item` or `tag` to be specified.");
		}

		this.item = item;
		this.tag = tag;
	}

	private HasItemCondition(Item item) {
		this(Optional.of(BuiltInRegistries.ITEM.getKey(item)), Optional.empty());
	}

	private HasItemCondition(TagKey<Item> tag) {
		this(Optional.empty(), Optional.of(tag));
	}

	static {
		CODEC = RecordCodecBuilder.mapCodec((instance) -> {
			return instance.group(
					ResourceLocation.CODEC.optionalFieldOf("item")
							.forGetter(HasItemCondition::item),
					TagKey.codec(Registries.ITEM)
							.optionalFieldOf("tag")
							.forGetter(HasItemCondition::tag)
			).apply(instance, HasItemCondition::new);
		});
	}

	public static HasItemCondition ofItem(Item item) {
		return new HasItemCondition(item);
	}

	public static HasItemCondition ofItem(ResourceLocation item) {
		return new HasItemCondition(BuiltInRegistries.ITEM.get(item));
	}

	public static HasItemCondition ofItem(String item) {
		return new HasItemCondition(BuiltInRegistries.ITEM.get(ResourceLocation.parse(item)));
	}

	public static HasItemCondition ofTag(TagKey<Item> tag) {
		return new HasItemCondition(tag);
	}

	public static HasItemCondition ofTag(ResourceLocation tag) {
		return new HasItemCondition(ItemTags.create(tag));
	}

	public static HasItemCondition ofTag(String tag) {
		return new HasItemCondition(ItemTags.create(ResourceLocation.parse(tag)));
	}

	public Optional<ResourceLocation> item() {
		return item;
	}

	public Optional<TagKey<Item>> tag() {
		return tag;
	}

	@Override
	public boolean test(IContext context) {
		return item.map(BuiltInRegistries.ITEM::containsKey)
				.orElseGet(() -> {
					return !context.getTag(tag.get()).isEmpty();
				});
	}

	@Override
	public MapCodec<? extends ICondition> codec() {
		return CODEC;
	}

	@Override
	public String toString() {
		return item.map((location) -> {
			return "has_item(\"" + location + "\")";
		}).orElseGet(() -> {
			return "has_item(\"" + tag.get().location() + "\")";
		});
	}
}