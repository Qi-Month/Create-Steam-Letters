package dev.celestiacraft.steam_letters.client.lang;

import com.simibubi.create.foundation.utility.CreateLang;
import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.api.SlTranKeys;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SlLang {
	public static LangBuilder lang() {
		return new LangBuilder(CreateLettersSteam.MODID);
	}

	public static void isShiftDown(@NotNull List<Component> tooltip) {
		if (Screen.hasShiftDown()) {
			return;
		}

		MutableComponent shift = Component.literal("Shift")
				.withStyle(ChatFormatting.GRAY);

		CreateLang.translate("tooltip.holdForDescription", shift)
				.style(ChatFormatting.DARK_GRAY)
				.addTo(tooltip);
	}

	public static boolean isShiftDown() {
		return Screen.hasShiftDown();
	}

	/**
	 * 翻译指定的语言键并返回 {@link LangBuilder}, 支持动态参数.
	 * <p>
	 * 自动添加模组 id 前缀到语言键. 例如, 传入 {@code "tooltip.example"}
	 * 会查找 {@code "steam_letters.tooltip.example"} 键.
	 *
	 * @param langKey 语言键(不含模组 id 前缀)
	 * @param args    格式化参数, 用于替换翻译文本中的占位符(如 {@code %s})
	 * @return 包含翻译结果的 LangBuilder, 可继续链式调用
	 * @see #translateDirect(String, Object...) 直接获取 Component 而非 Builder
	 */
	public static LangBuilder translate(String langKey, Object... args) {
		return lang().add(Component.translatable(
				SlTranKeys.langKey(langKey),
				LangBuilder.resolveBuilders(args)
		));
	}

	public static MutableComponent translateDirect(String langKey, Object... args) {
		return Component.translatable(
				SlTranKeys.langKey(langKey),
				LangBuilder.resolveBuilders(args)
		);
	}
}