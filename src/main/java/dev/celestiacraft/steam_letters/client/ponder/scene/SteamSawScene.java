package dev.celestiacraft.steam_letters.client.ponder.scene;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.celestiacraft.steam_letters.common.block.steam_saw.SteamSawBlockEntity;
import dev.celestiacraft.steam_letters.common.register.SlFluids;
import dev.celestiacraft.steam_letters.common.register.SlParticles;
import net.createmod.catnip.math.VecHelper;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

/**
 * 蒸汽锯的 Ponder 场景.
 * <p>
 * 结构文件: {@code assets/steam_letters/ponder/steam_saw.nbt}
 */
public class SteamSawScene {
	public static void usage(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		RandomSource random = RandomSource.create();

		scene.scaleSceneView(0.9F);
		scene.title("steam_saw", "蒸汽锯");

		BlockPos sawPos = new BlockPos(3, 1, 2);
		BlockPos tankController = new BlockPos(5, 1, 5);
		BlockPos tankCorner = new BlockPos(6, 3, 6);
		BlockPos burner = new BlockPos(1, 1, 5);
		BlockPos boiler = new BlockPos(1, 2, 5);
		BlockPos beltI = new BlockPos(1, 1, 2);
		BlockPos beltII = new BlockPos(2, 1, 2);
		BlockPos beltIII = new BlockPos(4, 1, 2);
		BlockPos beltIV = new BlockPos(5, 1, 2);
		BlockPos funnel = new BlockPos(5, 2, 2);
		BlockPos pipe = new BlockPos(3, 2, 5);

		Selection saw = util.select().position(sawPos);
		Selection tank = util.select().fromTo(tankController, tankCorner);
		Selection boilerSelection = util.select().fromTo(burner, boiler);
		Selection pipeI = util.select().fromTo(2, 2, 5, 4, 2, 5)
				.add(util.select().position(2, 1, 5))
				.add(util.select().position(4, 1, 5));
		Selection pipeII = util.select().fromTo(3, 1, 3, 3, 1, 5);
		Selection beltFront = util.select().fromTo(beltI, beltII);
		Selection beltBehind = util.select().fromTo(beltIII, beltIV);
		Selection casing = util.select().position(6, 1, 2);
		Selection barrel = util.select().fromTo(5, 2, 2, 6, 2, 2);

		ItemStack planksItem = new ItemStack(Items.OAK_PLANKS, 16);
		ItemStack buttonItem = new ItemStack(Items.OAK_BUTTON, 16);
		ItemStack stoneItem = new ItemStack(Items.STONE, 16);

		scene.world().showSection(util.select().layer(0), Direction.UP);
		scene.idle(5);

		scene.world().showSection(saw, Direction.DOWN);

		scene.world().modifyBlockEntity(sawPos, SteamSawBlockEntity.class, (entity) -> {
			entity.setSteamStack(new FluidStack(SlFluids.STEAM.get(), 10000));
		});

		scene.idle(10);
		scene.overlay().showText(70)
				.pointAt(util.vector().centerOf(sawPos))
				.placeNearTarget()
				.attachKeyFrame()
				.text("蒸汽锯是一台升级过的动力锯");
		scene.idle(80);

		scene.overlay().showText(70)
				.pointAt(util.vector().centerOf(sawPos))
				.placeNearTarget()
				.attachKeyFrame()
				.text("它不仅需要更高的应力维持工作...");
		scene.idle(40);

		scene.world().setKineticSpeed(saw, 16.0F);
		for (int i = 0; i < 8; i++) {
			scene.effects().emitParticles(
					util.vector().blockSurface(sawPos, Direction.NORTH),
					scene.effects().simpleParticleEmitter(
							SlParticles.STEAM.get(),
							VecHelper.offsetRandomly(Vec3.ZERO, random, 0.1f)
					),
					1, 1
			);
		}
		scene.idle(40);

		scene.overlay().showText(80)
				.placeNearTarget()
				.attachKeyFrame()
				.text("...还需要源源不断的蒸汽供应");
		scene.idle(40);

		scene.world().modifyBlockEntity(tankController, FluidTankBlockEntity.class, (entity) -> {
			entity.getTankInventory().fill(
					new FluidStack(SlFluids.STEAM.get(), 96000),
					IFluidHandler.FluidAction.EXECUTE
			);
		});

		scene.world().showSection(tank, Direction.WEST);
		scene.idle(5);
		scene.world().showSection(boilerSelection, Direction.EAST);
		scene.idle(5);
		scene.world().showSection(pipeI, Direction.DOWN);
		scene.idle(5);

		ElementLink<WorldSectionElement> pipeIILink = scene.world().showIndependentSection(pipeII, Direction.NORTH);
		scene.idle(35);

		scene.world().showSection(beltFront, Direction.EAST);
		scene.idle(5);
		scene.world().showSection(beltBehind, Direction.WEST);
		scene.idle(5);
		scene.world().showSection(casing, Direction.DOWN);
		scene.idle(5);
		scene.world().showSection(barrel, Direction.DOWN);
		scene.idle(15);

		scene.overlay().showText(80)
				.placeNearTarget()
				.attachKeyFrame()
				.text("作为回报, 它可以一次处理多个物品");
		scene.idle(40);

		scene.world().createItemOnBelt(beltI, Direction.WEST, planksItem);
		scene.idle(12);
		scene.world().removeItemsFromBelt(beltII);
		scene.world().modifyBlockEntity(sawPos, SteamSawBlockEntity.class, (entity) -> {
			entity.inventory.setStackInSlot(0, planksItem.copy());
			entity.inventory.remainingTime = 38.0F;
			entity.inventory.recipeDuration = 38.0F;
			entity.inventory.appliedRecipe = false;
		});
		scene.idle(58);

		scene.world().createItemOnBelt(beltIII, Direction.WEST, buttonItem);
		scene.idle(8);
		scene.world().removeItemsFromBelt(beltIV);
		scene.world().flapFunnel(funnel, false);

		scene.idle(10);
		scene.world().modifyBlockEntity(sawPos, SteamSawBlockEntity.class, (entity) -> {
			entity.setSteamStack(FluidStack.EMPTY);
		});
		scene.world().hideIndependentSection(pipeIILink, Direction.SOUTH);
		scene.world().modifyBlock(pipe, (state) -> {
			return state.setValue(PipeBlock.DOWN, false);
		}, false);

		scene.idle(10);
		scene.overlay().showText(70)
				.placeNearTarget()
				.attachKeyFrame()
				.text("当蒸汽停止供应时, 蒸汽锯将停止工作, 并会卡住传送带");
		scene.idle(40);

		scene.world().createItemOnBelt(beltI, Direction.DOWN, stoneItem);
		scene.idle(9);
		scene.world().removeItemsFromBelt(beltII);
		scene.idle(31);

		scene.markAsFinished();
	}
}