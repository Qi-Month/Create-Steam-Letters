package dev.celestiacraft.steam_letters.client.ponder.scene;

import com.simibubi.create.AllFluids;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import com.simibubi.create.foundation.ponder.element.BeltItemElement;
import dev.celestiacraft.steam_letters.common.block.advanced_spout.AdvancedSpoutBlockEntity;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.lang.reflect.Field;

/**
 * 高级注液器的 Ponder 场景.
 * <p>
 * 结构文件: {@code assets/steam_letters/ponder/advanced_spout.nbt}
 */
public class AdvancedSpoutScene {
	public static void usage(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		RandomSource random = RandomSource.create();

		scene.scaleSceneView(1.1F);
		scene.title("advanced_spout", "高级注液器");

		BlockPos spoutPos = util.grid().at(1, 3, 1);
		BlockPos beltHead = util.grid().at(0, 1, 1);
		BlockPos beltSpout = util.grid().at(1, 1, 1);
		BlockPos beltTail = util.grid().at(2, 1, 1);

		Selection spout = util.select().position(spoutPos);
		Selection belt = util.select().fromTo(beltHead, beltTail);

		Vec3 spoutControl = util.vector().centerOf(spoutPos);

		ItemStack honeyBucket = new ItemStack(AllFluids.HONEY.getBucket().get());
		ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE, 8);
		ItemStack honey = new ItemStack(Items.HONEY_BOTTLE, 8);

		scene.world().showSection(util.select().layer(0), Direction.UP);
		scene.idle(5);

		scene.world().showSection(spout, Direction.DOWN);
		scene.idle(10);
		scene.overlay().showText(70)
				.pointAt(util.vector().centerOf(spoutPos))
				.placeNearTarget()
				.attachKeyFrame()
				.text("高级注液器是一台升级过的注液器");
		scene.idle(80);

		scene.world().showSection(belt, Direction.EAST);

		scene.idle(10);
		scene.overlay().showText(70)
				.placeNearTarget()
				.attachKeyFrame()
				.text("它可以快速地一次处理多个物品");
		scene.idle(40);

		scene.overlay().showControls(spoutControl, Pointing.DOWN, 10)
				.withItem(honeyBucket);
		scene.world().modifyBlockEntity(spoutPos, AdvancedSpoutBlockEntity.class, (entity) -> {
			try {
				Field f = SpoutBlockEntity.class.getDeclaredField("tank");
				f.setAccessible(true);
				SmartFluidTankBehaviour tank = (SmartFluidTankBehaviour) f.get(entity);
				tank.getCapability().fill(new FluidStack(AllFluids.HONEY.get(), 5000), IFluidHandler.FluidAction.EXECUTE);
			} catch (Exception e) {
			}
		});

		scene.idle(20);
		ElementLink<BeltItemElement> itemDrop = scene.world().createItemOnBelt(beltHead, Direction.DOWN, bottle);

		scene.idle(7);
		scene.world().stallBeltItem(itemDrop, true);
		scene.world().modifyBlockEntityNBT(spout, AdvancedSpoutBlockEntity.class, (nbt) -> nbt.putInt("ProcessingTicks", 5));
		scene.idle(5);

		scene.world().removeItemsFromBelt(beltSpout);
		itemDrop = scene.world().createItemOnBelt(beltSpout, Direction.UP, honey);
		scene.world().stallBeltItem(itemDrop, false);
		scene.idle(11);

		scene.markAsFinished();
	}
}
