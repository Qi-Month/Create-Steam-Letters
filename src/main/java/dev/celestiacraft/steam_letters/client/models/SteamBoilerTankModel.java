package dev.celestiacraft.steam_letters.client.models;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.foundation.block.connected.CTModel;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.HorizontalCTBehaviour;
import net.createmod.catnip.data.Iterate;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 多方块锅炉储罐的模型, 照 Create 流体储罐的做法给侧面与顶面套上连接纹理.
 * <p>
 * 水平相邻的同类方块之间会剔掉朝向彼此的那一面, 相邻时替换成 connected 图集里对应的格子.
 */
public class SteamBoilerTankModel extends CTModel {
	private static final ModelProperty<CullData> CULL_PROPERTY = new ModelProperty<>();

	public static SteamBoilerTankModel create(BakedModel originalModel, ResourceLocation directory) {
		return new SteamBoilerTankModel(originalModel, SteamBoilerSpriteShifts.side(directory), SteamBoilerSpriteShifts.top(directory));
	}

	private SteamBoilerTankModel(BakedModel originalModel, CTSpriteShiftEntry side, CTSpriteShiftEntry top) {
		super(originalModel, new HorizontalCTBehaviour(side, top));
	}

	@Override
	protected ModelData.Builder gatherModelData(ModelData.Builder builder, BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
		super.gatherModelData(builder, level, pos, state, modelData);
		CullData cull = new CullData();
		for (Direction direction : Iterate.horizontalDirections) {
			cull.setCulled(direction, ConnectivityHandler.isConnected(level, pos, pos.relative(direction)));
		}
		return builder.with(CULL_PROPERTY, cull);
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random, ModelData data, RenderType renderType) {
		if (side != null) {
			return Collections.emptyList();
		}

		List<BakedQuad> quads = new ArrayList<>();
		for (Direction direction : Iterate.directions) {
			if (data.has(CULL_PROPERTY) && data.get(CULL_PROPERTY).isCulled(direction)) {
				continue;
			}
			quads.addAll(super.getQuads(state, direction, random, data, renderType));
		}
		quads.addAll(super.getQuads(state, null, random, data, renderType));
		return quads;
	}

	private static class CullData {
		private final boolean[] culled;

		private CullData() {
			culled = new boolean[4];
			Arrays.fill(culled, false);
		}

		private void setCulled(Direction face, boolean value) {
			if (face.getAxis().isVertical()) {
				return;
			}
			culled[face.get2DDataValue()] = value;
		}

		private boolean isCulled(Direction face) {
			if (face.getAxis().isVertical()) {
				return false;
			}
			return culled[face.get2DDataValue()];
		}
	}
}