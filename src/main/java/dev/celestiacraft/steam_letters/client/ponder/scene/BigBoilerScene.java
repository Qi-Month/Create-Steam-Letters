package dev.celestiacraft.steam_letters.client.ponder.scene;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

/**
 * 蒸汽锅炉 (大型锅炉) 的 Ponder 场景.
 * <p>
 * 结构文件: {@code assets/steam_letters/ponder/big_boiler.nbt}
 */
public class BigBoilerScene {
	public static void usage(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);

		scene.scaleSceneView(0.86F);
		scene.title("big_boiler", "大型锅炉");

		BlockPos pipe = util.grid().at(7, 3, 3);
		BlockPos pump = util.grid().at(7, 3, 4);

		Selection rotate = util.select().position(1, 2, 1)
				.add(util.select().position(1, 2, 3));
		Selection engineI = util.select().fromTo(1, 1, 1, 1, 2, 1)
				.add(util.select().position(3, 2, 1));
		Selection engineII = util.select().fromTo(1, 1, 3, 3, 2, 3);
		Selection smallBoiler = util.select().fromTo(4, 1, 1, 4, 2, 1);
		Selection bigBoilerI = util.select().fromTo(1, 2, 6, 2, 5, 7);
		Selection bigBoilerII = util.select().fromTo(5, 2, 5, 7, 6, 7);
		Selection burnerI = util.select().fromTo(1, 1, 6, 2, 1, 7);
		Selection burnerII = util.select().fromTo(5, 1, 5, 7, 1, 7);
		Selection scaffolding = util.select().position(3, 1, 1);
		Selection tank = util.select().fromTo(7, 1, 1, 8, 3, 2);
		Selection waterPipe = util.select().fromTo(pipe, pump);
		Selection pumpSelection = util.select().position(pump);
		Selection pipeI = util.select().fromTo(5, 3, 3, 5, 3, 4);
		Selection pipeII = util.select().fromTo(2, 3, 4, 2, 3, 5);
		Selection pipeIII = util.select().fromTo(2, 3, 3, 4, 3, 3);
		Selection pipeIV = util.select().fromTo(3, 3, 1, 3, 3, 2);

		scene.world().showSection(util.select().layer(0), Direction.UP);
		scene.idle(5);

		scene.world().modifyBlockEntityNBT(rotate, KineticBlockEntity.class, (nbt) -> {
			nbt.putFloat("Speed", -16.0F);
		});

		scene.world().showSection(engineI, Direction.DOWN);
		scene.idle(5);
		ElementLink<WorldSectionElement> link = scene.world().showIndependentSection(smallBoiler, Direction.WEST);
		scene.idle(10);

		scene.overlay().showText(70)
				.pointAt(util.vector().centerOf(util.grid().at(4, 2, 1)))
				.placeNearTarget()
				.attachKeyFrame()
				.text("一个锅炉太小, 产出蒸汽太少, 怎么办? ");
		scene.idle(80);

		scene.overlay().showText(70)
				.placeNearTarget()
				.attachKeyFrame()
				.text("那就把多个锅炉以立方体形式摆在一起吧! ");
		scene.idle(5);
		scene.world().showSection(bigBoilerI, Direction.DOWN);
		scene.idle(5);
		scene.world().showSection(bigBoilerII, Direction.DOWN);
		scene.idle(70);

		scene.overlay().showText(70)
				.placeNearTarget()
				.attachKeyFrame()
				.colored(PonderPalette.RED)
				.text("注意: 它的热源必须是处于超级燃烧形态的烈焰人燃烧室");
		scene.idle(5);
		scene.world().showSection(burnerI, Direction.NORTH);
		scene.idle(5);
		scene.world().showSection(burnerII, Direction.WEST);
		scene.idle(70);

		scene.world().modifyBlockEntityNBT(pumpSelection, KineticBlockEntity.class, (nbt) -> {
			nbt.putFloat("Speed", -16.0F);
		});

		scene.overlay().showText(70)
				.placeNearTarget()
				.attachKeyFrame()
				.text("此时锅炉需要更多的水...");
		scene.idle(20);
		scene.world().showSection(tank, Direction.DOWN);
		scene.idle(5);
		scene.world().showSection(waterPipe, Direction.DOWN);
		scene.idle(20);
		scene.world().hideIndependentSection(link, Direction.UP);
		scene.world().showSection(scaffolding, Direction.WEST);
		scene.idle(35);

		scene.overlay().showText(70)
				.placeNearTarget()
				.attachKeyFrame()
				.text("...也会产出更多蒸汽");
		scene.idle(10);
		scene.world().showSection(engineII, Direction.DOWN);
		scene.idle(10);
		scene.world().showSection(pipeI, Direction.SOUTH);
		scene.idle(5);
		scene.world().showSection(pipeII, Direction.SOUTH);
		scene.idle(5);
		scene.world().showSection(pipeIII, Direction.DOWN);
		scene.idle(5);
		scene.world().showSection(pipeIV, Direction.DOWN);
		scene.idle(45);
	}
}