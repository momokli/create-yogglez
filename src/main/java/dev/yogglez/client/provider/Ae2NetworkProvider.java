package dev.yogglez.client.provider;

import java.util.List;

import appeng.api.networking.GridHelper;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.pathing.IPathingService;

import dev.yogglez.YogglezMod;
import dev.yogglez.client.LensInfoProvider;
import dev.yogglez.lens.data.Ae2NetworkInfo;
import dev.yogglez.lens.data.TooltipLine;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

/**
 * "AE2 Network Lens" (ae2:network): a Network Tool replacement built strictly on
 * the public AE2 API ({@code appeng.api}). While the lens is active and you look
 * at an AE2 device, it shows the network status: node state, channel usage,
 * power usage and energy buffer of the connected grid.
 *
 * <p>All API access is guarded - if the device is not part of a grid (or AE2 is
 * absent) the tooltip degrades gracefully instead of crashing. The collected
 * values are delegated to the pure {@link Ae2NetworkInfo} record (headless
 * unit-tested); only the AE2 API reads happen here.
 */
public class Ae2NetworkProvider implements LensInfoProvider {

	@Override
	public void gatherInfo(BlockEntity blockEntity, BlockHitResult hitResult, List<Component> tooltip) {
		var level = blockEntity.getLevel();
		if (level == null)
			return;

		IGridNode node = null;
		try {
			node = GridHelper.getExposedNode(level, blockEntity.getBlockPos(), hitResult.getDirection());
		} catch (Throwable t) {
			YogglezMod.LOGGER.warn("AE2 lens: getExposedNode failed", t);
		}

		Ae2NetworkInfo info;
		if (node == null) {
			info = Ae2NetworkInfo.withoutNode();
		} else {
			try {
				info = collect(node);
			} catch (Throwable t) {
				YogglezMod.LOGGER.warn("AE2 lens: reading network data failed", t);
				info = Ae2NetworkInfo.failed();
			}
		}

		for (TooltipLine line : info.lines())
			tooltip.add(LensInfoProvider.toComponent(line));
	}

	private static Ae2NetworkInfo collect(IGridNode node) {
		String status = Ae2NetworkInfo.statusOf(node.isPowered(), node.isActive());
		int used = node.getUsedChannels();
		int max = node.getMaxChannels();
		boolean ok = node.meetsChannelRequirements();
		double idle = node.getIdlePowerUsage();

		IGrid grid = node.getGrid();
		if (grid == null)
			return Ae2NetworkInfo.withoutGrid(status, used, max, ok, idle);

		IEnergyService energy = grid.getEnergyService();
		IPathingService pathing = grid.getPathingService();

		return new Ae2NetworkInfo(false, false, false, status, used, max, ok, idle,
			pathing.getUsedChannels(), energy.getAvgPowerUsage(),
			energy.getStoredPower(), energy.getMaxStoredPower(),
			pathing.isNetworkBooting());
	}
}
