package dev.celestiacraft.steam_letters.client.ponder.scene;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.celestiacraft.steam_letters.common.register.SlFluids;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.ArrayList;
import java.util.List;

/**
 * 青铜蒸汽泵 Ponder 场景.
 * <p>
 * 结构文件: {@code assets/steam_letters/ponder/bronze_steam_pump.nbt}
 */
public class BronzeSteamPumpScene {
	public static void usage(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);

		scene.scaleSceneView(0.5F);
		scene.title("bronze_steam_pump", "青铜蒸汽泵");

		BlockPos tankControllerPos = util.grid().at(8, 4, 3);
		BlockPos tankControllerPosII = util.grid().at(3, 4, 8);
		BlockPos fakePump = util.grid().at(8, 6, 7);
		BlockPos pumpPos = util.grid().at(8, 5, 7);

		Selection engine = util.select().fromTo(1, 1, 1, 3, 2, 3);
		Selection boiler = util.select().fromTo(4, 1, 1, 4, 2, 3);
		Selection rotate = util.select().fromTo(1, 2, 1, 1, 2, 3);
		Selection tankI = util.select().fromTo(8, 4, 3, 9, 6, 4);
		Selection tankII = util.select().fromTo(3, 4, 8, 4, 6, 9);
		Selection pipe = util.select().fromTo(9, 4, 5, 5, 5, 9);
		Selection tankController = util.select().position(tankControllerPos);
		Selection tankControllerII = util.select().position(tankControllerPosII);
		Selection bronzeSteamPumpSelection = util.select().position(fakePump);

		scene.world().showSection(util.select().layer(0), Direction.UP);
		scene.idle(5);

		ElementLink<WorldSectionElement> boilerLink = scene.world().showIndependentSection(boiler, Direction.DOWN);
		scene.world().showSection(engine, Direction.DOWN);
		scene.world().modifyBlockEntityNBT(rotate, KineticBlockEntity.class, (nbt) -> {
			nbt.putFloat("Speed", -16.0F);
		});

		scene.idle(10);
		scene.overlay().showText(70)
				.pointAt(util.vector().of(4.0D, 2.5D, 3.5D))
				.placeNearTarget()
				.attachKeyFrame()
				.text("平常使用蒸汽引擎时, 将飞轮引擎和锅炉放在一起会降低效率");
		scene.idle(80);
		scene.overlay().showText(70)
				.pointAt(util.vector().of(4.0D, 2.5D, 3.5D))
				.placeNearTarget()
				.attachKeyFrame()
				.text("在这里额外放置一个动力泵又太麻烦");
		scene.idle(80);

		scene.overlay().showText(40)
				.placeNearTarget()
				.attachKeyFrame()
				.text("这时就该青铜蒸汽泵出场了! ");
		scene.idle(50);

		List<BlockPos> temp = new ArrayList<>();

		for (int x = 7; x <= 9; x++) {
			for (int y = 1; y <= 3; y++) {
				for (int z = 2; z <= 6; z++) {
					temp.add(util.grid().at(x, y, z));
				}
			}
		}

		for (int x = 2; x <= 9; x++) {
			for (int y = 1; y <= 3; y++) {
				for (int z = 7; z <= 9; z++) {
					temp.add(util.grid().at(x, y, z));
				}
			}
		}

		for (int i = 0; i < 12; i++) {
			int i1 = i;
			temp.stream()
					.filter((pos) -> {
						return 9 - pos.getX() + pos.getY() - 1 + 9 - pos.getZ() == i1;
					})
					.forEach((pos) -> {
						scene.world().showSection(util.select().position(pos), Direction.DOWN);
					});
			scene.idle(2);
		}

		scene.idle(5);
		scene.world().showSection(tankI, Direction.SOUTH);
		scene.idle(5);
		scene.world().showSection(pipe, Direction.DOWN);
		scene.idle(5);
		scene.world().showSection(tankII, Direction.EAST);
		scene.idle(10);

		scene.idle(10);
		scene.overlay().showText(90)
				.placeNearTarget()
				.attachKeyFrame()
				.text("青铜蒸汽泵可以在不消耗应力的情况下运输蒸汽");
		scene.idle(30);

		scene.world().modifyBlockEntityNBT(tankController, FluidTankBlockEntity.class, (nbt) -> {
			FluidTank tank = new FluidTank(96000);
			tank.setFluid(new FluidStack(SlFluids.STEAM.get(), 96000));
			nbt.put("TankContent", tank.writeToNBT(RegistryAccess.EMPTY, new CompoundTag()));
		});
		scene.idle(10);

		scene.world().setBlock(pumpPos, Blocks.AIR.defaultBlockState(), true);
		ElementLink<WorldSectionElement> bronzeSteamPump = scene.world().showIndependentSection(bronzeSteamPumpSelection, Direction.DOWN);
		scene.world().moveSection(bronzeSteamPump, util.vector().of(0, -1, 0), 0);
		scene.idle(10);

		final int temp2Speed = 145;

		int temp2Counter = temp2Speed;
		int temp2 = 0;
		for (int i = 0; i < 50; i++) {
			temp2 += temp2Counter;
			int temp3 = temp2;
			scene.world().modifyBlockEntityNBT(tankController, FluidTankBlockEntity.class, (nbt) -> {
				FluidTank tank = new FluidTank(96000);
				tank.setFluid(new FluidStack(SlFluids.STEAM.get(), 96000 - temp3));
				nbt.put("TankContent", tank.writeToNBT(RegistryAccess.EMPTY, new CompoundTag()));
			});
			scene.world().modifyBlockEntityNBT(tankControllerII, FluidTankBlockEntity.class, (nbt) -> {
				FluidTank tank = new FluidTank(96000);
				tank.setFluid(new FluidStack(SlFluids.STEAM.get(), temp3));
				nbt.put("TankContent", tank.writeToNBT(RegistryAccess.EMPTY, new CompoundTag()));
			});
			scene.idle(1);

			if (i < 24) temp2Counter += temp2Speed;
			if (i > 25) temp2Counter -= temp2Speed;
		}

		scene.idle(30);
		scene.overlay().showText(40)
				.pointAt(util.vector().of(4.0D, 2.5D, 3.5D))
				.placeNearTarget()
				.attachKeyFrame()
				.text("回到最开始的问题");
		scene.idle(50);

		scene.overlay().showText(70)
				.pointAt(util.vector().of(4.0D, 2.5D, 3.5D))
				.placeNearTarget()
				.attachKeyFrame()
				.text("在锅炉与飞轮引擎之间放置青铜蒸汽泵即可恢复正常效率");
		scene.idle(40);

		scene.world().moveSection(boilerLink, util.vector().of(1, 0, 0), 10);
		scene.idle(15);
		ElementLink<WorldSectionElement> bronzeSteamPumpII = scene.world().showIndependentSection(util.select().position(4, 3, 3), Direction.DOWN);
		scene.world().moveSection(bronzeSteamPumpII, util.vector().of(0, -1, 0), 0);
		scene.idle(25);

		scene.markAsFinished();
	}
}