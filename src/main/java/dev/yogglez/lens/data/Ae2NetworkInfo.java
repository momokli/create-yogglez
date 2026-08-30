package dev.yogglez.lens.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure, headless-testable data model of the "AE2 Network Lens" (ae2:network).
 *
 * <p>The provider reads the values from the public AE2 API
 * ({@code appeng.api}) and hands them to this record; all derivation
 * (node status, energy formatting, channel colors) and rendering
 * ({@link TooltipLine}s) happens here without any Minecraft/AE2 classes.
 *
 * <p>Variants: {@link #withoutNode()} (not an AE2 device), {@link #failed()}
 * (reading failed), {@link #withoutGrid(...)} (device has no connected grid) and
 * the full constructor (node + grid data).
 */
public record Ae2NetworkInfo(
		boolean noNode,
		boolean noGrid,
		boolean error,
		String nodeStatus,
		int usedChannels,
		int maxChannels,
		boolean channelOk,
		double idlePower,
		int gridUsedChannels,
		double gridAvgPower,
		double gridStored,
		double gridMaxStored,
		boolean gridBooting) {

	/** @return the degenerate "no AE2 grid node" info. */
	public static Ae2NetworkInfo withoutNode() {
		return new Ae2NetworkInfo(true, false, false, null, 0, 0, false, 0, 0, 0, 0, 0, false);
	}

	/** @return the degenerate "could not read network data" info. */
	public static Ae2NetworkInfo failed() {
		return new Ae2NetworkInfo(false, false, true, null, 0, 0, false, 0, 0, 0, 0, 0, false);
	}

	/** @return node-level info of a device that is not connected to a grid. */
	public static Ae2NetworkInfo withoutGrid(String nodeStatus, int usedChannels, int maxChannels,
			boolean channelOk, double idlePower) {
		return new Ae2NetworkInfo(false, true, false, nodeStatus, usedChannels, maxChannels,
			channelOk, idlePower, 0, 0, 0, 0, false);
	}

	/** AE2 node status text: ACTIVE / INACTIVE / UNPOWERED. */
	public static String statusOf(boolean powered, boolean active) {
		if (!powered)
			return "UNPOWERED";
		if (!active)
			return "INACTIVE";
		return "ACTIVE";
	}

	/** AE2 energy is in AE units; format with up to one decimal. */
	public static String formatEnergy(double ae) {
		return String.format("%.1f AE", ae);
	}

	public static String bootingText(boolean booting) {
		return booting ? "YES" : "NO";
	}

	/** @return the tooltip lines the lens renders for this data. */
	public List<TooltipLine> lines() {
		List<TooltipLine> out = new ArrayList<>();
		if (noNode) {
			out.add(TooltipLine.of("lens.yogglez.ae2.no_node", "RED"));
			return out;
		}
		if (error) {
			out.add(TooltipLine.of("lens.yogglez.ae2.error", "RED"));
			return out;
		}
		out.add(TooltipLine.of("lens.yogglez.ae2.header", "WHITE"));
		out.add(TooltipLine.of("lens.yogglez.ae2.status", "GRAY", nodeStatus));
		out.add(TooltipLine.of("lens.yogglez.ae2.channels",
			channelOk ? "GOLD" : "RED",
			String.valueOf(usedChannels), String.valueOf(maxChannels)));
		out.add(TooltipLine.of("lens.yogglez.ae2.idle_power", "AQUA", formatEnergy(idlePower)));
		if (noGrid) {
			out.add(TooltipLine.of("lens.yogglez.ae2.no_grid", "GRAY"));
			return out;
		}
		out.add(TooltipLine.of("lens.yogglez.ae2.grid_header", "WHITE"));
		out.add(TooltipLine.of("lens.yogglez.ae2.grid_channels", "GOLD",
			String.valueOf(gridUsedChannels)));
		out.add(TooltipLine.of("lens.yogglez.ae2.grid_power", "AQUA", formatEnergy(gridAvgPower)));
		out.add(TooltipLine.of("lens.yogglez.ae2.grid_storage", "LIGHT_PURPLE",
			formatEnergy(gridStored), formatEnergy(gridMaxStored)));
		out.add(TooltipLine.of("lens.yogglez.ae2.grid_booting", "GRAY", bootingText(gridBooting)));
		return out;
	}
}
