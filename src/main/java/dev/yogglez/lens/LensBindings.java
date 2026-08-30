package dev.yogglez.lens;

import java.util.List;
import java.util.Map;

/**
 * Pure, headless-testable binding table: which lens targets which block
 * entity types.
 *
 * <p>This is the single source of truth for the client-side
 * {@code LensProviderRegistry} population (see {@code YogglezClient}): for
 * every lens id it lists the {@code namespace:path} block entity type ids the
 * lens applies to. Being free of Minecraft classes it can be unit-tested as a
 * plain POJO - the test asserts exactly "which provider matches which block
 * entity type".
 */
public final class LensBindings {

	/** Insight Lens (yogglez:demo): generic block analysis for vanilla BEs. */
	public static final String DEMO_LENS = "yogglez:demo";

	/** AE2 Network Lens (ae2:network): Network Tool replacement. */
	public static final String AE2_NETWORK_LENS = "ae2:network";

	private static final Map<String, List<String>> BINDINGS = Map.of(
		DEMO_LENS, List.of(
			"minecraft:furnace",
			"minecraft:chest",
			"minecraft:hopper",
			"minecraft:brewing_stand"),
		AE2_NETWORK_LENS, List.of(
			"ae2:controller",
			"ae2:drive",
			"ae2:energy_acceptor",
			"ae2:cell_workbench",
			"ae2:pattern_provider",
			"ae2:interface",
			"ae2:chest"));

	private LensBindings() {}

	/** @return immutable lens id -> block entity type ids mapping. */
	public static Map<String, List<String>> all() {
		return BINDINGS;
	}

	/** @return the block entity type ids a lens applies to (never null). */
	public static List<String> blockEntityTypesFor(String lensId) {
		return BINDINGS.getOrDefault(lensId, List.of());
	}

	/**
	 * @return the lens ids that apply to a block entity type id (sorted,
	 *         empty when none). A type id may match several lenses; the
	 *         provider registry keeps them all and the overlay renders every
	 *         matching provider.
	 */
	public static List<String> lensesFor(String blockEntityTypeId) {
		return BINDINGS.entrySet().stream()
			.filter(e -> e.getValue().contains(blockEntityTypeId))
			.map(Map.Entry::getKey)
			.sorted()
			.toList();
	}
}
