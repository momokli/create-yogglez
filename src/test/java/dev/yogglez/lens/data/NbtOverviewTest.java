package dev.yogglez.lens.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class NbtOverviewTest {

	@Test
	void emptyKeysYieldEmptyOverview() {
		NbtOverview overview = NbtOverview.of(List.of());
		assertEquals(0, overview.keyCount());
		assertTrue(overview.shownKeys().isEmpty());
		assertFalse(overview.truncated());
	}

	@Test
	void nullKeysYieldEmptyOverview() {
		NbtOverview overview = NbtOverview.of(null);
		assertEquals(0, overview.keyCount());
		assertTrue(overview.shownKeys().isEmpty());
	}

	@Test
	void keysAreSortedAlphabetically() {
		NbtOverview overview = NbtOverview.of(List.of("z", "a", "m"));
		assertEquals(3, overview.keyCount());
		assertEquals(List.of("a", "m", "z"), overview.shownKeys());
		assertFalse(overview.truncated());
	}

	@Test
	void exactlyEightKeysAreNotTruncated() {
		List<String> keys = List.of("k1", "k2", "k3", "k4", "k5", "k6", "k7", "k8");
		NbtOverview overview = NbtOverview.of(keys);
		assertEquals(8, overview.keyCount());
		assertEquals(8, overview.shownKeys().size());
		assertFalse(overview.truncated());
	}

	@Test
	void moreThanEightKeysAreTruncated() {
		List<String> keys = List.of("k1", "k2", "k3", "k4", "k5", "k6", "k7", "k8", "k9");
		NbtOverview overview = NbtOverview.of(keys);
		assertEquals(9, overview.keyCount());
		assertEquals(8, overview.shownKeys().size());
		assertTrue(overview.truncated());
		assertEquals("k1", overview.shownKeys().get(0));
		assertEquals("k8", overview.shownKeys().get(7));
	}
}
