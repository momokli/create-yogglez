package dev.yogglez.lens;

import java.util.HashMap;
import java.util.Map;

import dev.yogglez.YogglezMod;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Server-safe registry of known lens types.
 *
 * <p>A lens is identified by a {@link ResourceLocation} (e.g. {@code yogglez:demo}
 * or {@code ae2:network}). Display names are registered here; the client-side
 * per-block rendering logic lives in
 * {@link dev.yogglez.client.lens.LensProviderRegistry} (BlockEntityType -> LensInfoProvider).
 */
public final class YogglezLenses {

	/** Insight Lens: generic block analysis (NBT inspection) for vanilla blocks. */
	public static final ResourceLocation DEMO = ResourceLocation.fromNamespaceAndPath(YogglezMod.MODID, "demo");

	/** AE2 Network Lens: Network Tool replacement, uses only the public appeng.api. */
	public static final ResourceLocation AE2_NETWORK = ResourceLocation.fromNamespaceAndPath("ae2", "network");

	private static final Map<ResourceLocation, Component> LENS_NAMES = new HashMap<>();

	private YogglezLenses() {}

	public static void register(ResourceLocation id, Component displayName) {
		LENS_NAMES.put(id, displayName);
	}

	public static boolean isRegistered(ResourceLocation id) {
		return LENS_NAMES.containsKey(id);
	}

	public static Component getDisplayName(ResourceLocation id) {
		Component name = LENS_NAMES.get(id);
		return name != null ? name : Component.literal(id.toString());
	}
}
