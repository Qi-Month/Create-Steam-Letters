package dev.celestiacraft.steam_letters.common.block.engine;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.steam_letters.api.machine.SteamEngineDriveSpec;
import dev.celestiacraft.steam_letters.client.models.SlBlockStateGen;
import dev.celestiacraft.steam_letters.common.register.SlBlockEntities;
import dev.celestiacraft.steam_letters.utils.BlockFluidInteractions;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SteamEngineBlock extends HorizontalDirectionalBlock implements IBE<SteamEngineBlockEntity>, IWrenchable {
	public static final MapCodec<SteamEngineBlock> CODEC = simpleCodec(SteamEngineBlock::new);
	public static final BooleanProperty LIT = BlockStateProperties.LIT;

	@Getter
	private final SteamEngineDriveSpec driveSpec;

	private final Supplier<BlockEntityType<? extends SteamEngineBlockEntity>> customBlockEntityType;

	public SteamEngineBlock(Properties properties) {
		this(properties, SteamEngineDriveSpec.builder().build(), null);
	}

	public SteamEngineBlock(Properties properties, SteamEngineDriveSpec driveSpec) {
		this(properties.noOcclusion(), driveSpec, null);
	}

	public SteamEngineBlock(Properties properties, SteamEngineDriveSpec driveSpec, Supplier<BlockEntityType<? extends SteamEngineBlockEntity>> customBlockEntityType) {
		super(properties);
		this.customBlockEntityType = customBlockEntityType;
		this.driveSpec = driveSpec;
		registerDefaultState(defaultBlockState()
				.setValue(FACING, Direction.NORTH)
				.setValue(LIT, false));
	}

	@Override
	protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(FACING, LIT));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState()
				.setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		Direction face = context.getClickedFace();
		if (!face.getAxis().isHorizontal()) {
			return InteractionResult.PASS;
		}
		Level level = context.getLevel();
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		level.setBlock(context.getClickedPos(), state.setValue(FACING, face), 3);
		return InteractionResult.SUCCESS;
	}

	@Override
	protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, BlockHitResult result) {
		InteractionResult outcome = BlockFluidInteractions.fillOnly(level, pos, player, hand, result.getDirection());
		if (outcome.consumesAction()) {
			return ItemInteractionResult.sidedSuccess(level.isClientSide());
		}

		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public Class<SteamEngineBlockEntity> getBlockEntityClass() {
		return SteamEngineBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends SteamEngineBlockEntity> getBlockEntityType() {
		return customBlockEntityType != null ? customBlockEntityType.get() : SlBlockEntities.STEAM_ENGINE.get();
	}

	public static NonNullBiConsumer<DataGenContext<Block, SteamEngineBlock>, RegistrateBlockstateProvider> genBlockState(String material) {
		return SlBlockStateGen.facingLit(FACING, LIT, "block/steam/engine/" + material, "block/steam/engine/" + material, 180);
	}
}