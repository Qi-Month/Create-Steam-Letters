package dev.celestiacraft.steam_letters.common.register;

import com.tterrag.registrate.util.entry.FluidEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.api.item.SteamBucketItem;
import dev.celestiacraft.steam_letters.common.fluid.SteamFluid;
import dev.celestiacraft.steam_letters.common.fluid.SteamFluidFlowing;
import dev.celestiacraft.steam_letters.common.fluid.SteamFluidSource;
import dev.celestiacraft.steam_letters.tags.SlFluidTags;
import net.minecraft.tags.ItemTags;

public class SlFluids {
	public static final FluidEntry<SteamFluidFlowing> STEAM;
	public static final FluidEntry<SteamFluidFlowing> SUPERHEATED_STEAM;

	static {
		STEAM = SlApis.REGISTRATE.fluid(
						"steam",
						SlApis.loadResource("block/fluid/steam_still"),
						SlApis.loadResource("block/fluid/steam_flow"),
						SteamFluid.create(0xFAFAFA, () -> {
							return 1.0f / 8.0f;
						}, 0xFFFAFAFA),
						SteamFluidFlowing::new
				)
				.tag(SlFluidTags.STEAM)
				.source(SteamFluidSource::new)
				.bucket(SteamBucketItem::new)
				.tag(ItemTags.create(SlApis.loadCommon("buckets/steam")))
				.tag(ItemTags.create(SlApis.loadCommon("buckets")))
				.model(NonNullBiConsumer.noop())
				.build()
				.lang("Steam")
				.noBlock()
				.register();

		SUPERHEATED_STEAM = SlApis.REGISTRATE.fluid(
						"superheated_steam",
						SlApis.loadResource("block/fluid/superheated_steam_still"),
						SlApis.loadResource("block/fluid/superheated_steam_flow"),
						SteamFluid.create(0xFAFAFA, () -> {
							return 1.0f / 8.0f;
						}, 0xFFFAFAFA),
						SteamFluidFlowing::new
				)
				.tag(SlFluidTags.SUPERHEATED_STEAM)
				.source(SteamFluidSource::new)
				.bucket(SteamBucketItem::new)
				.model(NonNullBiConsumer.noop())
				.tag(ItemTags.create(SlApis.loadCommon("buckets/superheated_steam")))
				.tag(ItemTags.create(SlApis.loadCommon("buckets")))
				.build()
				.lang("Superheated Steam")
				.noBlock()
				.register();
	}

	public static void init() {
	}
}