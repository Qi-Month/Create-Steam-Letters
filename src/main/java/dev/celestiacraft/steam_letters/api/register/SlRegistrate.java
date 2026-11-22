package dev.celestiacraft.steam_letters.api.register;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import dev.celestiacraft.steam_letters.api.machine.SteamEngineDriveSpec;
import dev.celestiacraft.steam_letters.api.machine.SteamEngineSpec;
import dev.celestiacraft.steam_letters.api.machine.SteamFlywheelSpec;
import dev.celestiacraft.steam_letters.api.register.builder.BoilerBlockBuilder;
import dev.celestiacraft.steam_letters.api.register.builder.BurnerBlockBuilder;
import dev.celestiacraft.steam_letters.api.register.builder.RecipeTypeBuilder;
import dev.celestiacraft.steam_letters.api.register.builder.SolarBoilerBlockBuilder;
import dev.celestiacraft.steam_letters.api.register.builder.SteamEngineBlockBuilder;
import dev.celestiacraft.steam_letters.api.register.builder.SteamFlywheelBlockBuilder;
import dev.celestiacraft.steam_letters.common.block.boiler.SteamBoilerBlock;
import dev.celestiacraft.steam_letters.common.block.burner.AbstractBurnerBlock;
import dev.celestiacraft.steam_letters.common.block.engine.SteamEngineBlock;
import dev.celestiacraft.steam_letters.common.block.flywheel.SteamFlywheelBlock;
import dev.celestiacraft.steam_letters.common.block.solar_boiler.SolarBoilerBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 本模组唯一使用的注册入口, 取代 {@link CreateRegistrate}.
 * <p>
 * 相比原版 {@link CreateRegistrate} 多出五种方块 Builder, 把机制参数直接写在注册链上:
 * {@link BurnerBlockBuilder}, {@link BoilerBlockBuilder}, {@link SolarBoilerBlockBuilder},
 * {@link SteamEngineBlockBuilder} 与 {@link SteamFlywheelBlockBuilder}.
 * <p>
 * 这些 Builder 不能走 {@code entry(...)}: 它的自类型参数写死成 {@link BlockBuilder}, 子类无法满足约束,
 * 所以这里自己调 {@code accept} 完成挂接.
 */
public class SlRegistrate extends CreateRegistrate {
	/**
	 * 配方要占用两个注册表(类型与序列化器), Registrate 的 Builder 只服务单个注册表, 因此这两条由本类自己持有,
	 * 在 {@link #registerEventListeners(IEventBus)} 里一并挂到总线上, 由 {@link #recipe(String, Supplier)} 使用.
	 */
	private final DeferredRegister<RecipeType<?>> recipeTypes;
	private final DeferredRegister<RecipeSerializer<?>> recipeSerializers;

	protected SlRegistrate(String modid) {
		super(modid);
		recipeTypes = DeferredRegister.create(Registries.RECIPE_TYPE, modid);
		recipeSerializers = DeferredRegister.create(Registries.RECIPE_SERIALIZER, modid);
	}

	public static SlRegistrate create(String modid) {
		return new SlRegistrate(modid);
	}

	@Override
	public SlRegistrate setTooltipModifierFactory(@Nullable Function<Item, TooltipModifier> factory) {
		super.setTooltipModifierFactory(factory);
		return this;
	}

	@Override
	public @NotNull SlRegistrate registerEventListeners(IEventBus bus) {
		super.registerEventListeners(bus);
		recipeTypes.register(bus);
		recipeSerializers.register(bus);
		return this;
	}

	public <T extends AbstractBurnerBlock, P> BurnerBlockBuilder<T, P> burner(P parent, String name, NonNullBiFunction<BlockBehaviour.Properties, SteamEngineSpec, T> factory) {
		return BurnerBlockBuilder.create(this, parent, name, this::accept, factory);
	}

	public <T extends AbstractBurnerBlock> BurnerBlockBuilder<T, CreateRegistrate> burner(String name, NonNullBiFunction<BlockBehaviour.Properties, SteamEngineSpec, T> factory) {
		return burner(self(), name, factory);
	}

	public <T extends SteamBoilerBlock, P> BoilerBlockBuilder<T, P> boiler(P parent, String name, NonNullBiFunction<BlockBehaviour.Properties, SteamEngineSpec, T> factory) {
		return BoilerBlockBuilder.create(this, parent, name, this::accept, factory);
	}

	public <T extends SteamBoilerBlock> BoilerBlockBuilder<T, CreateRegistrate> boiler(String name, NonNullBiFunction<BlockBehaviour.Properties, SteamEngineSpec, T> factory) {
		return boiler(self(), name, factory);
	}

	public <T extends SolarBoilerBlock, P> SolarBoilerBlockBuilder<T, P> solarBoiler(P parent, String name, NonNullBiFunction<BlockBehaviour.Properties, SteamEngineSpec, T> factory) {
		return SolarBoilerBlockBuilder.create(this, parent, name, this::accept, factory);
	}

	public <T extends SolarBoilerBlock> SolarBoilerBlockBuilder<T, CreateRegistrate> solarBoiler(String name, NonNullBiFunction<BlockBehaviour.Properties, SteamEngineSpec, T> factory) {
		return solarBoiler(self(), name, factory);
	}

	public <T extends SteamEngineBlock, P> SteamEngineBlockBuilder<T, P> steamEngine(P parent, String name, NonNullBiFunction<BlockBehaviour.Properties, SteamEngineDriveSpec, T> factory) {
		return SteamEngineBlockBuilder.create(this, parent, name, this::accept, factory);
	}

	public <T extends SteamEngineBlock> SteamEngineBlockBuilder<T, CreateRegistrate> steamEngine(String name, NonNullBiFunction<BlockBehaviour.Properties, SteamEngineDriveSpec, T> factory) {
		return steamEngine(self(), name, factory);
	}

	public <T extends SteamFlywheelBlock, P> SteamFlywheelBlockBuilder<T, P> steamFlywheel(P parent, String name, NonNullBiFunction<BlockBehaviour.Properties, SteamFlywheelSpec, T> factory) {
		return SteamFlywheelBlockBuilder.create(this, parent, name, this::accept, factory);
	}

	public <T extends SteamFlywheelBlock> SteamFlywheelBlockBuilder<T, CreateRegistrate> steamFlywheel(String name, NonNullBiFunction<BlockBehaviour.Properties, SteamFlywheelSpec, T> factory) {
		return steamFlywheel(self(), name, factory);
	}

	public <R extends Recipe<?>> RecipeTypeBuilder<R> recipe(String name, Supplier<RecipeSerializer<R>> serializer) {
		return new RecipeTypeBuilder<>(this, name, serializer);
	}

	public <R extends ProcessingRecipe<?>> RecipeTypeBuilder fuelRecipe(String name, ProcessingRecipeBuilder.ProcessingRecipeFactory<R> factory) {
		return recipe(name, () -> {
			return new ProcessingRecipeSerializer<>(factory);
		});
	}

	public <R extends RecipeType<?>> RegistryObject<R> registerRecipeType(String name, Supplier<R> type) {
		return recipeTypes.register(name, type);
	}

	public <R extends RecipeSerializer<?>> RegistryObject<R> registerRecipeSerializer(String name, Supplier<R> serializer) {
		return recipeSerializers.register(name, serializer);
	}
}