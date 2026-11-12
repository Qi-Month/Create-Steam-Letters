package dev.celestiacraft.steam_letters.mixin.boiler;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 读取大锅炉结构尺寸.
 * <p>
 * {@code width} / {@code height} 在 {@link FluidTankBlockEntity} 中是 protected,
 * 且与本模组不在同一个包, 因此只能通过访问器取出.
 * <p>
 * 只有拿到这两项才能按与 {@code BoilerData.evaluate} 相同的方式遍历结构表面,
 * 从而统计贴在锅炉上的青铜蒸汽泵.
 */
@Mixin(FluidTankBlockEntity.class)
public interface IFluidTankBlockEntityAccessor {
	@Accessor(value = "width", remap = false)
	int sl$getWidth();

	@Accessor(value = "height", remap = false)
	int sl$getHeight();
}