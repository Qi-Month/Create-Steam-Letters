package dev.celestiacraft.steam_letters.common.register;

import com.simibubi.create.AllTags;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.content.kinetics.saw.SawMovementBehaviour;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import com.simibubi.create.content.processing.basin.BasinMovementBehaviour;
import com.simibubi.create.foundation.data.BuilderTransformers;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.api.SlStress;
import dev.celestiacraft.steam_letters.api.item.SteamBlockItem;
import dev.celestiacraft.steam_letters.client.models.ItemModelGen;
import dev.celestiacraft.steam_letters.common.block.advanced_spout.AdvancedSpoutBlock;
import dev.celestiacraft.steam_letters.common.block.boiler.SolarBoilerBlock;
import dev.celestiacraft.steam_letters.common.block.boiler.SolarBoilerBlockEntity;
import dev.celestiacraft.steam_letters.common.block.boiler.SteamBoilerBlock;
import dev.celestiacraft.steam_letters.common.block.boiler.SteamBoilerBlockEntity;
import dev.celestiacraft.steam_letters.common.block.bronze_basin.BronzeBasinBlock;
import dev.celestiacraft.steam_letters.common.block.bronze_depot.BronzeDepotBlock;
import dev.celestiacraft.steam_letters.common.block.burner.AbstractBurnerBlockEntity;
import dev.celestiacraft.steam_letters.common.block.burner.FluidBurnerBlock;
import dev.celestiacraft.steam_letters.common.block.burner.FluidBurnerBlockEntity;
import dev.celestiacraft.steam_letters.common.block.burner.SolidBurnerBlock;
import dev.celestiacraft.steam_letters.common.block.engine.SteamEngineBlock;
import dev.celestiacraft.steam_letters.common.block.engine.SteamEngineBlockEntity;
import dev.celestiacraft.steam_letters.common.block.flywheel.SteamFlywheelBlock;
import dev.celestiacraft.steam_letters.common.block.steam_hammer.SteamHammerBlock;
import dev.celestiacraft.steam_letters.common.block.steam_mixer.SteamMixerBlock;
import dev.celestiacraft.steam_letters.common.block.steam_pump.BronzeSteamPumpBlock;
import dev.celestiacraft.steam_letters.common.block.steam_saw.SteamSawBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.common.Tags;

public class SlBlocks {
	public static final BlockEntry<BronzeBasinBlock> BRONZE_BASIN;
	public static final BlockEntry<BronzeDepotBlock> BRONZE_DEPOT;
	public static final BlockEntry<AdvancedSpoutBlock> ADVANCED_SPOUT;
	public static final BlockEntry<SteamHammerBlock> STEAM_HAMMER;
	public static final BlockEntry<SteamMixerBlock> STEAM_MIXER;
	public static final BlockEntry<SteamSawBlock> STEAM_SAW;
	public static final BlockEntry<SteamBoilerBlock> BRONZE_BOILER;
	public static final BlockEntry<SolarBoilerBlock> BRONZE_SOLAR_BOILER;
	public static final BlockEntry<SolidBurnerBlock> BRONZE_SOLID_BURNER;
	public static final BlockEntry<FluidBurnerBlock> BRONZE_FLUID_BURNER;
	public static final BlockEntry<SteamFlywheelBlock> BRONZE_STEAM_FLYWHEEL;
	public static final BlockEntry<SteamEngineBlock> BRONZE_STEAM_ENGINE;
	public static final BlockEntry<BronzeSteamPumpBlock> BRONZE_STEAM_PUMP;
	public static final BlockEntry<CasingBlock> BRONZE_CASING;

	static {
		BRONZE_BASIN = SlApis.REGISTRATE.block("bronze_basin", BronzeBasinBlock::new)
				.item()
				.model(ItemModelGen.withModel("block/bronze_basin/block"))
				.build()
				.initialProperties(SharedProperties::copperMetal)
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(Tags.Blocks.NEEDS_WOOD_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.blockstate(BronzeBasinBlock.genBlockState())
				.onRegister(MovementBehaviour.movementBehaviour(new BasinMovementBehaviour()))
				.lang("Bronze Basin")
				.register();

		BRONZE_DEPOT = SlApis.REGISTRATE.block("bronze_depot", BronzeDepotBlock::new)
				.item()
				.model(ItemModelGen.withModel("block/bronze_depot/block"))
				.build()
				.initialProperties(SharedProperties::copperMetal)
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(Tags.Blocks.NEEDS_WOOD_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.blockstate(BronzeDepotBlock.genBlockState())
				.lang("Bronze Depot")
				.register();

		ADVANCED_SPOUT = SlApis.REGISTRATE.block("advanced_spout", AdvancedSpoutBlock::new)
				.initialProperties(SharedProperties::copperMetal)
				.item(AssemblyOperatorBlockItem::new)
				.model(ItemModelGen.withModel("block/advanced_spout/item"))
				.build()
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(BlockTags.NEEDS_STONE_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.blockstate(AdvancedSpoutBlock.genBlockState())
				.register();

		STEAM_HAMMER = SlApis.REGISTRATE.block("steam_hammer", SteamHammerBlock::new)
				.item(SteamBlockItem::new)
				.model(ItemModelGen.withModel("block/steam_hammer/item"))
				.build()
				.initialProperties(SharedProperties::stone)
				.transform(SlStress.setImpact(16))
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(BlockTags.NEEDS_STONE_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.blockstate(SteamHammerBlock.genBlockState())
				.lang("Steam Hammer")
				.register();

		STEAM_MIXER = SlApis.REGISTRATE.block("steam_mixer", SteamMixerBlock::new)
				.item(SteamBlockItem::new)
				.model(ItemModelGen.withModel("block/steam_mixer/item"))
				.build()
				.initialProperties(SharedProperties::stone)
				.transform(SlStress.setImpact(16))
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(BlockTags.NEEDS_STONE_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.blockstate(SteamMixerBlock.genBlockState())
				.lang("Steam Mixer")
				.register();

		STEAM_SAW = SlApis.REGISTRATE.block("steam_saw", SteamSawBlock::new)
				.item(SteamBlockItem::new)
				.model(ItemModelGen.withModel("block/mechanical_saw/item"))
				.tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
				.build()
				.initialProperties(SharedProperties::stone)
				.transform(SlStress.setImpact(16))
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(BlockTags.NEEDS_STONE_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.blockstate(SteamSawBlock.genBlockState())
				.onRegister(MovementBehaviour.movementBehaviour(new SawMovementBehaviour()))
				.lang("Steam Saw")
				.register();

		BRONZE_BOILER = SlApis.REGISTRATE.boiler("bronze_boiler", SteamBoilerBlock::new)
				.capacity(SteamBoilerBlockEntity.DEFAULT_CAPACITY)
				.production(SteamBoilerBlockEntity.DEFAULT_PRODUCTION)
				.item(SteamBlockItem::new)
				.model(ItemModelGen.withModel("block/steam/boiler/bronze"))
				.build()
				.initialProperties(SharedProperties::softMetal)
				.properties((properties) -> {
					return properties.sound(SoundType.LANTERN);
				})
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(BlockTags.NEEDS_STONE_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.blockstate(SteamBoilerBlock.genBlockState("bronze"))
				.lang("Bronze Boiler")
				.register();

		BRONZE_SOLAR_BOILER = SlApis.REGISTRATE.solarBoiler("bronze_solar_boiler", SolarBoilerBlock::new)
				.capacity(SolarBoilerBlockEntity.DEFAULT_CAPACITY)
				.production(SolarBoilerBlockEntity.DEFAULT_PRODUCTION)
				.item(SteamBlockItem::new)
				.model(ItemModelGen.withModel("block/steam/solar_boiler/bronze"))
				.build()
				.initialProperties(SharedProperties::softMetal)
				.properties((properties) -> {
					return properties.sound(SoundType.LANTERN);
				})
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(BlockTags.NEEDS_STONE_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.blockstate(SolarBoilerBlock.genBlockState("bronze"))
				.lang("Bronze Solar Boiler")
				.register();

		BRONZE_SOLID_BURNER = SlApis.REGISTRATE.burner("bronze_solid_burner", SolidBurnerBlock::new)
				.maxHeat(AbstractBurnerBlockEntity.DEFAULT_MAX_HEAT)
				.item(SteamBlockItem::new)
				.model(ItemModelGen.withModel("block/steam/solid_burner/bronze"))
				.build()
				.initialProperties(SharedProperties::softMetal)
				.properties((properties) -> {
					return properties.sound(SoundType.LANTERN);
				})
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(BlockTags.NEEDS_STONE_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.blockstate(SolidBurnerBlock.genBlockState("bronze"))
				.lang("Bronze Solid Burner")
				.register();

		BRONZE_FLUID_BURNER = SlApis.REGISTRATE.burner("bronze_fluid_burner", FluidBurnerBlock::new)
				.capacity(FluidBurnerBlockEntity.DEFAULT_CAPACITY)
				.maxHeat(AbstractBurnerBlockEntity.DEFAULT_MAX_HEAT)
				.item(SteamBlockItem::new)
				.model(ItemModelGen.withModel("block/steam/fluid_burner/bronze"))
				.build()
				.initialProperties(SharedProperties::softMetal)
				.properties((properties) -> {
					return properties.sound(SoundType.LANTERN);
				})
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(BlockTags.NEEDS_STONE_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.blockstate(FluidBurnerBlock.genBlockState("bronze"))
				.lang("Bronze Fluid Burner")
				.register();

		BRONZE_STEAM_FLYWHEEL = SlApis.REGISTRATE.steamFlywheel("bronze_steam_flywheel", SteamFlywheelBlock::new)
				.item(SteamBlockItem::new)
				.model(ItemModelGen.withModel("block/steam/flywheel/bronze_item"))
				.build()
				.initialProperties(SharedProperties::softMetal)
				.properties((properties) -> {
					return properties.sound(SoundType.LANTERN);
				})
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(BlockTags.NEEDS_STONE_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.blockstate(SteamFlywheelBlock.genBlockState("bronze"))
				.lang("Bronze Steam Flywheel")
				.register();

		BRONZE_STEAM_ENGINE = SlApis.REGISTRATE.steamEngine("bronze_steam_engine", SteamEngineBlock::new)
				.strength(SteamEngineBlockEntity.DEFAULT_STRENGTH)
				.speed(SteamEngineBlockEntity.DEFAULT_SPEED)
				.consumption(SteamEngineBlockEntity.DEFAULT_CONSUMPTION)
				.capacity(SteamEngineBlockEntity.DEFAULT_STORAGE)
				.material(SlApis.loadResource("bronze"))
				.item(SteamBlockItem::new)
				.model(ItemModelGen.withModel("block/steam/engine/bronze_item"))
				.build()
				.initialProperties(SharedProperties::softMetal)
				.properties((properties) -> {
					return properties.sound(SoundType.LANTERN);
				})
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(BlockTags.NEEDS_STONE_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.blockstate(SteamEngineBlock.genBlockState("bronze"))
				.lang("Bronze Steam Engine")
				.register();

		BRONZE_STEAM_PUMP = SlApis.REGISTRATE.block("bronze_steam_pump", BronzeSteamPumpBlock::new)
				.item(SteamBlockItem::new)
				.model(ItemModelGen.withModel("block/bronze_pump/item"))
				.build()
				.initialProperties(SharedProperties::softMetal)
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(BlockTags.NEEDS_STONE_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.blockstate(BronzeSteamPumpBlock.genBlockState())
				.lang("Bronze Steam Pump")
				.register();

		BRONZE_CASING = SlApis.REGISTRATE.block("bronze_casing", CasingBlock::new)
				.transform(BuilderTransformers.casing(() -> SlSpriteShifts.BRONZE_CASING))
				.properties((properties) -> {
					return properties.sound(SoundType.LANTERN);
				})
				.lang("Bronze Casing")
				.register();
	}

	public static void init() {
	}
}