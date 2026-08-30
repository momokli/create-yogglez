package dev.yogglez.lens.data;

import java.util.List;

/**
 * Pure, headless-testable description of a single tooltip line.
 *
 * <p>Lens providers translate their collected data into these plain records
 * instead of building Minecraft {@code Component}s directly. The thin
 * Minecraft adapter ({@code LensInfoProvider#toComponent}) turns a
 * {@code TooltipLine} into an actual chat component, so all formatting/data
 * logic can be unit-tested without a display, GL or a game bootstrap.
 *
 * @param key   translation key for {@code Component.translatable}, or
 *              {@code null} for a literal line
 * @param args  translation arguments (strings); for literal lines the single
 *              text to render
 * @param color name of a {@code ChatFormatting} color (e.g. {@code GRAY}),
 *              or {@code null} for the default color
 */
public record TooltipLine(String key, List<String> args, String color) {

	public TooltipLine {
		args = args == null ? List.of() : List.copyOf(args);
	}

	/** @return a translatable line (key + string args + color name). */
	public static TooltipLine of(String key, String color, String... args) {
		return new TooltipLine(key, List.of(args), color);
	}

	/** @return a literal (non-translated) line with a single text argument. */
	public static TooltipLine literal(String text, String color) {
		return new TooltipLine(null, List.of(text), color);
	}

	/** @return true for literal lines (key is {@code null}). */
	public boolean isLiteral() {
		return key == null;
	}
}
