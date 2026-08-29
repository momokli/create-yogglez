package dev.yogglez.client;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Client-side lens info provider: gathers tooltip lines for a foreign (or own)
 * {@link BlockEntity} while the corresponding lens is active.
 *
 * <p>Implementations must only use public APIs of the target mod (e.g.
 * {@code appeng.api} for AE2). The registry is populated on the client only.
 */
@FunctionalInterface
public interface LensInfoProvider {

	/**
	 * Appends tooltip lines about the given block entity.
	 *
	 * @param blockEntity the looked-at block entity (never null)
	 * @param hitResult   the block ray-trace result (face, position)
	 * @param tooltip     the list to append to
	 */
	void gatherInfo(BlockEntity blockEntity, BlockHitResult hitResult, List<Component> tooltip);
}
