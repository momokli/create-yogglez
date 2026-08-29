package dev.yogglez.network;

import dev.yogglez.YogglezMod;
import dev.yogglez.item.YogglezGogglesItem;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * "Cycle Lens" keybind action. Sent from the client, executed on the server so
 * the worn/hand-held goggles item NBT is authoritative and synced back.
 */
public record CycleLensPayload() implements CustomPacketPayload {

	public static final Type<CycleLensPayload> TYPE =
		new Type<>(ResourceLocation.fromNamespaceAndPath(YogglezMod.MODID, "cycle_lens"));

	public static final StreamCodec<RegistryFriendlyByteBuf, CycleLensPayload> STREAM_CODEC =
		StreamCodec.unit(new CycleLensPayload());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(CycleLensPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			Player player = context.player();
			if (player == null)
				return;
			ItemStack goggles = findGoggles(player);
			if (!goggles.isEmpty())
				YogglezGogglesItem.cycleLens(goggles);
		});
	}

	private static ItemStack findGoggles(Player player) {
		ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
		if (head.getItem() instanceof YogglezGogglesItem)
			return head;
		ItemStack mainHand = player.getMainHandItem();
		if (mainHand.getItem() instanceof YogglezGogglesItem)
			return mainHand;
		return player.getOffhandItem();
	}
}
