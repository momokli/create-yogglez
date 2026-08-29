package dev.yogglez;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.equipment.goggles.GogglesItem;

import dev.yogglez.item.YogglezGogglesItem;
import dev.yogglez.lens.YogglezLenses;
import dev.yogglez.network.CycleLensPayload;
import dev.yogglez.registry.YogglezRegistries;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/**
 * create:yogglez - a modular lens system for the Create Engineer's Goggles.
 *
 * POC milestone overview (see README):
 *  M0 template + build, M1 goggles/lens items + cycle keybind,
 *  M2 own BlockEntity implementing IHaveGoggleInformation (core proof),
 *  M3 client-side provider registry (BlockEntityType -> LensInfoProvider) + AE2 example provider + HUD indicator.
 */
@Mod(YogglezMod.MODID)
public class YogglezMod {

	public static final String MODID = "yogglez";
	public static final Logger LOGGER = LogUtils.getLogger();

	public YogglezMod(IEventBus modEventBus, ModContainer modContainer) {
		// ---- registries (items, blocks, block entity types, creative tab) ----
		YogglezRegistries.ITEMS.register(modEventBus);
		YogglezRegistries.BLOCKS.register(modEventBus);
		YogglezRegistries.BLOCK_ENTITIES.register(modEventBus);
		YogglezRegistries.CREATIVE_TABS.register(modEventBus);

		// ---- known lenses (server-safe registry, display names) ----
		YogglezLenses.register(YogglezLenses.DEMO, Component.translatable("lens.yogglez.demo"));
		YogglezLenses.register(YogglezLenses.AE2_NETWORK, Component.translatable("lens.yogglez.ae2_network"));

		// ---- networking: "cycle lens" keybind action ----
		modEventBus.addListener((RegisterPayloadHandlersEvent event) -> event.registrar(MODID)
			.playToServer(CycleLensPayload.TYPE, CycleLensPayload.STREAM_CODEC, CycleLensPayload::handle));

		// ---- Create integration: our goggles count as "wearing goggles" ----
		// This is the official Create extension point for custom goggles/overlay entry points.
		// It makes Create's goggle overlay render for our goggles (incl. our own demo BlockEntity, M2).
		GogglesItem.addIsWearingPredicate(player -> player.getItemBySlot(EquipmentSlot.HEAD)
			.getItem() instanceof YogglezGogglesItem);

		LOGGER.info("create:yogglez loaded (POC)");
	}
}
