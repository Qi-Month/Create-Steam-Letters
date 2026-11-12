package dev.celestiacraft.steam_letters.client.ponder.scene;

import com.simibubi.create.content.fluids.pump.PumpBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.celestiacraft.steam_letters.common.block.boiler.SolarBoilerBlockEntity;
import dev.celestiacraft.steam_letters.common.register.SlFluids;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

/**
 * 太阳能锅炉 Ponder 场景: 太阳能锅炉 / 蒸汽飞轮 / 飞轮引擎 共用.
 * <p>
 * 结构文件: {@code assets/steam_letters/ponder/solar_boiler.nbt}
 */
public class SolarBoilerScene {
	public static void usage(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);

		scene.title("solar_boiler", "Solar Boiler");

		BlockPos boilerScaffoldingPos = new BlockPos(1, 1, 1);
		BlockPos boilerPos = new BlockPos(1, 2, 1);
		BlockPos waterScaffoldingPos = new BlockPos(2, 1, 1);
		BlockPos waterTankUpperPos = new BlockPos(3, 2, 1);
		BlockPos waterTankLowerPos = new BlockPos(3, 1, 1);
		BlockPos pumpPos = new BlockPos(2, 2, 1);
		BlockPos clockPos = new BlockPos(0, 2, 4);

		Selection boilerSelectionForShow = util.select().fromTo(boilerScaffoldingPos, boilerPos);
		Selection water = util.select().fromTo(waterScaffoldingPos, waterTankUpperPos);
		Selection waterTank = util.select().position(waterTankLowerPos);
		Selection pump = util.select().position(pumpPos);
		Selection boilerSelectionForNbt = util.select().fromTo(boilerScaffoldingPos, boilerPos);
		Selection clock = util.select().fromTo(0, 1, 4, 0, 2, 4);
		Selection engineAndFlywheel = util.select().fromTo(1, 1, 2, 1, 2, 4);
		Selection flywheelSelectionForNbt = util.select().fromTo(1, 1, 2, 1, 2, 4);

		scene.world().showSection(util.select().layer(0), Direction.UP);
		scene.idle(5);

		scene.world().showSection(boilerSelectionForShow, Direction.DOWN);

		scene.idle(10);
		scene.overlay().showText(70)
				.pointAt(util.vector().centerOf(boilerPos))
				.placeNearTarget()
				.attachKeyFrame()
				.text("The Solar Boiler offers a cheap way to obtain steam");
		scene.idle(80);

		scene.world().showSection(water, Direction.WEST);
		scene.world().modifyBlockEntityNBT(waterTank, FluidTankBlockEntity.class, (nbt) -> {
			FluidTank tank = new FluidTank(16000);
			tank.setFluid(new FluidStack(Fluids.WATER, 16000));
			nbt.put("TankContent", tank.writeToNBT(RegistryAccess.EMPTY, new CompoundTag()));
		});
		scene.world().modifyBlockEntityNBT(pump, PumpBlockEntity.class, (nbt) -> {
			nbt.putFloat("Speed", -16.0F);
		});
		scene.world().modifyBlockEntityNBT(boilerSelectionForNbt, SolarBoilerBlockEntity.class, (nbt) -> {
			FluidTank tank = new FluidTank(16000);
			tank.setFluid(new FluidStack(Fluids.WATER, 20000));
			nbt.put("InputTank", tank.writeToNBT(RegistryAccess.EMPTY, new CompoundTag()));
		});

		scene.idle(10);
		scene.overlay().showText(50)
				.pointAt(util.vector().centerOf(boilerPos))
				.placeNearTarget()
				.attachKeyFrame()
				.text("Feed water into the boiler");
		scene.idle(60);

		scene.idle(10);
		scene.overlay().showText(60)
				.placeNearTarget()
				.attachKeyFrame()
				.text("With enough light...(the brighter the better)");
		scene.idle(70);

		scene.world().showSection(clock, Direction.DOWN);

		scene.idle(10);
		scene.overlay().showText(60)
				.pointAt(util.vector().centerOf(clockPos))
				.placeNearTarget()
				.attachKeyFrame()
				.text("...and real sunlight is best");
		scene.idle(90);

		scene.world().modifyBlockEntityNBT(boilerSelectionForNbt, SolarBoilerBlockEntity.class, (nbt) -> {
			FluidTank inputTank = new FluidTank(16000);
			inputTank.setFluid(new FluidStack(Fluids.WATER, 0));
			FluidTank outputTank = new FluidTank(16000);
			outputTank.setFluid(new FluidStack(SlFluids.STEAM.get(), 20000));
			nbt.put("InputTank", inputTank.writeToNBT(RegistryAccess.EMPTY, new CompoundTag()));
			nbt.put("OutputTank", outputTank.writeToNBT(RegistryAccess.EMPTY, new CompoundTag()));
		});

		scene.idle(30);
		scene.overlay().showText(70)
				.pointAt(util.vector().centerOf(boilerPos))
				.placeNearTarget()
				.attachKeyFrame()
				.text("it starts producing steam");
		scene.idle(80);

		scene.world().showSection(engineAndFlywheel, Direction.NORTH);
		scene.world().modifyBlockEntityNBT(flywheelSelectionForNbt, KineticBlockEntity.class, (nbt) -> {
			nbt.putFloat("Speed", -16.0F);
		});

		scene.idle(10);
		scene.overlay().showText(70)
				.pointAt(util.vector().centerOf(1, 2, 3))
				.placeNearTarget()
				.attachKeyFrame()
				.text("Attach the same flywheel setup as any other boiler to get stress out");
		scene.idle(80);

		scene.markAsFinished();
	}
}