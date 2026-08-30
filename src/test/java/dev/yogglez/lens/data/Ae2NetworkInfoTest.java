package dev.yogglez.lens.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class Ae2NetworkInfoTest {

	@Test
	void statusOfDerivesNodeState() {
		assertEquals("UNPOWERED", Ae2NetworkInfo.statusOf(false, false));
		assertEquals("UNPOWERED", Ae2NetworkInfo.statusOf(false, true));
		assertEquals("INACTIVE", Ae2NetworkInfo.statusOf(true, false));
		assertEquals("ACTIVE", Ae2NetworkInfo.statusOf(true, true));
	}

	@Test
	void formatEnergyUsesOneDecimalAndAeUnit() {
		assertEquals("0.0 AE", Ae2NetworkInfo.formatEnergy(0.0));
		assertEquals("123.5 AE", Ae2NetworkInfo.formatEnergy(123.456));
		assertEquals("1000.0 AE", Ae2NetworkInfo.formatEnergy(1000.0));
	}

	@Test
	void bootingTextMapsBoolean() {
		assertEquals("YES", Ae2NetworkInfo.bootingText(true));
		assertEquals("NO", Ae2NetworkInfo.bootingText(false));
	}

	@Test
	void noNodeRendersSingleRedLine() {
		List<TooltipLine> lines = Ae2NetworkInfo.withoutNode().lines();
		assertEquals(1, lines.size());
		assertEquals("lens.yogglez.ae2.no_node", lines.get(0).key());
		assertEquals("RED", lines.get(0).color());
	}

	@Test
	void errorRendersSingleRedLine() {
		List<TooltipLine> lines = Ae2NetworkInfo.failed().lines();
		assertEquals(1, lines.size());
		assertEquals("lens.yogglez.ae2.error", lines.get(0).key());
		assertEquals("RED", lines.get(0).color());
	}

	@Test
	void noGridRendersNodeLinesAndNoGridFooter() {
		List<TooltipLine> lines = Ae2NetworkInfo.withoutGrid("ACTIVE", 4, 8, true, 1.5).lines();
		assertEquals(5, lines.size());
		assertEquals("lens.yogglez.ae2.header", lines.get(0).key());
		assertEquals(List.of("ACTIVE"), lines.get(1).args());
		assertEquals(List.of("4", "8"), lines.get(2).args());
		assertEquals("GOLD", lines.get(2).color()); // channels ok
		assertEquals(List.of("1.5 AE"), lines.get(3).args());
		assertEquals("lens.yogglez.ae2.no_grid", lines.get(4).key());
		assertEquals("GRAY", lines.get(4).color());
	}

	@Test
	void channelOverloadIsRenderedRed() {
		Ae2NetworkInfo info = new Ae2NetworkInfo(false, true, false, "ACTIVE", 9, 8, false, 1.0,
			0, 0, 0, 0, false);
		assertEquals("RED", info.lines().get(2).color());
	}

	@Test
	void fullGridInfoRendersAllGridLines() {
		Ae2NetworkInfo info = new Ae2NetworkInfo(false, false, false, "ACTIVE", 3, 8, true, 2.0,
			12, 5.5, 20000.0, 100000.0, true);
		List<TooltipLine> lines = info.lines();
		// header, status, channels, idle_power, grid_header, grid_channels,
		// grid_power, grid_storage, grid_booting
		assertEquals(9, lines.size());

		assertEquals("lens.yogglez.ae2.grid_header", lines.get(4).key());
		assertEquals("WHITE", lines.get(4).color());

		TooltipLine gridChannels = lines.get(5);
		assertEquals("lens.yogglez.ae2.grid_channels", gridChannels.key());
		assertEquals(List.of("12"), gridChannels.args());
		assertEquals("GOLD", gridChannels.color());

		TooltipLine gridPower = lines.get(6);
		assertEquals("lens.yogglez.ae2.grid_power", gridPower.key());
		assertEquals(List.of("5.5 AE"), gridPower.args());
		assertEquals("AQUA", gridPower.color());

		TooltipLine gridStorage = lines.get(7);
		assertEquals("lens.yogglez.ae2.grid_storage", gridStorage.key());
		assertEquals(List.of("20000.0 AE", "100000.0 AE"), gridStorage.args());
		assertEquals("LIGHT_PURPLE", gridStorage.color());

		TooltipLine booting = lines.get(8);
		assertEquals("lens.yogglez.ae2.grid_booting", booting.key());
		assertEquals(List.of("YES"), booting.args());
		assertEquals("GRAY", booting.color());
	}

	@Test
	void fullGridInfoWithIdleBootingIsNo() {
		Ae2NetworkInfo info = new Ae2NetworkInfo(false, false, false, "INACTIVE", 0, 8, true, 0.5,
			0, 0, 0, 0, false);
		List<TooltipLine> lines = info.lines();
		assertEquals(List.of("INACTIVE"), lines.get(1).args());
		assertEquals(List.of("NO"), lines.get(8).args());
		assertFalse(info.noNode());
		assertFalse(info.error());
		assertEquals(9, lines.size());
	}
}
