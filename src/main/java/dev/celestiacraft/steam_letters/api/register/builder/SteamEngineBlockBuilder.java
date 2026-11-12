package dev.celestiacraft.steam_letters.api.register.builder;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import dev.celestiacraft.steam_letters.api.machine.SteamEngineDriveSpec;
import dev.celestiacraft.steam_letters.common.block.engine.SteamEngineBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

/**
 * 蒸汽引擎的注册 Builder.
 * <p>
 * 引擎的参数分两套: 应力, 转速, 消耗与蒸汽罐容量落在 {@link SteamEngineDriveSpec} 上,
 * 由本类负责; 蒸汽相关的容量与产出仍走蒸汽设备自己的 spec.
 */
public class SteamEngineBlockBuilder<T extends SteamEngineBlock, P> extends BlockBuilder<T, P> {
	private final SteamEngineDriveSpec.Builder driveParts;

	protected SteamEngineBlockBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, SteamEngineDriveSpec.Builder driveParts, NonNullBiFunction<BlockBehaviour.Properties, SteamEngineDriveSpec, T> factory) {
		super(owner, parent, name, callback, (properties) -> {
			return factory.apply(properties, driveParts.build());
		}, () -> {
			return BlockBehaviour.Properties.of();
		});
		this.driveParts = driveParts;
	}

	/**
	 * 建立一个引擎 Builder, 并挂上 Registrate 的默认方块状态, 掉落表与语言文件.
	 */
	public static <T extends SteamEngineBlock, P> SteamEngineBlockBuilder<T, P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullBiFunction<BlockBehaviour.Properties, SteamEngineDriveSpec, T> factory) {
		SteamEngineBlockBuilder<T, P> builder = new SteamEngineBlockBuilder<>(owner, parent, name, callback, SteamEngineDriveSpec.builder(), factory);
		builder.defaultBlockstate().defaultLoot().defaultLang();
		return builder;
	}

	/**
	 * 普通蒸汽满负荷时的应力输出 (SU).
	 */
	public SteamEngineBlockBuilder<T, P> strength(double strength) {
		driveParts.strength(strength);
		return this;
	}

	public SteamEngineBlockBuilder<T, P> strength(DoubleSupplier strength) {
		driveParts.strength(strength);
		return this;
	}

	/**
	 * 输出转速 (RPM).
	 */
	public SteamEngineBlockBuilder<T, P> speed(double speed) {
		driveParts.speed(speed);
		return this;
	}

	public SteamEngineBlockBuilder<T, P> speed(DoubleSupplier speed) {
		driveParts.speed(speed);
		return this;
	}

	/**
	 * 满负荷时每 tick 消耗的蒸汽量 (mB/tick).
	 */
	public SteamEngineBlockBuilder<T, P> consumption(double consumption) {
		driveParts.consumption(consumption);
		return this;
	}

	public SteamEngineBlockBuilder<T, P> consumption(DoubleSupplier supplier) {
		driveParts.consumption(supplier);
		return this;
	}

	/**
	 * 引擎自带蒸汽罐的容量 (mB).
	 */
	public SteamEngineBlockBuilder<T, P> capacity(int capacity) {
		driveParts.capacity(capacity);
		return this;
	}

	public SteamEngineBlockBuilder<T, P> capacity(IntSupplier capacity) {
		driveParts.capacity(capacity);
		return this;
	}

	/**
	 * 引擎的材质 id, 只有材质相同的飞轮才能与本引擎配对.
	 */
	public SteamEngineBlockBuilder<T, P> material(ResourceLocation material) {
		driveParts.material(material);
		return this;
	}
}