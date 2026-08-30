package dev.yogglez.lens.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure, headless-testable data model of the "Insight Lens" (yogglez:demo):
 * what the lens knows about the looked-at block entity (block name, block
 * entity type id, position, NBT key overview) and how it is rendered as
 * {@link TooltipLine}s. No Minecraft classes involved - the provider only
 * extracts the raw values from the game and hands them to this record.
 *
 * @param blockName          localized block name (e.g. "Furnace")
 * @param blockEntityTypeId  registry id of the block entity type, e.g.
 *                           {@code minecraft:furnace}
 * @param x                  block position x
 * @param y                  block position y
 * @param z                  block position z
 * @param nbt                NBT key overview of the block entity data
 */
public record DemoLensInfo(String blockName, String blockEntityTypeId, int x, int y, int z, NbtOverview nbt) {

	/** @return the tooltip lines the lens renders for this data. */
	public List<TooltipLine> lines() {
		List<TooltipLine> out = new ArrayList<>();
		out.add(TooltipLine.of("lens.yogglez.demo.header", "WHITE"));
		out.add(TooltipLine.of("lens.yogglez.demo.block", "GRAY", blockName));
		out.add(TooltipLine.of("lens.yogglez.demo.type", "GRAY", blockEntityTypeId));
		out.add(TooltipLine.of("lens.yogglez.demo.pos", "DARK_GRAY",
			String.valueOf(x), String.valueOf(y), String.valueOf(z)));
		out.add(TooltipLine.of("lens.yogglez.demo.nbt_keys", "GRAY",
			String.valueOf(nbt.keyCount())));
		for (String key : nbt.shownKeys())
			out.add(TooltipLine.literal(" - " + key, "DARK_GRAY"));
		if (nbt.truncated())
			out.add(TooltipLine.literal(" ...", "DARK_GRAY"));
		return out;
	}
}
