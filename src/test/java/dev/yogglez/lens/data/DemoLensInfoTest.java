package dev.yogglez.lens.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class DemoLensInfoTest {

	/** Standard sample data: furnace at 1/2/3 with 4 NBT keys, no truncation. */
	private static DemoLensInfo sample() {
		return new DemoLensInfo("Furnace", "minecraft:furnace", 1, 2, 3,
			NbtOverview.of(List.of("Items", "BurnTime", "CookTime", "CookTimeTotal")));
	}

	@Test
	void linesContainAllExpectedData() {
		List<TooltipLine> lines = sample().lines();
		assertEquals(5 + 4, lines.size()); // header, block, type, pos, nbt count + 4 keys

		assertEquals("lens.yogglez.demo.header", lines.get(0).key());
		assertEquals("WHITE", lines.get(0).color());

		TooltipLine block = lines.get(1);
		assertEquals("lens.yogglez.demo.block", block.key());
		assertEquals(List.of("Furnace"), block.args());

		TooltipLine type = lines.get(2);
		assertEquals("lens.yogglez.demo.type", type.key());
		assertEquals(List.of("minecraft:furnace"), type.args());

		TooltipLine pos = lines.get(3);
		assertEquals("lens.yogglez.demo.pos", pos.key());
		assertEquals(List.of("1", "2", "3"), pos.args());
		assertEquals("DARK_GRAY", pos.color());

		TooltipLine nbtCount = lines.get(4);
		assertEquals("lens.yogglez.demo.nbt_keys", nbtCount.key());
		assertEquals(List.of("4"), nbtCount.args());
	}

	@Test
	void nbtKeysAreRenderedAsLiteralIndentedLines() {
		List<TooltipLine> lines = sample().lines();
		TooltipLine keyLine = lines.get(5);
		assertTrue(keyLine.isLiteral());
		assertEquals(" - BurnTime", keyLine.args().get(0)); // keys are sorted alphabetically
		assertEquals("DARK_GRAY", keyLine.color());
	}

	@Test
	void truncatedNbtAppendsEllipsisLine() {
		DemoLensInfo info = new DemoLensInfo("Chest", "minecraft:chest", 0, 0, 0,
			NbtOverview.of(List.of("k1", "k2", "k3", "k4", "k5", "k6", "k7", "k8", "k9")));
		List<TooltipLine> lines = info.lines();
		// header, block, type, pos, nbt count, 8 keys, ellipsis
		assertEquals(5 + 8 + 1, lines.size());
		TooltipLine ellipsis = lines.get(lines.size() - 1);
		assertTrue(ellipsis.isLiteral());
		assertEquals(" ...", ellipsis.args().get(0));
		assertEquals("9", lines.get(4).args().get(0));
	}

	@Test
	void emptyNbtShowsZeroKeysAndNoKeyLines() {
		DemoLensInfo info = new DemoLensInfo("Hopper", "minecraft:hopper", 0, 0, 0, NbtOverview.of(List.of()));
		List<TooltipLine> lines = info.lines();
		assertEquals(5, lines.size()); // no key lines, no ellipsis
		assertEquals(List.of("0"), lines.get(4).args());
	}
}
