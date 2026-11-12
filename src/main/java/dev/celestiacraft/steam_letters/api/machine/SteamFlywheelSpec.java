package dev.celestiacraft.steam_letters.api.machine;

import dev.celestiacraft.steam_letters.common.block.flywheel.SteamFlywheelBlockEntity;
import dev.celestiacraft.steam_letters.common.register.SlBlockEntities;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

/**
 * 蒸汽飞轮在注册时写入的参数.
 * <p>
 * 与 {@link SteamEngineSpec} 不同, 飞轮的取值在注册期就已经确定, 不需要推迟到运行时,
 * 因此这里直接收着材质名与方块实体类型, 而不是取值方式.
 */
@AllArgsConstructor
public class SteamFlywheelSpec {
	@Getter
	private final ResourceLocation material;
	private final Supplier<BlockEntityType<? extends SteamFlywheelBlockEntity>> blockEntityType;

	/**
	 * @return 方块实体类型, 未指定时回退到内置的飞轮类型
	 */
	public BlockEntityType<? extends SteamFlywheelBlockEntity> getBlockEntityType() {
		return blockEntityType != null ? blockEntityType.get() : SlBlockEntities.STEAM_FLYWHEEL.get();
	}
}