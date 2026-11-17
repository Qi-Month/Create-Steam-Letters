package dev.celestiacraft.steam_letters.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import dev.celestiacraft.steam_letters.api.SlTranKeys;
import dev.celestiacraft.steam_letters.common.block.burner.AbstractBurnerBlock;
import dev.celestiacraft.steam_letters.common.recipe.type.IBurnerFuelRecipe;
import dev.celestiacraft.steam_letters.common.register.SlFluids;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;

public abstract class BurnerFuelCategory<R extends IBurnerFuelRecipe> implements IRecipeCategory<R> {
	// 补药问咱为什么不用final常量, Debug的给咱调成傻逼了
	private static int width = 177;
	private static int height = 56;

	private static final String RECIPE_ID_KEY = "jei.tooltip.recipe.id";

	private static int fuelSlotX = width / 2 - 16;
	private static int fuelSlotY = 3;

	private static int arrowX = width / 2 + 3;
	private static int arrowY = 8;

	private static int lightX = 81;
	private static int lightY = 38;

	private static int burnerX = width / 2 + 3;
	private static int burnerY = 5;

	private static double burnerScale = 21.0D;
	private static double burnerLocalY = 1.8D;

	private static int heatBarX = 4;
	private static int heatBarY = 30;
	private static int heatTextX = 9;
	private static int textY = 36;
	private static int timeMargin = 8;

	private static int heatedColor = HeatCondition.HEATED.getColor();
	private static int superheatedColor = HeatCondition.SUPERHEATED.getColor();
	private static int timeColor = 0x404040;

	private final RecipeType<R> type;
	private final Component title;
	private final IDrawable icon;
	private final IDrawableStatic slotBackground;

	/**
	 * 配方实例到配方 ID 的映射, 由 JEI 插件在注册配方时填充.
	 */
	private static final Map<Object, ResourceLocation> RECIPE_IDS = new IdentityHashMap<>();

	protected BurnerFuelCategory(RecipeType<R> type, Component title, IDrawable icon, IGuiHelper helper) {
		this.type = type;
		this.title = title;
		this.icon = icon;
		slotBackground = helper.createDrawable(
				AllGuiTextures.JEI_SLOT.getLocation(),
				AllGuiTextures.JEI_SLOT.getStartX(),
				AllGuiTextures.JEI_SLOT.getStartY(),
				AllGuiTextures.JEI_SLOT.getWidth(),
				AllGuiTextures.JEI_SLOT.getHeight()
		);
	}

	@Override
	public @NotNull RecipeType<R> getRecipeType() {
		return type;
	}

	@Override
	public @NotNull Component getTitle() {
		return title;
	}

	@Override
	public @Nullable IDrawable getIcon() {
		return icon;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull R recipe, @NotNull IFocusGroup group) {
		IRecipeSlotBuilder slot = addFuelSlot(builder, recipe, fuelSlotX, fuelSlotY);
		builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT)
				.addFluidStack(SlFluids.STEAM.getSource(), recipe.getSteamAmount())
				.addFluidStack(SlFluids.SUPERHEATED_STEAM.getSource(), recipe.getSteamAmount())
				.addItemLike(SlFluids.STEAM.get().getBucket())
				.addItemLike(SlFluids.SUPERHEATED_STEAM.get().getBucket());

		if (slot != null) {
			slot.setBackground(slotBackground, -1, -1);

			ResourceLocation id = getRecipeId(recipe);
			if (id != null) {
				slot.addRichTooltipCallback((view, tooltip) -> {
					tooltip.add(Component.translatable(RECIPE_ID_KEY, id.toString())
							.withStyle(ChatFormatting.DARK_GRAY));
				});
			}
		}
	}

	/**
	 * 绑定配方实例与其配方 ID; 原版熔炉燃料派生出的条目绑定的是合成 ID.
	 */
	public static void bindRecipeId(@NotNull Object recipe, @NotNull ResourceLocation id) {
		RECIPE_IDS.put(recipe, id);
	}

	public static @Nullable ResourceLocation getRecipeId(@NotNull Object recipe) {
		return RECIPE_IDS.get(recipe);
	}

	@Override
	public @Nullable ResourceLocation getRegistryName(@NotNull R recipe) {
		return getRecipeId(recipe);
	}

	/**
	 * 往布局里放燃料槽.
	 *
	 * @return 已放置的槽位, 供基类补背景; 没有可显示的燃料时返回 null
	 */
	protected abstract @Nullable IRecipeSlotBuilder addFuelSlot(@NotNull IRecipeLayoutBuilder builder, @NotNull R recipe, int x, int y);

	@Override
	public void draw(@NotNull R recipe, @NotNull IRecipeSlotsView view, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
		Font font = Minecraft.getInstance().font;

		AllGuiTextures.JEI_LIGHT.render(graphics, lightX, lightY);

		renderBurnerBlock(graphics);
		AllGuiTextures.JEI_DOWN_ARROW.render(graphics, arrowX, arrowY);
		AllGuiTextures.JEI_HEAT_BAR.render(graphics, heatBarX, heatBarY);

		if (recipe.isSuperheated()) {
			Component note = Component.translatable(SlTranKeys.langKey(SlTranKeys.JEI_SUPERHEATED));
			graphics.drawString(font, note, heatTextX, textY, superheatedColor, false);
		} else {
			Component note = Component.translatable(SlTranKeys.langKey(SlTranKeys.JEI_HEATED));
			graphics.drawString(font, note, heatTextX, textY, heatedColor, false);
		}

		Component time = Component.literal(String.format(Locale.ROOT, "%.1fs", recipe.getTime() / 20.0D));
		graphics.drawString(font, time, width - timeMargin - font.width(time), textY, timeColor, false);
	}

	private void renderBurnerBlock(@NotNull GuiGraphics graphics) {
		BlockState state = getBurnerBlock().defaultBlockState();

		if (state.hasProperty(AbstractBurnerBlock.FACING)) {
			state = state.setValue(AbstractBurnerBlock.FACING, Direction.NORTH);
		}
		if (state.hasProperty(AbstractBurnerBlock.LIT)) {
			state = state.setValue(AbstractBurnerBlock.LIT, true);
		}

		PoseStack matrix = graphics.pose();
		matrix.pushPose();
		matrix.translate(burnerX, burnerY, 200.0F);
		matrix.mulPose(Axis.XP.rotationDegrees(-15.5F));
		matrix.mulPose(Axis.YP.rotationDegrees(22.5F));

		GuiGameElement.of(state)
				.lighting(AnimatedKinetics.DEFAULT_LIGHTING)
				.atLocal(0.05D, burnerLocalY, 0.0D)
				.scale(burnerScale)
				.render(graphics);

		matrix.popPose();
	}

	/**
	 * 获取渲染的机器方块
	 *
	 * @return
	 */
	public abstract Block getBurnerBlock();
}