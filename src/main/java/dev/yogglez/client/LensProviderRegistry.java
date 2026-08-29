package dev.yogglez.client;

import java.util.concurrent.ConcurrentHashMap;

import dev.yogglez.item.YogglezGogglesItem;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Client-side lens provider registry: maps {@link BlockEntityType} to
 * {@link LensInfoProvider}s. This is the extension point for foreign blocks
 * (e.g. AE2) - providers read data exclusively through public APIs, no mixins.
 */
public final class LensProviderRegistry {

	private static final java.util.Map<BlockEntityType<?>, LensInfoProvider> PROVIDERS = new ConcurrentHashMap<>();

	private LensProviderRegistry() {}

	/** Registers a provider for a block entity type (call from client init). */
	public static void register(BlockEntityType<?> type, LensInfoProvider provider) {
		PROVIDERS.put(type, provider);
	}

	/** Looks up the provider for a block entity type, or null. */
	public static LensInfoProvider get(BlockEntityType<?> type) {
		return PROVIDERS.get(type);
	}

	/**
	 * @return the active lens id from the goggles the player is wearing, falling
	 *         back to the main hand - or null when no goggles are active.
	 */
	public static ResourceLocation activeLensOf(LocalPlayer player) {
		ItemStack worn = player.getItemBySlot(EquipmentSlot.HEAD);
		if (!(worn.getItem() instanceof YogglezGogglesItem)) {
			worn = player.getMainHandItem();
			if (!(worn.getItem() instanceof YogglezGogglesItem))
				return null;
		}
		return YogglezGogglesItem.getActiveLens(worn);
	}

	/** @return the block entity at the hit position of the client level, or null. */
	public static BlockEntity blockEntityAt(ClientLevel level, BlockHitResult hit) {
		if (level == null)
			return null;
		BlockPos pos = hit.getBlockPos();
		return level.getBlockEntity(pos);
	}
}
