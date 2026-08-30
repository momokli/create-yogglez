package dev.yogglez.lens;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Headless test of the lens <-> block entity type binding table: this is
 * exactly "which provider matches which BlockEntityType" (at the data level;
 * the final registry wiring against real {@code BlockEntityType} instances is
 * covered by the "game"-tagged tests).
 */
class LensBindingsTest {

	@Test
	void demoLensTargetsVanillaBlockEntityTypes() {
		List<String> types = LensBindings.blockEntityTypesFor(LensBindings.DEMO_LENS);
		assertEquals(4, types.size());
		assertTrue(types.containsAll(List.of(
			"minecraft:furnace", "minecraft:chest", "minecraft:hopper", "minecraft:brewing_stand")));
	}

	@Test
	void ae2NetworkLensTargetsAe2Devices() {
		List<String> types = LensBindings.blockEntityTypesFor(LensBindings.AE2_NETWORK_LENS);
		assertEquals(7, types.size());
		assertTrue(types.containsAll(List.of(
			"ae2:controller", "ae2:drive", "ae2:energy_acceptor", "ae2:cell_workbench",
			"ae2:pattern_provider", "ae2:interface", "ae2:chest")));
	}

	@Test
	void furnaceIsMatchedByInsightLens() {
		assertEquals(List.of(LensBindings.DEMO_LENS), LensBindings.lensesFor("minecraft:furnace"));
	}

	@Test
	void ae2ControllerIsMatchedByNetworkLens() {
		assertEquals(List.of(LensBindings.AE2_NETWORK_LENS), LensBindings.lensesFor("ae2:controller"));
	}

	@Test
	void unknownBlockEntityTypeMatchesNoLens() {
		assertTrue(LensBindings.lensesFor("minecraft:beacon").isEmpty());
		assertTrue(LensBindings.lensesFor("create:mechanical_press").isEmpty());
	}

	@Test
	void unknownLensHasNoTypes() {
		assertTrue(LensBindings.blockEntityTypesFor("mod:unknown").isEmpty());
	}

	@Test
	void tableIsCompleteAndImmutable() {
		Map<String, List<String>> all = LensBindings.all();
		assertEquals(2, all.size());
		assertTrue(all.containsKey(LensBindings.DEMO_LENS));
		assertTrue(all.containsKey(LensBindings.AE2_NETWORK_LENS));
		// no null/blank ids anywhere
		all.forEach((lens, types) -> {
			assertFalse(lens.isBlank());
			types.forEach(type -> assertFalse(type.isBlank()));
		});
		// every ae2 binding is in the ae2 namespace, every demo binding in minecraft
		assertTrue(all.get(LensBindings.AE2_NETWORK_LENS).stream().allMatch(t -> t.startsWith("ae2:")));
		assertTrue(all.get(LensBindings.DEMO_LENS).stream().allMatch(t -> t.startsWith("minecraft:")));
	}
}
