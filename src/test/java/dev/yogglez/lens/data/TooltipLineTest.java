package dev.yogglez.lens.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class TooltipLineTest {

	@Test
	void translatableLineKeepsKeyArgsAndColor() {
		TooltipLine line = TooltipLine.of("lens.yogglez.demo.type", "GRAY", "minecraft:furnace");
		assertFalse(line.isLiteral());
		assertEquals("lens.yogglez.demo.type", line.key());
		assertEquals(List.of("minecraft:furnace"), line.args());
		assertEquals("GRAY", line.color());
	}

	@Test
	void literalLineHasNullKeyAndSingleText() {
		TooltipLine line = TooltipLine.literal(" - X", "DARK_GRAY");
		assertTrue(line.isLiteral());
		assertNull(line.key());
		assertEquals(List.of(" - X"), line.args());
	}

	@Test
	void argsAreDefensivelyCopied() {
		List<String> mutable = new java.util.ArrayList<>(List.of("a"));
		TooltipLine line = TooltipLine.of("k", null, mutable.toArray(new String[0]));
		mutable.add("b");
		assertEquals(1, line.args().size());
	}

	@Test
	void nullArgsYieldEmptyList() {
		TooltipLine line = new TooltipLine("k", null, null);
		assertTrue(line.args().isEmpty());
	}
}
