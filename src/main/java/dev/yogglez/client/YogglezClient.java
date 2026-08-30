package dev.yogglez.client;

import java.util.Map;

import dev.yogglez.YogglezMod;
import dev.yogglez.client.hud.LensHudOverlay;
import dev.yogglez.client.overlay.LensOverlayRenderer;
import dev.yogglez.client.provider.Ae2NetworkProvider;
import dev.yogglez.client.provider.DemoLensProvider;
import dev.yogglez.lens.LensBindings;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.fml.ModList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client setup for create:yogglez (M3):
 * <ul>
 *   <li>populates the lens provider registry (vanilla demo providers + AE2 network provider)</li>
 *   <li>registers the "cycle lens" keybinding</li>
 *   <li>registers GUI layers: the foreign-block lens overlay (above Create's goggle info)
 *       and the active-lens HUD indicator</li>
 * </ul>
 */
@EventBusSubscriber(modid = YogglezMod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class YogglezClient {

	private static final Logger LOGGER = LoggerFactory.getLogger("yogglez-client");

	private YogglezClient() {}

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(YogglezClient::initLensProviders);
	}

	@SubscribeEvent
	public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(Keybindings.CYCLE_LENS);
	}

	@SubscribeEvent
	public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
		ResourceLocation lensInfo = ResourceLocation.fromNamespaceAndPath(YogglezMod.MODID, "lens_info");
		ResourceLocation lensHud = ResourceLocation.fromNamespaceAndPath(YogglezMod.MODID, "lens_hud");
		ResourceLocation createGoggleInfo = ResourceLocation.fromNamespaceAndPath("create", "goggle_info");

		// foreign-block lens tooltip, drawn just above Create's own goggle overlay
		event.registerAbove(createGoggleInfo, lensInfo, LensOverlayRenderer.OVERLAY);
		// active-lens HUD indicator
		event.registerAbove(VanillaGuiLayers.HOTBAR, lensHud, LensHudOverlay.OVERLAY);
	}

	/**
	 * Registers per-block-entity-type lens providers (client only).
	 *
	 * <p>The lens -> block entity type bindings live in the pure
	 * {@link LensBindings} table (unit-tested headless); this method only
	 * resolves the type ids against the game registries and wires the
	 * provider instances into {@link LensProviderRegistry}.
	 */
	private static void initLensProviders() {
		boolean ae2Loaded = ModList.get().isLoaded("ae2");

		for (Map.Entry<String, java.util.List<String>> binding : LensBindings.all().entrySet()) {
			String lensId = binding.getKey();

			// ae2:network is only registered when AE2 is actually loaded
			if (LensBindings.AE2_NETWORK_LENS.equals(lensId) && !ae2Loaded) {
				LOGGER.info("AE2 not loaded - network lens provider skipped");
				continue;
			}

			LensInfoProvider provider = LensBindings.DEMO_LENS.equals(lensId)
				? new DemoLensProvider()
				: new Ae2NetworkProvider();
			for (String typeId : binding.getValue())
				registerById(typeId, provider);

			if (LensBindings.AE2_NETWORK_LENS.equals(lensId))
				LOGGER.info("AE2 detected - network lens providers registered");
		}

		Minecraft mc = Minecraft.getInstance();
		LOGGER.info("Lens provider registry initialized on {}", mc != null ? mc.getUser().getName() : "?");
	}

	private static void registerById(String fullId, LensInfoProvider provider) {
		String[] parts = fullId.split(":", 2);
		if (parts.length != 2) {
			LOGGER.warn("Invalid block entity type id '{}', provider skipped", fullId);
			return;
		}
		try {
			BlockEntityType<?> type = BuiltInRegistries.BLOCK_ENTITY_TYPE
				.get(ResourceLocation.fromNamespaceAndPath(parts[0], parts[1]));
			if (type != null) {
				LensProviderRegistry.register(type, provider);
			} else {
				LOGGER.warn("Block entity type {} not found, provider skipped", fullId);
			}
		} catch (RuntimeException e) {
			LOGGER.warn("Block entity type {} not resolvable, provider skipped ({})", fullId, e.toString());
		}
	}
}
