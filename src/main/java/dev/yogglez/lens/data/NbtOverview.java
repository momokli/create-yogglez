package dev.yogglez.lens.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure, headless-testable "NBT key overview" logic used by the Insight Lens
 * (yogglez:demo): how many keys a block entity tag has, which keys are shown
 * and whether the list was truncated. No Minecraft classes involved.
 *
 * @param keyCount  total number of keys in the tag
 * @param shownKeys keys that are actually displayed (sorted, capped)
 * @param truncated true when more keys exist than are shown
 */
public record NbtOverview(int keyCount, List<String> shownKeys, boolean truncated) {

	/** Maximum number of keys the lens shows before appending " ...". */
	public static final int MAX_SHOWN_KEYS = 8;

	public NbtOverview {
		shownKeys = shownKeys == null ? List.of() : List.copyOf(shownKeys);
	}

	/**
	 * Builds the overview from the raw (unsorted) key set of a block entity
	 * tag. Keys are sorted alphabetically and capped at
	 * {@value #MAX_SHOWN_KEYS}.
	 */
	public static NbtOverview of(List<String> rawKeys) {
		if (rawKeys == null || rawKeys.isEmpty())
			return new NbtOverview(0, List.of(), false);
		List<String> sorted = new ArrayList<>(rawKeys);
		sorted.sort(String::compareTo);
		int count = sorted.size();
		boolean truncated = count > MAX_SHOWN_KEYS;
		List<String> shown = truncated ? sorted.subList(0, MAX_SHOWN_KEYS) : sorted;
		return new NbtOverview(count, shown, truncated);
	}
}
