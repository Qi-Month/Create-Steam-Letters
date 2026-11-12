package dev.celestiacraft.steam_letters.mixin.boiler;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import dev.celestiacraft.steam_letters.common.boiler.BoilerPumpScanner;
import dev.celestiacraft.steam_letters.common.boiler.SteamBoilerData;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 按节拍补扫贴在锅炉上的青铜蒸汽泵.
 * <p>
 * Create 只在锅炉方块变化与蒸汽引擎的放置 / 破坏时调用 {@code FluidTankBlock.updateBoilerState},
 * 往锅炉旁边放一台泵不会走到那里, 泵数会一直停在 0, 引擎与泵共用名额这件事就无从成立.
 * 而只挂泵不挂引擎时 {@code BoilerData.tick} 本身不会执行, 所以补扫必须挂在方块实体这一层.
 */
@Mixin(FluidTankBlockEntity.class)
public class FluidTankBlockEntityMixin {
	/**
	 * 与 Create 的供水采样同一节拍, 每 5 tick 重扫一次.
	 *
	 * @param info
	 */
	@Inject(method = "tick", at = @At("TAIL"), remap = false)
	private void sl$rescanAttachedPumps(CallbackInfo info) {
		FluidTankBlockEntity self = (FluidTankBlockEntity) (Object) this;
		Level level = self.getLevel();

		if (level == null || level.isClientSide() || !self.isController()) {
			return;
		}

		if (level.getGameTime() % 5L != 0L) {
			return;
		}

		if (self.boiler instanceof SteamBoilerData data) {
			data.sl$setAttachedPumps(BoilerPumpScanner.count(self));
		}
	}
}