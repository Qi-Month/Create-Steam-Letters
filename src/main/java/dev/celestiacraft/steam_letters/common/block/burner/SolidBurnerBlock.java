package dev.celestiacraft.steam_letters.common.block.burner;

import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.steam_letters.api.machine.SteamEngineSpec;
import dev.celestiacraft.steam_letters.client.models.SlBlockStateGen;
import dev.celestiacraft.steam_letters.common.register.SlBlockEntities;
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

public class SolidBurnerBlock extends AbstractBurnerBlock implements IBE<SolidBurnerBlockEntity> {
	private final Supplier<BlockEntityType<? extends SolidBurnerBlockEntity>> customBlockEntityType;

	public SolidBurnerBlock(Properties properties, SteamEngineSpec spec) {
		this(properties, spec, null);
	}

	public SolidBurnerBlock(Properties properties, SteamEngineSpec spec, Supplier<BlockEntityType<? extends SolidBurnerBlockEntity>> customBlockEntityType) {
		super(properties, spec);
		this.customBlockEntityType = customBlockEntityType;
	}

	@Override
	public Class<SolidBurnerBlockEntity> getBlockEntityClass() {
		return SolidBurnerBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends SolidBurnerBlockEntity> getBlockEntityType() {
		return customBlockEntityType != null ? customBlockEntityType.get() : SlBlockEntities.SOLID_BURNER.get();
	}

	@Override
	protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
		if (stack.isEmpty() || !(level.getBlockEntity(pos) instanceof SolidBurnerBlockEntity burner)) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}

		if (level.isClientSide()) {
			return burner.isFuel(stack) ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}

		return burner.insertFuel(stack) ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState(String material) {
		return SlBlockStateGen.facingLit("block/steam/solid_burner/" + material, "block/steam/solid_burner/" + material + "_lit");
	}
}