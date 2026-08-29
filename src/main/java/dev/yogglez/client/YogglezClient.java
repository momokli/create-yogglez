package dev.yogglez.client;

import dev.yogglez.YogglezMod;
import dev.yogglez.client.hud.LensHudOverlay;
import dev.yogglez.client.overlay.LensOverlayRenderer;
import dev.yogglez.client.provider.Ae2NetworkProvider;
import dev.yogglez.client.provider.DemoLensProvider;

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

	/** Registers per-block-entity-type lens providers (client only). */
	private static void initLensProviders() {
		// ---- yogglez:demo (Insight Lens) for a few vanilla block entities ----
		registerById("minecraft", "furnace", new DemoLensProvider());
		registerById("minecraft", "chest", new DemoLensProvider());
		registerById("minecraft", "hopper", new DemoLensProvider());
		registerById("minecraft", "brewing_stand", new DemoLensProvider());

		// ---- ae2:network (Network Lens) - only if AE2 is loaded ----
		if (ModList.get().isLoaded("ae2")) {
			Ae2NetworkProvider provider = new Ae2NetworkProvider();
			String[] ae2Types = { "controller", "drive", "energy_acceptor", "cell_workbench",
				"pattern_provider", "interface", "chest" };
			for (String type : ae2Types)
				registerById("ae2", type, provider);
			LOGGER.info("AE2 detected - network lens providers registered");
		} else {
			LOGGER.info("AE2 not loaded - network lens provider skipped");
		}

		Minecraft mc = Minecraft.getInstance();
		LOGGER.info("Lens provider registry initialized on {}", mc != null ? mc.getUser().getName() : "?");
	}

	private static void registerById(String namespace, String path, dev.yogglez.client.LensInfoProvider provider) {
		BlockEntityType<?> type = BuiltInRegistries.BLOCK_ENTITY_TYPE
			.get(ResourceLocation.fromNamespaceAndPath(namespace, path));
		if (type != null) {
			LensProviderRegistry.register(type, provider);
		} else {
			LOGGER.warn("Block entity type {}:{} not found, provider skipped", namespace, path);
		}
	}
}
