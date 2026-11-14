package dev.celestiacraft.steam_letters.client.ponder.scene;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.celestiacraft.steam_letters.common.block.boiler.SteamBoilerBlockEntity;
import dev.celestiacraft.steam_letters.common.block.burner.AbstractBurnerBlock;
import dev.celestiacraft.steam_letters.common.register.SlParticles;
import net.createmod.catnip.math.VecHelper;
import net.createmod.ponder.api.PonderPalette;
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
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

/**
 * 蒸汽动力机组的 Ponder 场景: 蒸汽锅炉 / 固体燃烧室 / 流体燃烧室 / 蒸汽飞轮 / 飞轮引擎 共用.
 * <p>
 * 结构文件: {@code assets/steam_letters/ponder/steam_engine.nbt}
 */
public class SteamEngineScene {
	public static void usage(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		RandomSource random = RandomSource.create();

		scene.scaleSceneView(0.86F);
		scene.title("steam_engine", "蒸汽动力源");

		BlockPos boilerPos = util.grid().at(4, 2, 4);
		BlockPos boilerScaffoldingPos = util.grid().at(4, 1, 4);
		BlockPos waterTankController = util.grid().at(6, 1, 6);
		BlockPos waterTankCorner = util.grid().at(7, 3, 7);
		BlockPos waterPipeCorner = util.grid().at(4, 1, 5);
		BlockPos waterPump = util.grid().at(5, 2, 6);
		BlockPos solidBurnerPos = util.grid().at(4, 1, 1);
		BlockPos liquidBurnerPos = util.grid().at(4, 1, 2);
		BlockPos beltHead = util.grid().at(8, 1, 1);
		BlockPos beltTail = util.grid().at(6, 1, 1);
		BlockPos lavaPumpPos = util.grid().at(6, 1, 2);
		BlockPos lavaTankControllerPos = util.grid().at(7, 1, 2);
		BlockPos flywheelPos = util.grid().at(1, 2, 4);
		BlockPos flywheelCorner = util.grid().at(3, 1, 4);

		Selection boiler = util.select().position(boilerPos);
		Selection boilerScaffolding = util.select().position(boilerScaffoldingPos);
		Selection waterTank = util.select().fromTo(waterTankController, waterTankCorner);
		Selection waterTankForNbt = util.select().position(waterTankController);
		Selection boilerForNbt = util.select().position(boilerPos);
		Selection waterPipe = util.select().fromTo(waterPipeCorner, waterPump);
		Selection waterPipeForNbt = util.select().position(waterPump);
		Selection solidBurnerSelection = util.select().fromTo(solidBurnerPos, beltHead);
		Selection liquidBurnerSelection = util.select().fromTo(4, 1, 2, 8, 2, 3);
		Selection lavaPump = util.select().position(lavaPumpPos);
		Selection lavaTankController = util.select().position(lavaTankControllerPos);
		Selection flywheel = util.select().fromTo(flywheelPos, flywheelCorner);
		Selection rotating = util.select().fromTo(1, 2, 1, 1, 2, 4);
		Selection shaft = util.select().fromTo(1, 1, 1, 1, 2, 3);

		ItemStack planks = new ItemStack(Items.SPRUCE_PLANKS, 64);

		scene.world().showSection(util.select().layer(0), Direction.UP);
		scene.idle(5);

		ElementLink<WorldSectionElement> boilerScaffoldingLink = scene.world().showIndependentSection(boilerScaffolding, Direction.DOWN);
		scene.idle(5);
		scene.world().showSection(boiler, Direction.DOWN);

		scene.idle(10);
		scene.overlay().showText(70)
				.pointAt(util.vector().centerOf(boilerPos))
				.placeNearTarget()
				.attachKeyFrame()
				.text("想要利用蒸汽动力源, 先搭建一个锅炉以生产蒸汽");
		scene.idle(80);

		scene.world().showSection(waterTank, Direction.WEST);
		scene.world().modifyBlockEntityNBT(waterTankForNbt, FluidTankBlockEntity.class, (nbt) -> {
			FluidTank tank = new FluidTank(96000);
			tank.setFluid(new FluidStack(Fluids.WATER, 96000));
			nbt.put("TankContent", tank.writeToNBT(RegistryAccess.EMPTY, new CompoundTag()));
		});
		scene.idle(5);

		scene.world().modifyBlockEntityNBT(boilerForNbt, SteamBoilerBlockEntity.class, (nbt) -> {
			FluidTank tank = new FluidTank(10000);
			tank.setFluid(new FluidStack(Fluids.WATER, 10000));
			nbt.put("TankContent", tank.writeToNBT(RegistryAccess.EMPTY, new CompoundTag()));
		});
		scene.world().modifyBlockEntityNBT(waterPipeForNbt, KineticBlockEntity.class, (nbt) -> {
			nbt.putFloat("Speed", -16.0F);
		});
		scene.world().showSection(waterPipe, Direction.NORTH);
		scene.idle(10);

		scene.overlay().showText(50)
				.pointAt(util.vector().centerOf(boilerPos))
				.placeNearTarget()
				.attachKeyFrame()
				.text("向锅炉里通入水");
		scene.idle(60);

		scene.world().hideIndependentSection(boilerScaffoldingLink, Direction.EAST);
		scene.idle(10);
		scene.overlay().showOutline(PonderPalette.GREEN, new Object(), boilerScaffolding, 80);
		scene.idle(10);
		scene.overlay().showText(70)
				.pointAt(util.vector().centerOf(boilerScaffoldingPos))
				.placeNearTarget()
				.attachKeyFrame()
				.text("再在锅炉下方布置热源");
		scene.idle(80);

		ElementLink<WorldSectionElement> solidBurner = scene.world().showIndependentSection(solidBurnerSelection, Direction.DOWN);
		scene.idle(5);
		scene.world().moveSection(solidBurner, util.vector().of(0, 0, 3), 20);
		scene.idle(15);
		scene.overlay().showText(50)
				.placeNearTarget()
				.attachKeyFrame()
				.text("热源可以是固体燃烧室...");

		scene.idle(10);
		for (int i = 0; i < 3; i++) {
			scene.world().createItemOnBelt(beltHead, Direction.DOWN, planks);
			scene.idle(15);
			scene.world().removeItemsFromBelt(beltTail);

			if (i == 0) {
				scene.world().modifyBlock(solidBurnerPos, (state) -> state.setValue(AbstractBurnerBlock.LIT, true), false);
			}
		}
		scene.idle(5);
		scene.world().hideIndependentSection(solidBurner, Direction.EAST);
		scene.idle(10);

		scene.world().modifyBlockEntityNBT(lavaPump, KineticBlockEntity.class, (nbt) -> {
			nbt.putFloat("Speed", -16.0F);
		});
		ElementLink<WorldSectionElement> liquidBurner = scene.world().showIndependentSection(liquidBurnerSelection, Direction.DOWN);
		scene.idle(5);
		scene.world().modifyBlockEntityNBT(lavaTankController, FluidTankBlockEntity.class, (nbt) -> {
			FluidTank tank = new FluidTank(64000);
			tank.setFluid(new FluidStack(Fluids.LAVA, 64000));
			nbt.put("TankContent", tank.writeToNBT(RegistryAccess.EMPTY, new CompoundTag()));
		});
		scene.idle(5);
		scene.world().modifyBlock(liquidBurnerPos, (state) -> state.setValue(AbstractBurnerBlock.LIT, true), false);
		scene.idle(5);
		scene.world().moveSection(liquidBurner, util.vector().of(0, 0, 2), 20);
		scene.idle(15);
		scene.overlay().showText(50)
				.placeNearTarget()
				.attachKeyFrame()
				.text("...也可以是流体燃烧室");
		scene.idle(60);

		scene.overlay().showText(70)
				.placeNearTarget()
				.attachKeyFrame()
				.text("既有热源又有水时, 锅炉便能产出蒸汽");
		scene.idle(40);
		for (int i = 0; i < 8; i++) {
			scene.effects().emitParticles(
					util.vector().blockSurface(boilerPos, Direction.NORTH),
					scene.effects().simpleParticleEmitter(SlParticles.STEAM.get(), VecHelper.offsetRandomly(Vec3.ZERO, random, 0.1f)),
					1, 1
			);
			scene.effects().emitParticles(
					util.vector().blockSurface(boilerPos, Direction.WEST),
					scene.effects().simpleParticleEmitter(SlParticles.STEAM.get(), VecHelper.offsetRandomly(Vec3.ZERO, random, 0.1f)),
					1, 1
			);
		}
		scene.idle(40);

		scene.world().modifyBlockEntityNBT(rotating, KineticBlockEntity.class, (nbt) -> {
			nbt.putFloat("Speed", -16.0F);
		});
		scene.world().showSection(flywheel, Direction.EAST);
		scene.idle(5);
		scene.world().showSection(shaft, Direction.SOUTH);
		scene.idle(10);

		scene.overlay().showText(70)
				.pointAt(util.vector().centerOf(flywheelPos))
				.placeNearTarget()
				.attachKeyFrame()
				.text("放上引擎与飞轮, 便能输出应力");
		scene.idle(80);

		scene.markAsFinished();
		scene.idle(200);

		scene.overlay().showText(70)
				.pointAt(util.vector().centerOf(boilerPos))
				.placeNearTarget()
				.attachKeyFrame()
				.text("值得注意的是, 在使用锅炉时有三位工程师不幸被烫伤, 他们分别是...");
		scene.idle(80);

		scene.overlay().showText(70)
				.pointAt(util.vector().centerOf(boilerPos))
				.placeNearTarget()
				.attachKeyFrame()
				.text("...所以我们改进了锅炉, 不会烫到你! ");
		scene.idle(80);
	}
}