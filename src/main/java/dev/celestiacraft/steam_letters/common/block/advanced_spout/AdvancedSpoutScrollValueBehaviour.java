package dev.celestiacraft.steam_letters.common.block.advanced_spout;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import dev.celestiacraft.steam_letters.config.common.AdvancedSpoutConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class AdvancedSpoutScrollValueBehaviour extends ScrollValueBehaviour {
	public static final BehaviourType<AdvancedSpoutScrollValueBehaviour> TYPE = new BehaviourType<>();

	public static final int MIN_PARALLEL = 1;
	public static final int MAX_PARALLEL = 20;
	public static final int MILESTONE_INTERVAL = 1;

	public static final String LANG_PARALLEL = "steam_letters.gui.advanced_spout.parallel";
	public static final String LANG_VALUE_PARALLEL = "steam_letters.gui.advanced_spout.value.parallel";

	private ValueSettings hovered;

	public AdvancedSpoutScrollValueBehaviour(Component label, SmartBlockEntity be, ValueBoxTransform slot) {
		super(label, be, slot);
		between(MIN_PARALLEL, MAX_PARALLEL);
		requiresWrench();
		value = Mth.clamp(AdvancedSpoutConfig.PARALLEL.get(), MIN_PARALLEL, MAX_PARALLEL);
	}

	@Override
	public BehaviourType<?> getType() {
		return TYPE;
	}

	@Override
	public int netId() {
		return 0;
	}

	/**
	 * 滑条只在手持扳手时出现, 也只在手持扳手时可调.
	 */
	@Override
	public boolean onlyVisibleWithWrench() {
		return true;
	}

	public int getParallel() {
		return Mth.clamp(value, MIN_PARALLEL, MAX_PARALLEL);
	}

	@Override
	public String formatValue() {
		ValueSettings setting = hovered;
		int amount = setting == null ? getParallel() : setting.value();
		return "x" + Mth.clamp(amount, MIN_PARALLEL, MAX_PARALLEL);
	}

	@Override
	public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
		ImmutableList<Component> rows = ImmutableList.of(Component.translatable(LANG_PARALLEL));
		return new ValueSettingsBoard(
				label,
				MAX_PARALLEL,
				MILESTONE_INTERVAL,
				rows,
				new ValueSettingsFormatter(this::formatSetting)
		);
	}

	@Override
	public ValueSettings getValueSettings() {
		return new ValueSettings(0, getParallel());
	}

	@Override
	public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlDown) {
		int amount = Mth.clamp(valueSetting.value(), MIN_PARALLEL, MAX_PARALLEL);
		if (amount == getParallel()) {
			return;
		}
		setValue(amount);
		playFeedbackSound(this);
	}

	@Override
	public void newSettingHovered(ValueSettings valueSetting) {
		hovered = valueSetting;
	}

	private MutableComponent formatSetting(ValueSettings valueSetting) {
		return Component.translatable(LANG_VALUE_PARALLEL, Mth.clamp(
				valueSetting.value(),
				MIN_PARALLEL,
				MAX_PARALLEL
		));
	}

	@Override
	public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(nbt, registries, clientPacket);
		nbt.putInt("SpoutParallel", getParallel());
	}

	@Override
	public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(nbt, registries, clientPacket);
		// 旧存档里 ScrollValue 存的是注液时间, 越界值一律夹回并行区间
		value = Mth.clamp(value, MIN_PARALLEL, MAX_PARALLEL);
		if (nbt.contains("SpoutParallel")) {
			value = Mth.clamp(nbt.getInt("SpoutParallel"), MIN_PARALLEL, MAX_PARALLEL);
		}
	}
}