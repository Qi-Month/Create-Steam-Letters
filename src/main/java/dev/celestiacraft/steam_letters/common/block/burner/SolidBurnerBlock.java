package dev.celestiacraft.steam_letters.common.block.burner;

import com.simibubi.create.foundation.block.IBE;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;

import dev.celestiacraft.steam_letters.client.models.SlBlockStateGen;
import dev.celestiacraft.steam_letters.api.machine.SteamEngineSpec;
import dev.celestiacraft.steam_letters.common.register.SlBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.Block;
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
	public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
		ItemStack stack = player.getItemInHand(hand);
		if (stack.isEmpty() || !(level.getBlockEntity(pos) instanceof SolidBurnerBlockEntity burner)) {
			return InteractionResult.PASS;
		}

		if (level.isClientSide()) {
			return burner.isFuel(stack) ? InteractionResult.SUCCESS : InteractionResult.PASS;
		}

		// 容器燃料不进燃料槽: 当场抽掉流体, 空容器还给玩家, 背包塞不下就掉在脚下
		ItemStack container = burner.consumeContainerFuel(stack, 1, false);
		if (!container.isEmpty()) {
			stack.shrink(1);
			if (!player.getInventory().add(container)) {
				player.drop(container, false);
			}
			return InteractionResult.SUCCESS;
		}

		return burner.insertFuel(stack) ? InteractionResult.SUCCESS : InteractionResult.PASS;
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState(String material) {
		return SlBlockStateGen.facingLit("block/steam/solid_burner/" + material, "block/steam/solid_burner/" + material + "_lit");
	}
}