package dev.celestiacraft.steam_letters.api.client.lang;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.data.PackOutput;

public class EnglishLangProvider extends RegistrateLangProvider implements SlLangSugar {
	public static final ProviderType<EnglishLangProvider> TYPE;

	private final AbstractRegistrate<?> owner;

	public EnglishLangProvider(AbstractRegistrate<?> owner, PackOutput output) {
		super(owner, output);
		this.owner = owner;
	}

	static {
		TYPE = ProviderType.register("lang", (parent, event) -> {
			return new EnglishLangProvider(parent, event.getGenerator().getPackOutput());
		});
	}

	@Override
	protected void addTranslations() {
		// 注册链上的 .lang() 条目全部挂在 ProviderType.LANG 这个键下, 必须用它取数据.
		owner.genData(ProviderType.LANG, this);
	}
}