package dev.yogglez.client;

import dev.yogglez.YogglezMod;
import dev.yogglez.network.CycleLensPayload;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/** Game-bus client events: reacts to the "cycle lens" keybind. */
@EventBusSubscriber(modid = YogglezMod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class YogglezClientEvents {

	private YogglezClientEvents() {}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Post event) {
		if (Keybindings.CYCLE_LENS.consumeClick())
			PacketDistributor.sendToServer(new CycleLensPayload());
	}
}
