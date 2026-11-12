package dev.celestiacraft.steam_letters.common.boiler;

import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import dev.celestiacraft.steam_letters.common.block.steam_pump.BronzeSteamPumpBlock;
import dev.celestiacraft.steam_letters.mixin.boiler.IFluidTankBlockEntityAccessor;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 统计贴在锅炉结构上的青铜蒸汽泵数量.
 * <p>
 * 判定方式与 Create 统计蒸汽引擎完全一致: 从结构内的每个锅炉方块朝六个方向看,
 * 该方向上是一个朝向背离锅炉的青铜泵, 也就是泵的背面(抽取侧)冲着锅炉.
 */
public class BoilerPumpScanner {
	/**
	 * @param controller 锅炉控制器
	 * @return 贴在本锅炉上的青铜泵数量, 控制器无效时返回 0
	 */
	public static int count(FluidTankBlockEntity controller) {
		Level level = controller.getLevel();

		if (level == null) {
			return 0;
		}

		IFluidTankBlockEntityAccessor accessor = (IFluidTankBlockEntityAccessor) controller;
		BlockPos controllerPos = controller.getBlockPos();
		int width = accessor.sl$getWidth();
		int height = accessor.sl$getHeight();
		int pumps = 0;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				for (int z = 0; z < width; z++) {
					BlockPos pos = controllerPos.offset(x, y, z);

					if (!FluidTankBlock.isTank(level.getBlockState(pos))) {
						continue;
					}

					for (Direction direction : Iterate.directions) {
						BlockState attachedState = level.getBlockState(pos.relative(direction));

						if (attachedState.getBlock() instanceof BronzeSteamPumpBlock && attachedState.getValue(DirectionalKineticBlock.FACING) == direction) {
							pumps++;
						}
					}
				}
			}
		}

		return pumps;
	}
}