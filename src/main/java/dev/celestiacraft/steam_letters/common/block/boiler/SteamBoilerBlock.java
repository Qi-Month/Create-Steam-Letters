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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SteamBoilerBlock extends Block implements IBE<SteamBoilerBlockEntity>, ISteamEngineBlock {
	private final SteamEngineSpec spec;
	private final Supplier<BlockEntityType<? extends SteamBoilerBlockEntity>> customBlockEntityType;

	public SteamBoilerBlock(Properties properties, SteamEngineSpec spec) {
		this(properties, spec, null);
	}

	public SteamBoilerBlock(Properties properties, SteamEngineSpec spec, Supplier<BlockEntityType<? extends SteamBoilerBlockEntity>> customBlockEntityType) {
		super(properties);
		this.spec = spec;
		this.customBlockEntityType = customBlockEntityType;
	}

	@Override
	public SteamEngineSpec getEngineSpec() {
		return spec;
	}

	@Override
	public Class<SteamBoilerBlockEntity> getBlockEntityClass() {
		return SteamBoilerBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends SteamBoilerBlockEntity> getBlockEntityType() {
		return customBlockEntityType != null ? customBlockEntityType.get() : SlBlockEntities.STEAM_BOILER.get();
	}

	@Override
	protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, BlockHitResult result) {
		InteractionResult outcome = BlockFluidInteractions.fillOnly(level, pos, player, hand, result.getDirection());
		if (outcome.consumesAction()) {
			return ItemInteractionResult.sidedSuccess(level.isClientSide());
		}

		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState(String material) {
		return SlBlockStateGen.simple("block/steam/boiler/" + material);
	}
}