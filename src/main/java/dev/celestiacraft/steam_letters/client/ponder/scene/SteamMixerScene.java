package dev.celestiacraft.steam_letters.client.ponder.scene;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.celestiacraft.steam_letters.common.block.steam_mixer.SteamMixerBlockEntity;
import dev.celestiacraft.steam_letters.common.register.SlFluids;
import dev.celestiacraft.steam_letters.common.register.SlParticles;
import net.createmod.catnip.math.Pointing;
import net.createmod.catnip.math.VecHelper;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

/**
 * 蒸汽搅拌机的 Ponder 场景.
 * <p>
 * 结构文件: {@code assets/steam_letters/ponder/steam_mixer.nbt}
 */
public class SteamMixerScene {
	public static void usage(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		RandomSource random = RandomSource.create();

		scene.scaleSceneView(0.9F);
		scene.title("steam_mixer", "蒸汽搅拌器");

		BlockPos mixerPos = util.grid().at(3, 4, 2);
		BlockPos tankController = util.grid().at(5, 1, 4);
		BlockPos tankCorner = util.grid().at(6, 3, 5);
		BlockPos casingPos = util.grid().at(3, 1, 2);
		BlockPos basinPos = util.grid().at(3, 2, 2);
		BlockPos beltHead = util.grid().at(3, 1, 1);
		BlockPos beltTail = util.grid().at(6, 1, 1);
		BlockPos pipe = util.grid().at(3, 2, 5);

		Selection mixer = util.select().position(mixerPos);
		Selection tankControllerForNbt = util.select().position(tankController);
		Selection tank = util.select().fromTo(tankController, tankCorner);
		Selection boiler = util.select().fromTo(1, 1, 5, 1, 2, 5);
		Selection pipeI = util.select().fromTo(2, 1, 5, 4, 2, 5);
		Selection pipeII = util.select().fromTo(3, 3, 3, 3, 4, 5);
		Selection casing = util.select().position(casingPos);
		Selection basin = util.select().position(basinPos);
		Selection belt = util.select().fromTo(beltHead, beltTail);

		Vec3 basinControl = util.vector().centerOf(basinPos);

		ItemStack redDye = new ItemStack(Items.RED_DYE);
		ItemStack blueDye = new ItemStack(Items.BLUE_DYE);
		ItemStack purpleDye = new ItemStack(Items.PURPLE_DYE, 32);

		scene.world().showSection(util.select().layer(0), Direction.UP);
		scene.idle(5);

		scene.world().showSection(mixer, Direction.DOWN);

		scene.world().modifyBlockEntityNBT(mixer, SteamMixerBlockEntity.class, (nbt) -> {
			FluidTank tankContent = new FluidTank(10000);
			tankContent.setFluid(new FluidStack(SlFluids.STEAM.get(), 10000));
			nbt.put("Steam", tankContent.writeToNBT(new CompoundTag()));
		});

		scene.idle(10);
		scene.overlay().showText(70)
				.pointAt(util.vector().centerOf(mixerPos))
				.placeNearTarget()
				.attachKeyFrame()
				.text("蒸汽搅拌器是一台升级过的动力搅拌器");
		scene.idle(80);

		scene.overlay().showText(70)
				.pointAt(util.vector().centerOf(mixerPos))
				.placeNearTarget()
				.attachKeyFrame()
				.text("它不仅需要更高的应力维持工作...");
		scene.idle(40);

		scene.world().modifyBlockEntityNBT(mixer, KineticBlockEntity.class, (nbt) -> {
			nbt.putFloat("Speed", 16.0F);
		});
		for (int i = 0; i < 8; i++) {
			scene.effects().emitParticles(
					util.vector().blockSurface(mixerPos, Direction.NORTH),
					scene.effects().simpleParticleEmitter(SlParticles.STEAM.get(), VecHelper.offsetRandomly(Vec3.ZERO, random, 0.1f)),
					1, 1
			);
			scene.effects().emitParticles(
					util.vector().blockSurface(mixerPos, Direction.WEST),
					scene.effects().simpleParticleEmitter(SlParticles.STEAM.get(), VecHelper.offsetRandomly(Vec3.ZERO, random, 0.1f)),
					1, 1
			);
		}
		scene.idle(40);

		scene.overlay().showText(80)
				.placeNearTarget()
				.attachKeyFrame()
				.text("...还需要源源不断的蒸汽供应");
		scene.idle(40);

		scene.world().modifyBlockEntityNBT(tankControllerForNbt, FluidTankBlockEntity.class, (nbt) -> {
			FluidTank tankContent = new FluidTank(96000);
			tankContent.setFluid(new FluidStack(SlFluids.STEAM.get(), 96000));
			nbt.put("TankContent", tankContent.writeToNBT(new CompoundTag()));
		});

		scene.world().showSection(tank, Direction.WEST);
		scene.idle(5);
		scene.world().showSection(boiler, Direction.EAST);
		scene.idle(5);
		scene.world().showSection(pipeI, Direction.DOWN);
		scene.idle(5);

		ElementLink<WorldSectionElement> pipeIILink = scene.world().showIndependentSection(pipeII, Direction.DOWN);
		scene.idle(35);

		scene.world().showSection(casing, Direction.EAST);
		scene.idle(5);
		scene.world().showSection(basin, Direction.WEST);
		scene.idle(5);
		scene.world().showSection(belt, Direction.SOUTH);
		scene.idle(15);

		scene.overlay().showText(80)
				.placeNearTarget()
				.attachKeyFrame()
				.text("作为回报, 它可以一次处理多个物品");
		scene.idle(20);

		scene.overlay().showControls(basinControl, Pointing.LEFT, 15)
				.withItem(redDye);
		scene.overlay().showControls(basinControl, Pointing.RIGHT, 15)
				.withItem(blueDye);

		scene.idle(20);
		scene.world().modifyBlockEntity(mixerPos, SteamMixerBlockEntity.class, (entity) -> {
			entity.startProcessingBasin();
		});
		scene.world().createItemOnBeltLike(basinPos, Direction.UP, redDye);
		scene.world().createItemOnBeltLike(basinPos, Direction.UP, blueDye);

		scene.idle(110);
		scene.world().createItemOnBelt(beltHead, Direction.DOWN, purpleDye);

		scene.idle(25);

		scene.world().hideIndependentSection(pipeIILink, Direction.UP);
		scene.world().modifyBlock(pipe, (state) -> state.setValue(PipeBlock.UP, false), false);

		scene.idle(20);

		scene.overlay().showText(70)
				.placeNearTarget()
				.attachKeyFrame()
				.text("当蒸汽停止供应时, 蒸汽搅拌器将停止工作");
		scene.idle(40);

		scene.overlay().showControls(basinControl, Pointing.LEFT, 15)
				.withItem(redDye);
		scene.overlay().showControls(basinControl, Pointing.RIGHT, 15)
				.withItem(blueDye);
		scene.idle(30);
		scene.overlay().showOutline(PonderPalette.RED, new Object(), basin, 30);
		scene.idle(40);

		scene.markAsFinished();
	}
}