package dev.celestiacraft.steam_letters.common.block.boiler;

import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.api.machine.ISteamEngineBlock;
import dev.celestiacraft.steam_letters.api.machine.SteamEngineSpec;
import dev.celestiacraft.steam_letters.common.register.SlBlockEntities;
import dev.celestiacraft.steam_letters.common.register.SlItems;
import dev.celestiacraft.steam_letters.utils.BlockFluidInteractions;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * 多方块锅炉储罐.
 * <p>
 * 拼接规则与机械动力原版的流体储罐完全一致, 因此直接继承 {@link FluidTankBlock}:
 * 上下左右相邻的同类方块会连成一个结构, 由最低层的角块担任控制器.
 * 与储罐不同的是它固定不显示窗口, 并且在控制器那一格接受过热蒸汽组件.
 */
public class SteamBoilerBlock extends FluidTankBlock implements ISteamEngineBlock {
	private static final ResourceLocation DEFAULT_TEXTURE = SlApis.loadResource("block/steam/boiler/bronze");

	private final SteamEngineSpec spec;
	private final Supplier<BlockEntityType<? extends SteamBoilerBlockEntity>> customBlockEntityType;
	@Getter
	private final ResourceLocation textureDirectory;

	public SteamBoilerBlock(Properties properties, SteamEngineSpec spec) {
		this(properties, spec, null, DEFAULT_TEXTURE);
	}

	public SteamBoilerBlock(Properties properties, SteamEngineSpec spec, ResourceLocation textureDirectory) {
		this(properties, spec, null, textureDirectory);
	}

	public SteamBoilerBlock(Properties properties, SteamEngineSpec spec, Supplier<BlockEntityType<? extends SteamBoilerBlockEntity>> customBlockEntityType, ResourceLocation textureDirectory) {
		super(properties, false);
		this.spec = spec;
		this.customBlockEntityType = customBlockEntityType;
		this.textureDirectory = textureDirectory;
	}

	@Override
	public SteamEngineSpec getEngineSpec() {
		return spec;
	}

	@Override
	public BlockEntityType<? extends SteamBoilerBlockEntity> getBlockEntityType() {
		return customBlockEntityType != null ? customBlockEntityType.get() : SlBlockEntities.STEAM_BOILER.get();
	}

	/**
	 * 锅炉没有窗口形态, 扳手不再切换外观.
	 */
	@Override
	public InteractionResult onWrenched(BlockState state, net.minecraft.world.item.context.UseOnContext context) {
		return InteractionResult.PASS;
	}

	@Override
	protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, BlockHitResult result) {
		FluidTankBlockEntity tank = getBlockEntity(level, pos);
		SteamBoilerBlockEntity controller = tank instanceof SteamBoilerBlockEntity boiler ? boiler.getControllerBoiler() : null;

		if (controller != null && tryComponent(controller, stack, player, hand, level)) {
			return ItemInteractionResult.sidedSuccess(level.isClientSide());
		}

		InteractionResult outcome = BlockFluidInteractions.fillOnly(level, pos, player, hand, result.getDirection());
		if (outcome.consumesAction()) {
			return ItemInteractionResult.sidedSuccess(level.isClientSide());
		}

		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	/**
	 * 过热蒸汽组件只装在控制器那一格: 手持组件安装, 空手拆下.
	 *
	 * @return 本次交互是否已被组件处理
	 */
	private boolean tryComponent(SteamBoilerBlockEntity controller, ItemStack stack, Player player, InteractionHand hand, Level level) {
		if (stack.is(SlItems.SH_STEAM_COMPONENT.get())) {
			if (controller.hasComponent()) {
				return false;
			}
			if (level.isClientSide()) {
				return true;
			}
			controller.setComponent(true);
			if (!player.isCreative()) {
				stack.shrink(1);
			}
			return true;
		}

		if (hand != InteractionHand.MAIN_HAND || !stack.isEmpty() || !controller.hasComponent()) {
			return false;
		}
		if (level.isClientSide()) {
			return true;
		}
		controller.setComponent(false);
		if (!player.isCreative()) {
			Block.popResource(level, controller.getBlockPos(), SlItems.SH_STEAM_COMPONENT.asStack());
		}
		return true;
	}

	/**
	 * 结构被拆掉时, 装在控制器上的过热蒸汽组件直接掉落, 不会随方块一起消失.
	 */
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		if (state.getBlock() == newState.getBlock()) {
			return;
		}
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof SteamBoilerBlockEntity boiler && boiler.isController() && boiler.hasComponent()) {
			boiler.setComponent(false);
			Block.popResource(level, pos, SlItems.SH_STEAM_COMPONENT.asStack());
		}
		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	/**
	 * 每种状态组合都要能解析到同一个模型, 否则客户端的模型替换会漏掉部分状态.
	 */
	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState(String material) {
		return (context, provider) -> {
			ModelFile model = provider.models().getExistingFile(provider.modLoc("block/steam/boiler/" + material));
			provider.getVariantBuilder(context.get())
					.forAllStates((state) -> {
						return ConfiguredModel.builder()
								.modelFile(model)
								.build();
					});
		};
	}
}