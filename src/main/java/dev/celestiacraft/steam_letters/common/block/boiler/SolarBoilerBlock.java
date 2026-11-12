package dev.celestiacraft.steam_letters.common.block.boiler;

import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.steam_letters.api.machine.ISteamEngineBlock;
import dev.celestiacraft.steam_letters.api.machine.SteamEngineSpec;
import dev.celestiacraft.steam_letters.client.models.SlBlockStateGen;
import dev.celestiacraft.steam_letters.common.register.SlBlockEntities;
import dev.celestiacraft.steam_letters.utils.BlockFluidInteractions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SolarBoilerBlock extends Block implements IBE<SolarBoilerBlockEntity>, ISteamEngineBlock {
	private final SteamEngineSpec spec;
	private final Supplier<BlockEntityType<? extends SolarBoilerBlockEntity>> customBlockEntityType;

	public SolarBoilerBlock(Properties properties, SteamEngineSpec spec) {
		this(properties, spec, null);
	}

	public SolarBoilerBlock(Properties properties, SteamEngineSpec spec, Supplier<BlockEntityType<? extends SolarBoilerBlockEntity>> customBlockEntityType) {
		super(properties.noOcclusion());
		this.spec = spec;
		this.customBlockEntityType = customBlockEntityType;
	}

	@Override
	public SteamEngineSpec getEngineSpec() {
		return spec;
	}

	@Override
	public Class<SolarBoilerBlockEntity> getBlockEntityClass() {
		return SolarBoilerBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends SolarBoilerBlockEntity> getBlockEntityType() {
		return customBlockEntityType != null ? customBlockEntityType.get() : SlBlockEntities.SOLAR_BOILER.get();
	}

	@Override
	protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, BlockHitResult hit) {
		InteractionResult result = BlockFluidInteractions.fillOnly(level, pos, player, hand, hit.getDirection());
		if (result.consumesAction()) {
			return ItemInteractionResult.sidedSuccess(level.isClientSide());
		}

		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return Shapes.or(
				Block.box(0, 0, 0, 16, 2, 16),
				Block.box(0, 2, 0, 16, 14, 1),
				Block.box(0, 2, 15, 16, 14, 16),
				Block.box(0, 2, 1, 1, 14, 15),
				Block.box(15, 2, 1, 16, 14, 15),
				Block.box(0, 14, 0, 16, 16, 2),
				Block.box(0, 14, 14, 16, 16, 16),
				Block.box(0, 14, 2, 2, 16, 14),
				Block.box(14, 14, 2, 16, 16, 14),
				Block.box(2, 14, 2, 14, 14.05, 14)
		);
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState(String material) {
		return SlBlockStateGen.simple("block/steam/solar_boiler/" + material);
	}
}