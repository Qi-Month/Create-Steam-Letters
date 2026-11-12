package dev.celestiacraft.steam_letters.common.block.bronze_depot;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.content.logistics.depot.DepotRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public class BronzeDepotRenderer extends SmartBlockEntityRenderer<BronzeDepotBlockEntity> {
	private static final float SURFACE_Y = 15 / 16.0F;
	private static final float CENTER = 0.5F;
	private static final float[][] SINGLE = {{CENTER, CENTER}};
	private static final float[][] PAIR = {{0.32F, CENTER}, {0.68F, CENTER}};
	private static final float[][] TRIANGLE = {{CENTER, 0.3F}, {0.32F, 0.63F}, {0.68F, 0.63F}};
	private static final float[][] QUAD = {{0.32F, 0.32F}, {0.68F, 0.32F}, {0.32F, 0.68F}, {0.68F, 0.68F}};

	public BronzeDepotRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void renderSafe(BronzeDepotBlockEntity entity, float partialTicks, PoseStack ms, MultiBufferSource source, int light, int overlay) {
		Level level = entity.getLevel();
		if (level == null) {
			return;
		}

		DepotBehaviour depot = entity.getBehaviour(DepotBehaviour.TYPE);
		boolean hasSurface = depot != null && !depot.isEmpty();
		ItemStackHandler inventory = entity.getInventory();

		List<ItemStack> stacks = new ArrayList<>();
		List<Integer> slots = new ArrayList<>();

		if (hasSurface) {
			stacks.add(ItemStack.EMPTY);
			slots.add(-1);
		}

		for (int slot = 0; slot < inventory.getSlots(); slot++) {
			ItemStack stack = inventory.getStackInSlot(slot);
			if (stack.isEmpty()) {
				continue;
			}

			stacks.add(stack);
			slots.add(slot);
		}

		int total = stacks.size();
		if (total == 0) {
			return;
		}

		float[][] layout = layout(total);
		float scale = scale(total);

		if (hasSurface) {
			renderSurface(entity, depot, partialTicks, ms, source, light, overlay, layout[0][0], layout[0][1], scale);
		}

		BlockPos pos = entity.getBlockPos();
		for (int index = 0; index < total; index++) {
			ItemStack stack = stacks.get(index);
			if (stack.isEmpty()) {
				continue;
			}

			int slot = slots.get(index);
			float x = layout[index][0];
			float z = layout[index][1];
			Vec3 worldPosition = new Vec3(pos.getX() + x, pos.getY() + SURFACE_Y, pos.getZ() + z);

			ms.pushPose();
			ms.translate(x, SURFACE_Y, z);
			ms.scale(scale, scale, scale);
			DepotRenderer.renderItem(ms, source, light, overlay, stack, entity.getRenderAngle(slot), entity.getRenderRandom(slot), worldPosition, false);
			ms.popPose();
		}
	}

	private void renderSurface(BronzeDepotBlockEntity entity, DepotBehaviour depot, float partialTicks, PoseStack stack, MultiBufferSource buffer, int light, int overlay, float x, float z, float scale) {
		stack.pushPose();
		stack.translate(x, SURFACE_Y, z);
		stack.scale(scale, scale, scale);
		stack.translate(-CENTER, -SURFACE_Y, -CENTER);
		DepotRenderer.renderItemsOf(entity, partialTicks, stack, buffer, light, overlay, depot);
		stack.popPose();
	}

	private static float[][] layout(int count) {
		return switch (count) {
			case 1 -> SINGLE;
			case 2 -> PAIR;
			case 3 -> TRIANGLE;
			case 4 -> QUAD;
			default -> grid(count);
		};
	}

	private static float[][] grid(int count) {
		int columns = Mth.ceil(Mth.sqrt(count));
		int rows = Mth.ceil(count / (float) columns);
		float step = Math.min(0.78F / columns, 0.78F / rows);
		float[][] points = new float[count][];

		for (int index = 0; index < count; index++) {
			int column = index % columns;
			int row = index / columns;
			float x = CENTER + (column - (columns - 1) / 2.0F) * step;
			float z = CENTER + (row - (rows - 1) / 2.0F) * step;
			points[index] = new float[]{x, z};
		}

		return points;
	}

	private static float scale(int count) {
		if (count <= 1) {
			return 1.0F;
		}
		if (count == 2) {
			return 0.85F;
		}
		if (count == 3) {
			return 0.78F;
		}
		if (count == 4) {
			return 0.72F;
		}

		int columns = Mth.ceil(Mth.sqrt(count));
		return Mth.clamp(1.4F / columns, 0.4F, 0.70F);
	}
}