package dev.celestiacraft.steam_letters.api.recipe;

import lombok.AllArgsConstructor;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@AllArgsConstructor
public class SlRecipeInjector implements PreparableReloadListener {
	private final RecipeManager manager;

	@Override
	public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager, ProfilerFiller filler, ProfilerFiller profiler, Executor executor, Executor gameExecutor) {
		return barrier.wait(Unit.INSTANCE).thenRunAsync(() -> {
			SlRecipeBuilders.inject(this.manager);
		}, gameExecutor);
	}
}