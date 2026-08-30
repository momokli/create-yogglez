package dev.yogglez.client;

import java.util.List;

import dev.yogglez.lens.data.TooltipLine;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Client-side lens info provider: gathers tooltip lines for a foreign (or own)
 * {@link BlockEntity} while the corresponding lens is active.
 *
 * <p>Implementations must only use public APIs of the target mod (e.g.
 * {@code appeng.api} for AE2). The registry is populated on the client only.
 *
 * <p>Implementations should collect their data into the pure data records of
 * {@code dev.yogglez.lens.data} (headless unit-testable) and render them via
 * {@link #toComponent(TooltipLine)}; only the data extraction itself touches
 * game classes.
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

	/**
	 * Converts a pure {@link TooltipLine} into an actual chat component:
	 * translatable lines become {@code Component.translatable(key, args)},
	 * literal lines become {@code Component.literal(text)}; the color name is
	 * mapped to {@link ChatFormatting} (falls back to white).
	 */
	static Component toComponent(TooltipLine line) {
		ChatFormatting format = ChatFormatting.getByName(line.color());
		if (format == null)
			format = ChatFormatting.WHITE;
		if (line.isLiteral())
			return Component.literal(line.args().get(0)).withStyle(format);
		return Component.translatable(line.key(), line.args().toArray()).withStyle(format);
	}
}
