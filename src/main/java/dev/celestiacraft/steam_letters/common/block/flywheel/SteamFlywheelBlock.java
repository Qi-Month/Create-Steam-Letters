package dev.celestiacraft.steam_letters.common.block.flywheel;

import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.api.machine.SteamFlywheelSpec;
import dev.celestiacraft.steam_letters.client.models.SlBlockStateGen;
import lombok.Getter;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

public class SteamFlywheelBlock extends HorizontalKineticBlock implements IBE<SteamFlywheelBlockEntity> {
	public static final EnumProperty<ConnectionState> CONNECTION = EnumProperty.create("connection", ConnectionState.class);

	private final SteamFlywheelSpec spec;
	@Getter
	private final ResourceLocation material;

	public SteamFlywheelBlock(Properties properties) {
		this(properties, new SteamFlywheelSpec(SlApis.loadResource("bronze"), null));
	}

	public SteamFlywheelBlock(Properties properties, SteamFlywheelSpec spec) {
		super(properties.noOcclusion());
		this.spec = spec;
		this.material = spec.getMaterial();
		registerDefaultState(defaultBlockState()
				.setValue(CONNECTION, ConnectionState.NONE));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(CONNECTION));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction preferred = getPreferredHorizontalFacing(context);
		if (preferred != null) {
			return defaultBlockState()
					.setValue(HORIZONTAL_FACING, preferred.getOpposite());
		}
		return defaultBlockState()
				.setValue(HORIZONTAL_FACING, context.getHorizontalDirection());
	}

	/**
	 * 飞轮是否已经和某个引擎配对.
	 */
	public static boolean isConnected(BlockState state) {
		return getConnection(state) != null;
	}

	/**
	 * 返回引擎所在的水平方向, 未配对时为 null.
	 */
	public static Direction getConnection(BlockState state) {
		Direction direction = state.getValue(HORIZONTAL_FACING);
		ConnectionState connection = state.getValue(CONNECTION);
		if (connection == ConnectionState.LEFT) {
			return direction.getCounterClockWise();
		}
		if (connection == ConnectionState.RIGHT) {
			return direction.getClockWise();
		}
		return null;
	}

	/**
	 * 写入配对方向, {@code direction} 为 null 表示断开配对.
	 */
	public static void setConnection(Level world, BlockPos pos, BlockState state, Direction direction) {
		Direction facing = state.getValue(HORIZONTAL_FACING);
		ConnectionState connection = ConnectionState.NONE;
		if (direction == facing.getClockWise()) {
			connection = ConnectionState.RIGHT;
		} else if (direction == facing.getCounterClockWise()) {
			connection = ConnectionState.LEFT;
		}
		world.setBlock(pos, state.setValue(CONNECTION, connection), 18);
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face == state.getValue(HORIZONTAL_FACING).getOpposite();
	}

	@Override
	public Direction.Axis getRotationAxis(BlockState state) {
		return state.getValue(HORIZONTAL_FACING).getAxis();
	}

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		Direction connection = getConnection(state);
		if (connection == null) {
			return super.onWrenched(state, context);
		}
		if (context.getClickedFace().getAxis() == state.getValue(HORIZONTAL_FACING).getAxis()) {
			return InteractionResult.PASS;
		}
		return super.onWrenched(state.setValue(CONNECTION, ConnectionState.NONE), context);
	}

	@Override
	public Class<SteamFlywheelBlockEntity> getBlockEntityClass() {
		return SteamFlywheelBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends SteamFlywheelBlockEntity> getBlockEntityType() {
		return spec.getBlockEntityType();
	}

	public static NonNullBiConsumer<DataGenContext<Block, SteamFlywheelBlock>, RegistrateBlockstateProvider> genBlockState(String material) {
		return SlBlockStateGen.flywheel("block/steam/flywheel/" + material, 90);
	}

	public enum ConnectionState implements StringRepresentable {
		NONE,
		LEFT,
		RIGHT;

		@Override
		public @NotNull String getSerializedName() {
			return Lang.asId(name());
		}
	}
}