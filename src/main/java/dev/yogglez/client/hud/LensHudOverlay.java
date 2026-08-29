package dev.yogglez.client.hud;

import dev.yogglez.item.YogglezGogglesItem;
import dev.yogglez.lens.YogglezLenses;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

/**
 * HUD indicator: shows the currently active lens of the worn Yogglez Goggles
 * in the bottom-right corner (above the hotbar area).
 */
public final class LensHudOverlay {

	public static final LayeredDraw.Layer OVERLAY = LensHudOverlay::renderOverlay;

	private LensHudOverlay() {}

	private static void renderOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.options.hideGui || mc.player == null)
			return;

		ItemStack worn = mc.player.getItemBySlot(EquipmentSlot.HEAD);
		if (!(worn.getItem() instanceof YogglezGogglesItem))
			return;

		ResourceLocation activeLens = YogglezGogglesItem.getActiveLens(worn);

		Component label;
		int color;
		if (activeLens == null) {
			label = Component.translatable("hud.yogglez.lens",
				Component.translatable("lens.yogglez.none"));
			color = 0x555555;
		} else {
			label = Component.translatable("hud.yogglez.lens", YogglezLenses.getDisplayName(activeLens));
			color = 0xFFAA00;
		}

		String text = label.getString();
		int x = guiGraphics.guiWidth() - mc.font.width(text) - 4;
		int y = guiGraphics.guiHeight() - 34;

		guiGraphics.drawString(mc.font, label, x, y, color, true);
	}
}
