package dev.celestiacraft.steam_letters.common.boiler;

import com.simibubi.create.content.fluids.tank.BoilerData;
import com.simibubi.create.foundation.fluid.FluidHelper;
import dev.celestiacraft.steam_letters.tags.SlFluidTags;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

/**
 * 锅炉输入处理器, 除了接受水(原版行为)外, 还接受 {@code forge:steam} / {@code forge:superheated_steam} 流体
 * <p>
 * 注进来的蒸汽直接存进锅炉的蒸汽缓冲, 不进 Create 的槽位; 过热蒸汽会把等级顶到 18, 普通蒸汽顶到 9.
 * <p>
 * 等级由缓冲存量支撑: 只要缓冲里还有蒸汽, 供汽中断后锅炉仍按原等级继续运行, 抽空才退回自产汽模式,
 * 因此管网间歇供汽不会让锅炉停机.
 * <p>
 * 输出侧 {@link #drain} / {@link #getFluidInTank} 都指向同一份缓冲, 于是引擎直抽, 青铜蒸汽泵与管网取汽
 * 共享同一份蒸汽, 灌进来的蒸汽也能被原样抽走.
 */
public class SteamBoilerFluidHandler extends BoilerData.BoilerFluidHandler {
	private final BoilerData owner;

	public SteamBoilerFluidHandler(BoilerData owner) {
		owner.super();
		this.owner = owner;
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return super.isFluidValid(tank, stack) || isSteam(stack);
	}

	@Override
	public int fill(FluidStack stack, FluidAction action) {
		SteamBoilerData steamBoiler = steamData();

		if (steamBoiler != null && FluidHelper.isWater(stack.getFluid())) {
			return sl$fillWaterBuffer(steamBoiler, stack, action);
		}

		// 蒸汽直接进蒸汽缓冲, 不写进 Create 的槽位:
		// 槽位对外只暴露缓冲, 只有进缓冲的蒸汽才能被引擎与蒸汽泵继续抽走.
		if (steamBoiler != null && isSteam(stack)) {
			if (action.simulate()) {
				int room = Math.max(0, steamBoiler.sl$getSteamCapacity() - steamBoiler.sl$peekSteam().getAmount());

				return Math.min(stack.getAmount(), room);
			}

			return steamBoiler.sl$notifySteamInput(steamHeatLevel(stack), stack.getAmount());
		}

		// 既不是水也不是蒸汽的流体交给 Create 原本的槽位
		return super.fill(stack, action);
	}

	/**
	 * 水先进锅炉自己的水罐, 产汽时再按需扣.
	 * <p>
	 * 罐满时拒收, 让管网自己憋住. 这与「蒸汽缓冲满时拒水」不同: 等级此时由罐里的存量支撑,
	 * 不再依赖 Create 的流入量采样, 所以拒收不会把等级拉塌.
	 *
	 * @param steamBoiler
	 * @param stack
	 * @param action
	 * @return 实际接受的水量(mB)
	 */
	private int sl$fillWaterBuffer(SteamBoilerData steamBoiler, FluidStack stack, FluidAction action) {
		int capacity = steamBoiler.sl$getWaterCapacity();

		// 没有水罐时退回 Create 原本的做法: 水一进来就被采样并消耗掉
		if (capacity <= 0) {
			return super.fill(stack, action);
		}

		int room = capacity - steamBoiler.sl$getWaterStored();

		if (room <= 0) {
			return 0;
		}

		int accepted = Math.min(stack.getAmount(), room);

		if (action.execute()) {
			steamBoiler.sl$fillWater(accepted);
		}
		return accepted;
	}

	@Override
	public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
		SteamBoilerData steamBoiler = steamData();

		if (steamBoiler == null || resource.isEmpty()) {
			return FluidStack.EMPTY;
		}

		FluidStack content = steamBoiler.sl$peekSteam();

		if (content.isEmpty() || !FluidStack.isSameFluidSameComponents(content, resource)) {
			return FluidStack.EMPTY;
		}

		int drained = steamBoiler.sl$drainSteam(resource.getAmount(), action.simulate());

		return drained <= 0 ? FluidStack.EMPTY : new FluidStack(content.getFluid(), drained);
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		SteamBoilerData steamBoiler = steamData();

		if (steamBoiler == null) {
			return FluidStack.EMPTY;
		}

		FluidStack content = steamBoiler.sl$peekSteam();

		if (content.isEmpty()) {
			return FluidStack.EMPTY;
		}

		int drained = steamBoiler.sl$drainSteam(maxDrain, action.simulate());

		return drained <= 0 ? FluidStack.EMPTY : new FluidStack(content.getFluid(), drained);
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int tank) {
		SteamBoilerData steamBoiler = steamData();

		return steamBoiler == null ? FluidStack.EMPTY : steamBoiler.sl$peekSteam();
	}

	@Override
	public int getTankCapacity(int tank) {
		SteamBoilerData steamBoiler = steamData();

		if (steamBoiler == null) {
			return 0;
		}

		return steamBoiler.sl$getSteamCapacity();
	}

	private SteamBoilerData steamData() {
		return owner instanceof SteamBoilerData steamBoiler ? steamBoiler : null;
	}

	private boolean isSteam(FluidStack stack) {
		Fluid fluid = stack.getFluid();

		return fluid.defaultFluidState().is(SlFluidTags.STEAM)
				|| fluid.defaultFluidState().is(SlFluidTags.SUPERHEATED_STEAM);
	}

	private int steamHeatLevel(FluidStack stack) {
		Fluid fluid = stack.getFluid();

		return fluid.defaultFluidState().is(SlFluidTags.SUPERHEATED_STEAM) ? 18 : 9;
	}
}