package dev.celestiacraft.steam_letters.common.block.burner;

import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.steam_letters.api.machine.SteamEngineSpec;
import dev.celestiacraft.steam_letters.client.models.SlBlockStateGen;
import dev.celestiacraft.steam_letters.common.register.SlBlockEntities;
import dev.celestiacraft.steam_letters.utils.BlockFluidInteractions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class FluidBurnerBlock extends AbstractBurnerBlock implements IBE<FluidBurnerBlockEntity> {
	private final Supplier<BlockEntityType<? extends FluidBurnerBlockEntity>> customBlockEntityType;

	public FluidBurnerBlock(Properties properties, SteamEngineSpec spec) {
		this(properties, spec, null);
	}

	public FluidBurnerBlock(Properties properties, SteamEngineSpec spec, Supplier<BlockEntityType<? extends FluidBurnerBlockEntity>> customBlockEntityType) {
		super(properties, spec);
		this.customBlockEntityType = customBlockEntityType;
	}

	@Override
	public Class<FluidBurnerBlockEntity> getBlockEntityClass() {
		return FluidBurnerBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends FluidBurnerBlockEntity> getBlockEntityType() {
		return customBlockEntityType != null ? customBlockEntityType.get() : SlBlockEntities.FLUID_BURNER.get();
	}

	/**
	 * 允许把容器里的流体灌进燃料罐, 空容器不处理 —— 因此空桶抽不走任何东西.
	 */
	@Override
	protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, BlockHitResult hit) {
		InteractionResult outcome = BlockFluidInteractions.fillOnly(level, pos, player, hand, hit.getDirection());
		if (outcome.consumesAction()) {
			return ItemInteractionResult.sidedSuccess(level.isClientSide());
		}

		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState(String material) {
		return SlBlockStateGen.facingLit("block/steam/fluid_burner/" + material, "block/steam/fluid_burner/" + material + "_lit");
	}
}