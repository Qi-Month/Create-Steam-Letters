package dev.celestiacraft.steam_letters.client.ponder.scene;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import com.simibubi.create.foundation.ponder.element.BeltItemElement;
import dev.celestiacraft.steam_letters.common.block.steam_hammer.SteamHammerBlockEntity;
import dev.celestiacraft.steam_letters.common.register.SlFluids;
import dev.celestiacraft.steam_letters.common.register.SlParticles;
import net.createmod.catnip.math.VecHelper;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

/**
 * 蒸汽锻锤的 Ponder 场景.
 * <p>
 * 结构文件: {@code assets/steam_letters/ponder/steam_hammer.nbt}
 */
public class SteamHammerScene {
	public static void usage(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		RandomSource random = RandomSource.create();

		scene.scaleSceneView(0.9F);
		scene.title("steam_hammer", "Steam Hammer");

		BlockPos hammer = new BlockPos(3, 3, 2);
		BlockPos tankController = new BlockPos(5, 1, 5);
		BlockPos tankCorner = new BlockPos(6, 3, 6);
		BlockPos burner = new BlockPos(1, 1, 5);
		BlockPos boiler = new BlockPos(1, 2, 5);
		BlockPos beltHead = new BlockPos(1, 1, 2);
		BlockPos beltTail = new BlockPos(5, 1, 2);
		BlockPos hammerBelt = new BlockPos(3, 1, 2);
		BlockPos funnelBelt = new BlockPos(5, 1, 2);
		BlockPos funnel = new BlockPos(5, 2, 2);
		BlockPos pipe = new BlockPos(3, 2, 5);

		Selection hammerSelection = util.select().position(hammer);
		Selection tankSelectionForNbt = util.select().position(tankController);
		Selection tank = util.select().fromTo(tankController, tankCorner);
		Selection boilerSelection = util.select().fromTo(burner, boiler);
		Selection pipeI = util.select().fromTo(2, 1, 5, 4, 2, 5);
		Selection pipeII = util.select().fromTo(3, 3, 3, 3, 3, 5);
		Selection belt = util.select().fromTo(beltHead, beltTail);
		Selection barrel = util.select().fromTo(6, 1, 2, 6, 2, 2)
				.add(util.select().position(5, 2, 2));
		Selection hammerForShow = util.select().position(3, 14, 2);

		ItemStack ironIngot = new ItemStack(Items.IRON_INGOT, 64);
		ItemStack ironSheet = new ItemStack(AllItems.IRON_SHEET.get(), 64);
		ItemStack copperIngot = new ItemStack(Items.COPPER_INGOT, 64);

		scene.world().showSection(util.select().layer(0), Direction.UP);
		scene.idle(5);

		scene.world().showSection(hammerSelection, Direction.DOWN);
		ElementLink<WorldSectionElement> hammerLink = scene.world().showIndependentSection(hammerForShow, Direction.DOWN);

		scene.idle(10);
		scene.overlay().showText(70)
				.pointAt(util.vector().centerOf(hammer))
				.placeNearTarget()
				.attachKeyFrame()
				.text("The Steam Hammer is a press that forms incoming items");
		scene.idle(80);

		scene.overlay().showText(70)
				.pointAt(util.vector().centerOf(hammer))
				.placeNearTarget()
				.attachKeyFrame()
				.text("It needs more stress than a mechanical press to keep working...");
		scene.idle(40);

		scene.world().modifyBlockEntityNBT(hammerSelection, KineticBlockEntity.class, (nbt) -> {
			nbt.putFloat("Speed", -16.0F);
		});
		for (int i = 0; i < 8; i++) {
			scene.effects().emitParticles(
					util.vector().blockSurface(hammer, Direction.NORTH),
					scene.effects().simpleParticleEmitter(SlParticles.STEAM.get(), VecHelper.offsetRandomly(Vec3.ZERO, random, 0.1f)),
					1, 1
			);
		}
		scene.idle(40);

		scene.overlay().showText(80)
				.placeNearTarget()
				.attachKeyFrame()
				.text("...and a constant supply of steam as well");
		scene.idle(40);

		scene.world().modifyBlockEntityNBT(tankSelectionForNbt, FluidTankBlockEntity.class, (nbt) -> {
			FluidTank tankContent = new FluidTank(96000);
			tankContent.setFluid(new FluidStack(SlFluids.STEAM.get(), 96000));
			nbt.put("TankContent", tankContent.writeToNBT(RegistryAccess.EMPTY, new CompoundTag()));
		});

		scene.world().showSection(tank, Direction.WEST);
		scene.idle(5);
		scene.world().showSection(boilerSelection, Direction.EAST);
		scene.idle(5);
		scene.world().showSection(pipeI, Direction.NORTH);
		scene.idle(5);

		ElementLink<WorldSectionElement> pipeIILink = scene.world().showIndependentSection(pipeII, Direction.DOWN);
		scene.idle(35);

		scene.world().showSection(belt, Direction.SOUTH);
		scene.idle(10);
		scene.world().showSection(barrel, Direction.WEST);
		scene.idle(10);

		scene.overlay().showText(80)
				.placeNearTarget()
				.attachKeyFrame()
				.text("In return it can process several items at once");
		scene.idle(40);

		ElementLink<BeltItemElement> itemDrop = scene.world().createItemOnBelt(beltHead, Direction.WEST, ironIngot);
		scene.idle(20);

		scene.world().modifyBlockEntity(hammer, SteamHammerBlockEntity.class, (entity) -> {
			entity.getPressingBehaviour().start(PressingBehaviour.Mode.BELT);
		});
		scene.world().stallBeltItem(itemDrop, true);

		scene.idle(55);
		scene.world().modifyBlockEntity(hammer, SteamHammerBlockEntity.class, (entity) -> {
			entity.getPressingBehaviour().makePressingParticleEffect(util.vector().centerOf(hammerBelt).add(0, 0.5, 0), ironIngot);
		});
		scene.idle(30);
		scene.world().removeItemsFromBelt(hammerBelt);

		itemDrop = scene.world().createItemOnBelt(hammerBelt, Direction.UP, ironSheet);
		scene.idle(10);
		scene.world().stallBeltItem(itemDrop, false);

		scene.idle(16);
		scene.world().removeItemsFromBelt(funnelBelt);
		scene.world().flapFunnel(funnel, false);

		scene.idle(15);
		scene.world().hideIndependentSection(pipeIILink, Direction.UP);
		scene.world().modifyBlock(pipe, (state) -> state.setValue(PipeBlock.UP, false), false);

		scene.idle(10);
		scene.world().setBlocks(hammerSelection, Blocks.AIR.defaultBlockState(), true);
		scene.world().moveSection(hammerLink, util.vector().of(0, -11, 0), 0);

		scene.overlay().showText(80)
				.pointAt(util.vector().centerOf(hammer))
				.placeNearTarget()
				.attachKeyFrame()
				.text("Without steam the hammer cannot process anything");
		scene.idle(40);

		scene.world().createItemOnBelt(beltHead, Direction.WEST, copperIngot);
		scene.idle(36);
		scene.world().removeItemsFromBelt(funnelBelt);
		scene.world().flapFunnel(funnel, false);
		scene.idle(34);

		scene.markAsFinished();
	}
}