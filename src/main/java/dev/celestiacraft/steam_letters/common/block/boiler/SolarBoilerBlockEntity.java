package dev.celestiacraft.steam_letters.common.block.boiler;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import dev.celestiacraft.steam_letters.api.SlTranKeys;
import dev.celestiacraft.steam_letters.client.lang.SlLang;
import dev.celestiacraft.steam_letters.config.common.SolarBoilerConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.List;
import java.util.Locale;

public class SolarBoilerBlockEntity extends AbstractBoilerBlockEntity implements IHaveGoggleInformation {
	/**
	 * 内置青铜太阳能锅炉的默认参数: 罐 10000 mB, 晴天满日照下产 12 mB/tick 蒸汽;
	 * 雨天/雷暴/人造光按配置折扣.
	 */
	public static final int DEFAULT_CAPACITY = 10000;
	public static final double DEFAULT_PRODUCTION = 12.0D;
	/**
	 * 太阳光条件检查的高度 (格): 正上方 1 至 16 格内不能有遮罩方块.
	 */
	private static final int SKY_CHECK_HEIGHT = 16;
	private final SolarBoilerSmoothFluid water;
	private final SolarBoilerSmoothFluid steam;

	private double cachedEfficiency = -1.0D;

	/**
	 * 上一次同步给客户端的两种流体量, 用于让玻璃顶下的液面跟着服务端变化.
	 */
	private int syncedInput = -1;
	private int syncedOutput = -1;

	public SolarBoilerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		water = new SolarBoilerSmoothFluid(getInputTank());
		steam = new SolarBoilerSmoothFluid(getOutputTank());
	}

	@Override
	public void tick() {
		super.tick();
		if (level != null && level.isClientSide()) {
			water.tick();
			steam.tick();
		}
	}

	/**
	 * 渲染用的水面高度 (0..1), 已经在两次 tick 之间做过插值.
	 */
	public float getRenderedWaterLevel(float partialTick) {
		return water.getLevel(partialTick);
	}

	/**
	 * 渲染用的蒸汽高度 (0..1), 已经在两次 tick 之间做过插值.
	 */
	public float getRenderedSteamLevel(float partialTick) {
		return steam.getLevel(partialTick);
	}

	/**
	 * 最后一次见过的非空水, 用于渲染水体; 不参与序列化.
	 */
	public FluidStack getRenderedWater() {
		return water.getRendered();
	}

	/**
	 * 最后一次见过的非空蒸汽, 用于渲染气体; 不参与序列化.
	 */
	public FluidStack getRenderedSteam() {
		return steam.getRendered();
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
		cachedEfficiency = computeEfficiency();
		syncFluidsToClient();
	}

	/**
	 * {@link FluidTank} 内容变化只会标脏, 不会主动发包; 这里每 lazyTick 比对一次水与蒸汽的量,
	 * 变了才同步, 否则客户端的两层液面永远停在方块加载时的那一刻.
	 */
	private void syncFluidsToClient() {
		if (level == null || level.isClientSide()) {
			return;
		}

		int input = getInputTank().getFluidAmount();
		int output = getOutputTank().getFluidAmount();
		if (input == syncedInput && output == syncedOutput) {
			return;
		}

		syncedInput = input;
		syncedOutput = output;
		sendData();
	}

	@Override
	protected double getCurrentEfficiency() {
		if (level == null) {
			return 0.0D;
		}
		if (cachedEfficiency < 0.0D) {
			cachedEfficiency = computeEfficiency();
		}
		return cachedEfficiency;
	}

	/**
	 * 太阳能锅炉不接燃烧室, 因此永远不会进入过热状态, 始终生产普通蒸汽.
	 */
	@Override
	protected boolean isSuperheated() {
		return false;
	}

	private double computeEfficiency() {
		if (level == null) {
			return 0.0D;
		}

		BlockPos above = worldPosition.above();

		if (hasSkyAccess(above)) {
			return SolarBoilerConfig.getSunlightEfficiency() * getWeatherMultiplier(above);
		}

		if (hasArtificialLight(above)) {
			return SolarBoilerConfig.getArtificialLightEfficiency() * getWeatherMultiplier(above);
		}

		return 0.0D;
	}

	/**
	 * 太阳光条件: 正上方 1 至 16 格内没有遮罩方块.
	 * <p>
	 * 直接看方块本身, 不看天空光亮度. 亮度只在区块重算时刷新, 客户端进入世界之后就冻住了,
	 * 整合包里还可能被其它模组改写; 而方块状态两端随时同步, 判定结果始终和玩家看到的一致.
	 * 玻璃, 树叶, 单个台阶这类不阻挡天空光的方块按透明处理 (propagatesSkylightDown), 不构成遮罩.
	 */
	private boolean hasSkyAccess(BlockPos above) {
		// 维度自己就没有天空光 (下界), 太阳光这一关不成立, 只能靠人造光.
		if (!level.dimensionType().hasSkyLight()) {
			return false;
		}
		if (!SolarBoilerConfig.isSkyLightUsable(level)) {
			return false;
		}

		for (int i = 0; i < SKY_CHECK_HEIGHT; i++) {
			BlockPos pos = above.above(i);
			if (pos.getY() >= level.getMaxBuildHeight()) {
				// 已经探到世界高度上限, 再往上只有天空, 不构成遮罩
				break;
			}
			if (!level.isLoaded(pos)) {
				// 未加载的区块不能当作无遮挡, 否则区块边缘的锅炉会凭空满效率工作
				return false;
			}
			if (!level.getBlockState(pos).propagatesSkylightDown(level, pos)) {
				return false;
			}
		}

		return true;
	}

	private boolean hasArtificialLight(BlockPos above) {
		int light = Math.max(level.getBrightness(LightLayer.BLOCK, worldPosition), level.getBrightness(LightLayer.BLOCK, above));
		return light >= SolarBoilerConfig.getArtificialLightThreshold();
	}

	private double getWeatherMultiplier(BlockPos above) {
		if (level.dimension().equals(Level.END)) {
			return 1.0D;
		}
		return SolarBoilerConfig.getWeatherMultiplier(level, above);
	}

	/**
	 * 当前生效的光照来源. 判定顺序与 {@link #getCurrentEfficiency()} 保持一致, 客户端同样可调用.
	 */
	public BoilerLightSource getLightSource() {
		if (level == null) {
			return BoilerLightSource.NONE;
		}

		BlockPos above = worldPosition.above();
		boolean sky = hasSkyAccess(above);

		if (!sky && !hasArtificialLight(above)) {
			return BoilerLightSource.NONE;
		}

		// 末地没有天气, 无遮挡时恒为太阳光
		if (level.dimension().equals(Level.END)) {
			return sky ? BoilerLightSource.SUNLIGHT : BoilerLightSource.NONE;
		}

		if (level.isThundering() && (level.isRainingAt(above) || level.isRaining())) {
			return BoilerLightSource.THUNDER;
		}
		if (level.isRainingAt(above)) {
			return BoilerLightSource.RAIN;
		}

		return sky ? BoilerLightSource.SUNLIGHT : BoilerLightSource.ARTIFICIAL_LIGHT;
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		SlLang.translate(SlTranKeys.GOGGLE_SOLAR_BOILER)
				.style(ChatFormatting.GRAY)
				.forGoggles(tooltip);

		double efficiency = computeEfficiency();
		// 满速产出未配置 (0) 时不产汽, 此时即便光照满足也只能算待机.
		double fullOutput = spec.getProduction();
		boolean working = efficiency > 0.0D && fullOutput > 0.0D;

		SlLang.translate(working ? SlTranKeys.GOGGLE_SOLAR_BOILER_WORKING : SlTranKeys.GOGGLE_SOLAR_BOILER_IDLE)
				.style(working ? ChatFormatting.GREEN : ChatFormatting.RED)
				.style(ChatFormatting.BOLD)
				.forGoggles(tooltip, 1);

		BoilerLightSource source = getLightSource();
		SlLang.translate(lightSourceKey(source))
				.style(lightSourceColor(source))
				.forGoggles(tooltip, 1);

		// 实际产汽速率 = 满速产出 x 当前环境效率; 占满速比例与效率是同一个数, 只是换了说法更直观.
		double production = fullOutput * efficiency;

		SlLang.translate(SlTranKeys.GOGGLE_SOLAR_BOILER_PRODUCTION)
				.style(ChatFormatting.GRAY)
				.text(ChatFormatting.DARK_GRAY, ": ")
				.add(Component.literal(String.format(Locale.ROOT, "%.1f", production) + " mB/t")
						.withStyle(working ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY))
				.forGoggles(tooltip, 1);

		SlLang.translate(SlTranKeys.GOGGLE_SOLAR_BOILER_RATE)
				.style(ChatFormatting.GRAY)
				.text(ChatFormatting.DARK_GRAY, ": ")
				.add(Component.literal(String.format(Locale.ROOT, "%.0f%%", working ? efficiency * 100.0D : 0.0D))
						.withStyle(working ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY))
				.forGoggles(tooltip, 1);

		return true;
	}

	private static String lightSourceKey(BoilerLightSource source) {
		return switch (source) {
			case SUNLIGHT -> SlTranKeys.GOGGLE_SOLAR_BOILER_LIGHT_SUNLIGHT;
			case RAIN -> SlTranKeys.GOGGLE_SOLAR_BOILER_LIGHT_RAIN;
			case THUNDER -> SlTranKeys.GOGGLE_SOLAR_BOILER_LIGHT_THUNDER;
			case ARTIFICIAL_LIGHT -> SlTranKeys.GOGGLE_SOLAR_BOILER_LIGHT_ARTIFICIAL;
			case NONE -> SlTranKeys.GOGGLE_SOLAR_BOILER_LIGHT_NONE;
		};
	}

	private static ChatFormatting lightSourceColor(BoilerLightSource source) {
		return switch (source) {
			case SUNLIGHT -> ChatFormatting.AQUA;
			case RAIN, THUNDER, ARTIFICIAL_LIGHT -> ChatFormatting.YELLOW;
			case NONE -> ChatFormatting.RED;
		};
	}
}