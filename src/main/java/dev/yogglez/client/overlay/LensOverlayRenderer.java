package dev.yogglez.client.overlay;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.yogglez.client.LensInfoProvider;
import dev.yogglez.client.LensProviderRegistry;
import dev.yogglez.item.YogglezGogglesItem;
import dev.yogglez.lens.YogglezLenses;

import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * M3 foreign-block overlay: renders a Create-style tooltip when the player
 * wears the Yogglez Goggles with an active lens and looks at a block entity
 * that has a registered {@link LensInfoProvider} (e.g. AE2 devices).
 *
 * <p>Unlike Create's own overlay (which only works for block entities that
 * implement {@code IHaveGoggleInformation}), this reads data through the
 * client-side provider registry - no mixins into foreign block entities.
 */
public final class LensOverlayRenderer {

	public static final LayeredDraw.Layer OVERLAY = LensOverlayRenderer::renderOverlay;

	private static int hoverTicks = 0;

	private LensOverlayRenderer() {}

	private static void renderOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.options.hideGui || mc.gameMode == null || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
			return;

		HitResult hit = mc.hitResult;
		if (!(hit instanceof BlockHitResult blockHit)) {
			hoverTicks = 0;
			return;
		}

		LocalPlayer player = mc.player;
		ClientLevel level = mc.level;
		if (player == null || level == null)
			return;

		// only with our goggles active
		ItemStack worn = player.getItemBySlot(EquipmentSlot.HEAD);
		if (!(worn.getItem() instanceof YogglezGogglesItem))
			return;

		ResourceLocation activeLens = YogglezGogglesItem.getActiveLens(worn);
		if (activeLens == null)
			return;

		BlockPos pos = blockHit.getBlockPos();
		BlockEntity be = level.getBlockEntity(pos);
		if (be == null)
			return;

		LensInfoProvider provider = LensProviderRegistry.get(be.getType());
		if (provider == null)
			return;

		List<Component> tooltip = new ArrayList<>();
		// header: active lens name (Create-style white line)
		tooltip.add(YogglezLenses.getDisplayName(activeLens).copy().withStyle(ChatFormatting.WHITE));
		provider.gatherInfo(be, blockHit, tooltip);

		if (tooltip.size() <= 1)
			return;

		hoverTicks = Math.min(hoverTicks + 1, 24);

		// position: slightly left of the crosshair area, above Create's goggle box
		int width = guiGraphics.guiWidth();
		int height = guiGraphics.guiHeight();
		int posX = width / 2 + 4;
		int posY = height / 2 - 8;

		PoseStack poseStack = guiGraphics.pose();
		poseStack.pushPose();

		// subtle fade-in like Create's overlay
		float fade = Mth.clamp((hoverTicks + deltaTracker.getGameTimeDeltaPartialTick(false)) / 24f, 0, 1);
		if (fade < 1)
			poseStack.translate((1 - fade) * 8, 0, 0);

		guiGraphics.renderComponentTooltip(mc.font, tooltip, posX, posY);
		poseStack.popPose();
	}
}
