package dev.celestiacraft.steam_letters.datagen.advancement;

import com.tterrag.registrate.providers.RegistrateAdvancementProvider;
import dev.celestiacraft.steam_letters.api.SlApis;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class SlAdvancementBuilder {
	/**
	 * 机械动力的成就描述统一用 #DBA213 着色, 作为其子节点时沿用同一套样式.
	 */
	private static final int CREATE_DESCRIPTION_COLOR = 0xDBA213;

	private final ResourceLocation id;
	private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
	private final Advancement.Builder builder = Advancement.Builder.advancement();

	@Nullable
	private ItemLike icon;
	@Nullable
	private ResourceLocation background;
	private Component title;
	private Component description;
	private AdvancementType type = AdvancementType.TASK;
	private boolean parentSet;
	private boolean toast = true;
	private boolean announce;
	private boolean hidden;

	private SlAdvancementBuilder(ResourceLocation id) {
		this.id = id;
		title = Component.translatable(SlAdvancement.titleKey(id.getPath()));
		description = Component.translatable(SlAdvancement.descKey(id.getPath()))
				.withStyle((style) -> {
					return style.withColor(CREATE_DESCRIPTION_COLOR);
				});
	}

	/**
	 * 以本模组的命名空间创建进度.
	 */
	public static SlAdvancementBuilder create(String id) {
		return new SlAdvancementBuilder(SlApis.loadResource(id));
	}

	public static SlAdvancementBuilder create(String namespace, String id) {
		return new SlAdvancementBuilder(ResourceLocation.fromNamespaceAndPath(namespace, id));
	}

	/**
	 * 父进度. 取不到其他模组成就的 AdvancementHolder 时只能用 id 指定.
	 */
	@SuppressWarnings("removal")
	public SlAdvancementBuilder parent(ResourceLocation parent) {
		builder.parent(parent);
		parentSet = true;
		return this;
	}

	public SlAdvancementBuilder parent(String parent) {
		parent(SlApis.loadResource(parent));
		return this;
	}

	public SlAdvancementBuilder parent(AdvancementHolder parent) {
		builder.parent(parent);
		parentSet = true;
		return this;
	}

	public SlAdvancementBuilder icon(ItemLike icon) {
		this.icon = icon;
		return this;
	}

	public SlAdvancementBuilder title(Component title) {
		this.title = title;
		return this;
	}

	public SlAdvancementBuilder title(String translationKey) {
		return title(Component.translatable(translationKey));
	}

	public SlAdvancementBuilder description(Component description) {
		this.description = description;
		return this;
	}

	/**
	 * 按语言键设置描述, 并套用机械动力的金色样式.
	 */
	public SlAdvancementBuilder description(String translationKey) {
		return description(Component.translatable(translationKey)
				.withStyle((style) -> {
					return style.withColor(CREATE_DESCRIPTION_COLOR);
				}));
	}

	/**
	 * 背景图, 只有根进度用得到.
	 */
	public SlAdvancementBuilder background(ResourceLocation background) {
		this.background = background;
		return this;
	}

	public SlAdvancementBuilder frame(AdvancementType type) {
		this.type = type;
		return this;
	}

	public SlAdvancementBuilder toast(boolean toast) {
		this.toast = toast;
		return this;
	}

	public SlAdvancementBuilder announce(boolean announce) {
		this.announce = announce;
		return this;
	}

	public SlAdvancementBuilder hidden(boolean hidden) {
		this.hidden = hidden;
		return this;
	}

	/**
	 * 判定物为指定物品标签, 标签内任一物品进入背包即达成.
	 */
	public SlAdvancementBuilder requires(TagKey<Item> tag) {
		return criterion(InventoryChangeTrigger.TriggerInstance.hasItems(
				ItemPredicate.Builder.item()
						.of(tag)
						.build()
		));
	}

	public SlAdvancementBuilder requires(ItemLike... items) {
		return criterion(InventoryChangeTrigger.TriggerInstance.hasItems(items));
	}

	/**
	 * 自动按插入顺序编号判定条件.
	 */
	public SlAdvancementBuilder criterion(Criterion<?> criterion) {
		return criterion(String.valueOf(criteria.size()), criterion);
	}

	public SlAdvancementBuilder criterion(String name, Criterion<?> criterion) {
		criteria.put(name, criterion);
		return this;
	}

	/**
	 * 生成进度 JSON 并交给 datagen 输出.
	 */
	public AdvancementHolder save(RegistrateAdvancementProvider provider) {
		if (!parentSet) {
			throw new IllegalStateException("Advancement " + id + " is missing a parent");
		}
		if (icon == null) {
			throw new IllegalStateException("Advancement " + id + " is missing an icon");
		}
		if (criteria.isEmpty()) {
			throw new IllegalStateException("Advancement " + id + " is missing a criterion");
		}

		builder.display(icon, title, description, background, type, toast, announce, hidden);
		criteria.forEach(builder::addCriterion);

		return builder.save(provider, id.toString());
	}
}