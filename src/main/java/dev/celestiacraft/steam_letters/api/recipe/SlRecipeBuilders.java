package dev.celestiacraft.steam_letters.api.recipe;

import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.common.recipe.SimpleRecipeTypeInfo;
import dev.celestiacraft.steam_letters.common.register.SlRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.*;
import java.util.function.BooleanSupplier;

/**
 * 代码配方的入口: 用 Java 写完即注入 {@link RecipeManager}, 不产出配方 JSON, 也不依赖 datagen.
 * <p>
 * 注入点是服务端资源重载的末尾. 该次重载的监听器顺序固定为标签、配方、函数、进度, 之后才是模组添加的监听器,
 * 所以本类拿到的配方表已经解析完数据包, 可以直接在其上叠加代码配方. 重载后原版会把整张表同步给在线玩家,
 * 玩家进服时同样发送, 客户端因此不需要另外注册.
 * <p>
 * 配方 id 与 datagen 产物同一套路径, 例如 {@code steam_letters:solid_fuel/diamond}. 同 id 时代码配方覆盖数据包里的那一份.
 */
public class SlRecipeBuilders {
	private static final Map<ResourceLocation, SlRecipePending> PENDING = new LinkedHashMap<>();
	private static final Set<ResourceLocation> INJECTED = new LinkedHashSet<>();
	private static ICondition.IContext conditionContext = ICondition.IContext.EMPTY;
	private static boolean initialized;

	/**
	 * 由模组主类在构造期调用, 挂上重载监听.
	 */
	public static void init() {
		if (initialized) {
			return;
		}

		initialized = true;
		NeoForge.EVENT_BUS.addListener(SlRecipeBuilders::onAddReloadListeners);
	}

	/**
	 * 名字按 {@code steam_letters:solid_fuel/<name>} 解析; 名字里带命名空间时该命名空间优先.
	 */
	public static SolidFuelRecipeBuilder solidFuel(String name) {
		return new SolidFuelRecipeBuilder(recipeId(SlRecipeTypes.SOLID_FUEL, name));
	}

	/**
	 * 完整指定 id, 不再补类型路径.
	 */
	public static SolidFuelRecipeBuilder solidFuel(ResourceLocation id) {
		return new SolidFuelRecipeBuilder(id);
	}

	/**
	 * 名字按 {@code steam_letters:fluid_fuel/<name>} 解析; 名字里带命名空间时该命名空间优先.
	 */
	public static FluidFuelRecipeBuilder fluidFuel(String name) {
		return new FluidFuelRecipeBuilder(recipeId(SlRecipeTypes.FLUID_FUEL, name));
	}

	/**
	 * 完整指定 id, 不再补类型路径.
	 */
	public static FluidFuelRecipeBuilder fluidFuel(ResourceLocation id) {
		return new FluidFuelRecipeBuilder(id);
	}

	/**
	 * 撤掉一条代码配方, 服务器已在跑时立即重新注入.
	 */
	public static void remove(ResourceLocation id) {
		PENDING.remove(id);
		injectIfRunning();
	}

	/**
	 * 登记一条代码配方, 条件每次重载时重新求值, 全部通过才注入.
	 */
	static synchronized <T extends Recipe<?>> RecipeHolder<T> register(RecipeHolder<T> holder, List<BooleanSupplier> conditions) {
		PENDING.put(holder.id(), new SlRecipePending(holder, List.copyOf(conditions)));
		injectIfRunning();

		return holder;
	}

	/**
	 * 把配方自检的错误打进日志.
	 */
	static void report(ResourceLocation id, List<String> errors) {
		if (!errors.isEmpty()) {
			CreateLettersSteam.LOGGER.warn("in-code recipe {} is invalid: {}", id, String.join(" ", errors));
		}
	}

	/**
	 * 用最近一次重载的条件上下文求值 {@link ICondition}, 供 {@code whenCondition} 使用.
	 * 重载尚未发生时上下文为空, 基于标签的条件会判为不通过.
	 */
	static boolean test(ICondition condition) {
		return condition.test(conditionContext);
	}

	private static ResourceLocation recipeId(SimpleRecipeTypeInfo<?> type, String name) {
		String namespace = CreateLettersSteam.MODID;
		String path = name;
		int split = name.indexOf(':');
		if (split >= 0) {
			namespace = name.substring(0, split);
			path = name.substring(split + 1);
		}

		return ResourceLocation.fromNamespaceAndPath(namespace, type.getId().getPath() + "/" + path);
	}

	private static void onAddReloadListeners(AddReloadListenerEvent event) {
		conditionContext = event.getConditionContext();
		event.addListener(new SlRecipeInjector(event.getServerResources().getRecipeManager()));
	}

	private static void injectIfRunning() {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server == null) {
			return;
		}

		if (server.isSameThread()) {
			inject(server.getRecipeManager());
			return;
		}

		server.execute(() -> {
			inject(server.getRecipeManager());
		});
	}

	/**
	 * 先剔掉上一次注入的条目 (重载时它们本就不存在), 再叠上当前条件全部通过的配方.
	 */
	public static synchronized void inject(RecipeManager manager) {
		Map<ResourceLocation, RecipeHolder<?>> merged = new LinkedHashMap<>();
		for (RecipeHolder<?> holder : manager.getRecipes()) {
			if (!INJECTED.contains(holder.id())) {
				merged.put(holder.id(), holder);
			}
		}

		Set<ResourceLocation> applied = new LinkedHashSet<>();
		int skipped = 0;
		for (Map.Entry<ResourceLocation, SlRecipePending> entry : PENDING.entrySet()) {
			if (!entry.getValue().passes()) {
				skipped++;
				continue;
			}

			merged.put(entry.getKey(), entry.getValue().getHolder());
			applied.add(entry.getKey());
		}

		if (applied.isEmpty() && INJECTED.isEmpty()) {
			return;
		}

		INJECTED.clear();
		INJECTED.addAll(applied);
		manager.replaceRecipes(merged.values());
		CreateLettersSteam.LOGGER.info("in-code recipes: {} injected, {} skipped by condition", applied.size(), skipped);
	}
}