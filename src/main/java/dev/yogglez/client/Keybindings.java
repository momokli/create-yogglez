package dev.yogglez.client;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;

import net.neoforged.neoforge.client.settings.KeyConflictContext;

/** Keybindings for create:yogglez. */
public final class Keybindings {

	/** Cycles the active lens of the worn/hand-held Yogglez Goggles. */
	public static final KeyMapping CYCLE_LENS = new KeyMapping(
		"key.yogglez.cycle_lens",
		KeyConflictContext.IN_GAME,
		InputConstants.Type.KEYSYM,
		GLFW.GLFW_KEY_H,
		"key.categories.yogglez");

	private Keybindings() {}
}
