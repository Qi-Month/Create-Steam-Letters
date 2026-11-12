package dev.celestiacraft.steam_letters.api.machine;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.Optional;

/**
 * 工作盆蒸汽机器接口: 一次操作按 {@link #getParallel()} 连续应用配方.
 *
 * <p>
 * 实现类 {@code extends} 机械动力的对应 BE 类并实现本接口,
 * <p>
 * 例如
 * {@code SteamMixerBlockEntity extends MechanicalMixerBlockEntity implements ISteamBasinMachine}.
 *
 * <p>
 * {@code BasinOperatingBlockEntity} 已具体实现 {@code applyBasinRecipe}
 * <p>
 * 父类具体方法优先于接口 default,
 * 实现类需以 public 覆盖并委托给 {@code ISteamBasinMachine.super.applyBasinRecipe()}.
 *
 * <p>
 * 循环依据 {@link BasinRecipe#apply(BasinBlockEntity, Recipe)} 的返回值结束:
 * <p>
 * 该方法先模拟后执行, 原料不足或产物放不下时返回 {@code false} 且不消耗原料. 蒸汽按操作扣减, 与冲程一致.
 *
 * <p>
 * 访问器对应父类的成员: {@link #getBasinRecipe()} 对应字段 {@code currentRecipe},
 * <p>
 * {@link #matchBasinRecipe(Recipe)} 会放宽 {@code matchBasinRecipe} 的访问级.
 */
public interface ISteamBasinMachine extends ISteamStorage {
	/**
	 * 待应用的配方, 无配方时返回 {@code null}.
	 */
	Recipe<?> getBasinRecipe();

	/**
	 * 机器下方的工作盆.
	 */
	Optional<BasinBlockEntity> getBasin();

	/**
	 * 盆内余料是否仍能应用配方.
	 *
	 * @param recipe 配方
	 * @param <C> 盆内容器
	 * @return
	 */
	<I extends RecipeInput> boolean matchBasinRecipe(Recipe<I> recipe);

	/**
	 * 接着处理下一份, 不重置加工进度.
	 */
	boolean continueWithPreviousRecipe();

	/**
	 * 同步方块实体数据.
	 */
	void sendData();

	/**
	 * 配方应用后的回调, {@code applied} 为应用的份数, 默认无操作.
	 */
	default void onBasinRecipeApplied(int applied) {
	}

	/**
	 * 连续应用配方; 蒸汽不足, 配方不可应用或盆输出未清空时不消耗原料.
	 */
	default void applyBasinRecipe() {
		Recipe<?> recipe = getBasinRecipe();
		if (recipe == null) {
			return;
		}

		Optional<BasinBlockEntity> basin = getBasin();
		int parallel = getParallel();
		if (basin.isEmpty() || parallel <= 0) {
			return;
		}

		boolean wasEmpty = basin.get().canContinueProcessing();
		int applied = 0;
		for (int i = 0; i < parallel; i++) {
			if (!basin.get().canContinueProcessing() || !BasinRecipe.apply(basin.get(), recipe)) {
				break;
			}
			applied++;
		}

		if (applied == 0) {
			return;
		}

		consumeSteam();
		basin.get().inputTank.sendDataImmediately();
		basin.get().notifyChangeOfContents();
		onBasinRecipeApplied(applied);

		if (wasEmpty && matchBasinRecipe(recipe)) {
			continueWithPreviousRecipe();
			sendData();
		}
	}
}