package dev.celestiacraft.steam_letters.common.block.burner;

import com.simibubi.create.foundation.block.IBE;
import dev.celestiacraft.steam_letters.api.machine.ISteamEngineBlock;
import dev.celestiacraft.steam_letters.api.machine.SteamEngineSpec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.NotNull;

/**
 * 燃烧室的公共外观与状态: 水平朝向 + 点燃标记.
 * <p>
 * 方块只负责外观与状态, 热量与燃料数据全部在方块实体里.
 */
public abstract class AbstractBurnerBlock extends Block implements ISteamEngineBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	private final SteamEngineSpec spec;

	protected AbstractBurnerBlock(Properties properties, SteamEngineSpec spec) {
		super(properties);
		this.spec = spec;
		registerDefaultState(defaultBlockState()
				.setValue(FACING, Direction.NORTH)
				.setValue(LIT, false));
	}

	@Override
	public SteamEngineSpec getEngineSpec() {
		return spec;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, LIT);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState()
				.setValue(FACING, context.getHorizontalDirection());
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
		return state.getValue(LIT) ? 15 : 0;
	}

	@Override
	public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
		return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
	}

	@Override
	public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
		IBE.onRemove(state, level, pos, newState);
	}
}