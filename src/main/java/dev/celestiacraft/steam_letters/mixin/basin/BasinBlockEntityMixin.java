package dev.celestiacraft.steam_letters.mixin.basin;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinInventory;
import dev.celestiacraft.steam_letters.common.block.bronze_basin.BronzeBasinBlockEntity;
import dev.celestiacraft.steam_letters.common.block.bronze_basin.BronzeBasinInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BasinBlockEntity.class)
public class BasinBlockEntityMixin {
	@Redirect(
			method = "<init>",
			remap = false,
			at = @At(
					value = "NEW",
					target = "com/simibubi/create/content/processing/basin/BasinInventory",
					ordinal = 0,
					remap = false
			)
	)
	private BasinInventory sl$createInputInventory(int slots, BasinBlockEntity blockEntity) {
		if (blockEntity instanceof BronzeBasinBlockEntity) {
			return new BronzeBasinInventory(BronzeBasinBlockEntity.INPUT_SLOT_COUNT, blockEntity);
		}
		return new BasinInventory(slots, blockEntity);
	}
}