package dev.celestiacraft.steam_letters.compat.shaders;

import java.lang.reflect.Method;
import java.util.function.BooleanSupplier;

/**
 * 光影 (Iris / Oculus) 探测.
 * <p>
 * 只回答一个问题: 现在是否有光影包正在生效. 走反射调用 Iris 的稳定 API
 * {@code net.irisshaders.iris.api.v0.IrisApi}, 因此没装光影时不会因为缺类而崩, 也不必把
 * Iris 写进依赖表.
 * <p>
 * 用途见 {@code SteamFlywheelRenderer}: Create 的 rotating 实例类型自带一套自定义顶点程序,
 * 在光影下可能被光影的着色器接管并整批丢弃, 这时需要让方块实体渲染器继续兜底,
 * 否则玩家看到的就是"方块还在, 轮盘整个不见".
 */
public class ShaderCompat {
	private static final BooleanSupplier SHADER_PACK_IN_USE = resolve();

	public static boolean isShaderPackInUse() {
		return SHADER_PACK_IN_USE.getAsBoolean();
	}

	private static BooleanSupplier resolve() {
		try {
			Class<?> api = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
			Object instance = api.getMethod("getInstance").invoke(null);
			Method method = api.getMethod("isShaderPackInUse");

			return () -> {
				try {
					return method.invoke(instance) instanceof Boolean value && value;
				} catch (Throwable throwable) {
					return false;
				}
			};
		} catch (Throwable throwable) {
			return () -> false;
		}
	}
}