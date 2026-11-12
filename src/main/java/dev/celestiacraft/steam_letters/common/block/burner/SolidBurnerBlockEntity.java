package dev.celestiacraft.steam_letters.common.block.burner;

import dev.celestiacraft.steam_letters.common.recipe.type.SolidFuelRecipe;
import dev.celestiacraft.steam_letters.common.register.SlBlockEntities;
import dev.celestiacraft.steam_letters.tags.SlItemTags;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * 固体燃烧室的方块实体.
 * <p>
 * 燃料匹配顺序: 自定义 {@code steam_letters:solid_fuel} 配方优先, 没命中才回退到原版熔炉燃料表;
 * 两者都不匹配的物品不能作为燃料. 配方只在「点燃下一份燃料」时查一次并缓存,
 * 不会每 tick 扫描配方表.
 */
public class SolidBurnerBlockEntity extends AbstractBurnerBlockEntity {
	private static final String NBT_FUEL = "Fuel";
	/**
	 * 燃料槽容量, 允许玩家一次性囤一小堆.
	 */
	public static final int FUEL_SLOT_LIMIT = 64;

	@Getter
	@Setter
	private ItemStack fuel = ItemStack.EMPTY;

	/**
	 * 上一次解析燃烧时间用的物品与结果, {@code cacheValid} 为 false 时表示还没有可用缓存.
	 */
	private ItemStack burnTimeKey = ItemStack.EMPTY;
	private int burnTimeValue;
	private boolean burnTimeSuperheated;
	private boolean burnTimeCacheValid;

	protected IItemHandler itemCapability;

	public SolidBurnerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		itemCapability = new FuelItemHandler(this);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		invalidateBurnTimeCache();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		invalidateBurnTimeCache();
	}

	@Override
	protected boolean tickFuel() {
		if (getBurnTime() > 0) {
			setBurnTime(getBurnTime() - 1);
			return true;
		}

		if (fuel.isEmpty()) {
			return false;
		}

		int time = resolveBurnTime(fuel);
		if (time <= 0) {
			return false;
		}

		fuel.shrink(1);
		if (fuel.isEmpty()) {
			fuel = ItemStack.EMPTY;
		}
		setBurnTime(time);
		setSuperheatedFuel(burnTimeSuperheated);
		setChanged();
		return true;
	}

	/**
	 * @return 该物品的燃烧时间 (tick), 0 表示不能作为燃料
	 */
	public int resolveBurnTime(ItemStack stack) {
		if (stack.isEmpty()) {
			return 0;
		}

		if (burnTimeCacheValid && !burnTimeKey.isEmpty() && ItemStack.isSameItemSameComponents(burnTimeKey, stack)) {
			return burnTimeValue;
		}

		int time = 0;
		// 标签优先于配方字段: 进了 superheated_fuel 的物品一律按过热燃料算, 原版燃料也适用
		boolean superheated = stack.is(SlItemTags.SUPERHEATED_FUEL);
		if (level != null) {
			SolidFuelRecipe recipe = SolidFuelRecipe.find(level, stack);
			if (recipe != null) {
				time = recipe.getTime();
				superheated |= recipe.isSuperheated();
			} else {
				time = stack.getBurnTime(RecipeType.SMELTING);
			}
		}

		burnTimeKey = stack.copy();
		burnTimeValue = time;
		burnTimeSuperheated = superheated;
		burnTimeCacheValid = true;
		return time;
	}

	public boolean isFuel(ItemStack stack) {
		return resolveBurnTime(stack) > 0;
	}

	public void invalidateBurnTimeCache() {
		burnTimeCacheValid = false;
		burnTimeKey = ItemStack.EMPTY;
		burnTimeValue = 0;
		burnTimeSuperheated = false;
	}

	/**
	 * 把玩家手里的物品放进燃料槽.
	 *
	 * @return 是否真的放进去了一些
	 */
	public boolean insertFuel(ItemStack stack) {
		if (stack.isEmpty() || !isFuel(stack)) {
			return false;
		}

		if (fuel.isEmpty()) {
			int moved = Math.min(FUEL_SLOT_LIMIT, stack.getCount());
			fuel = stack.copyWithCount(moved);
			stack.shrink(moved);
		} else {
			if (!ItemStack.isSameItemSameComponents(fuel, stack)) {
				return false;
			}
			int space = FUEL_SLOT_LIMIT - fuel.getCount();
			if (space <= 0) {
				return false;
			}
			int moved = Math.min(space, stack.getCount());
			fuel.grow(moved);
			stack.shrink(moved);
		}

		setChanged();
		return true;
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		registerCapabilities(event, SlBlockEntities.SOLID_BURNER.get());
	}

	/**
	 * 给指定方块实体类型注册物品能力, 供静态注册与 KubeJS 动态设备共用.
	 */
	public static <T extends SolidBurnerBlockEntity> void registerCapabilities(RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, type, (burner, direction) -> {
			return burner.itemCapability;
		});
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		if (!fuel.isEmpty()) {
			tag.put(NBT_FUEL, fuel.save(registries, new CompoundTag()));
		}
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		fuel = tag.contains(NBT_FUEL) ? ItemStack.parseOptional(registries, tag.getCompound(NBT_FUEL)) : ItemStack.EMPTY;
	}

	@Override
	public void destroy() {
		super.destroy();
		if (level == null || fuel.isEmpty()) {
			return;
		}

		ItemStack dropped = fuel;
		fuel = ItemStack.EMPTY;
		Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), dropped);
	}
}