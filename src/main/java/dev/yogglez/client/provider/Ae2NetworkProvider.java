package dev.yogglez.client.provider;

import java.util.List;

import appeng.api.networking.GridHelper;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.pathing.IPathingService;

import dev.yogglez.YogglezMod;
import dev.yogglez.client.LensInfoProvider;

import net.minecraft.ChatFormatting;
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
 * absent) the tooltip degrades gracefully instead of crashing.
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

		if (node == null) {
			tooltip.add(Component.translatable("lens.yogglez.ae2.no_node").withStyle(ChatFormatting.RED));
			return;
		}

		try {
			// ---- node level ----
			tooltip.add(Component.translatable("lens.yogglez.ae2.header").withStyle(ChatFormatting.WHITE));

			tooltip.add(Component.translatable("lens.yogglez.ae2.status",
					nodeStatus(node)).withStyle(ChatFormatting.GRAY));

			tooltip.add(Component.translatable("lens.yogglez.ae2.channels",
					node.getUsedChannels(), node.getMaxChannels())
				.withStyle(node.meetsChannelRequirements() ? ChatFormatting.GOLD : ChatFormatting.RED));

			tooltip.add(Component.translatable("lens.yogglez.ae2.idle_power",
					formatEnergy(node.getIdlePowerUsage())).withStyle(ChatFormatting.AQUA));

			// ---- grid level ----
			IGrid grid = node.getGrid();
			if (grid == null) {
				tooltip.add(Component.translatable("lens.yogglez.ae2.no_grid").withStyle(ChatFormatting.GRAY));
				return;
			}

			IEnergyService energy = grid.getEnergyService();
			IPathingService pathing = grid.getPathingService();

			tooltip.add(Component.translatable("lens.yogglez.ae2.grid_header").withStyle(ChatFormatting.WHITE));
			tooltip.add(Component.translatable("lens.yogglez.ae2.grid_channels",
					pathing.getUsedChannels()).withStyle(ChatFormatting.GOLD));
			tooltip.add(Component.translatable("lens.yogglez.ae2.grid_power",
					formatEnergy(energy.getAvgPowerUsage())).withStyle(ChatFormatting.AQUA));
			tooltip.add(Component.translatable("lens.yogglez.ae2.grid_storage",
					formatEnergy(energy.getStoredPower()), formatEnergy(energy.getMaxStoredPower()))
				.withStyle(ChatFormatting.LIGHT_PURPLE));
			tooltip.add(Component.translatable("lens.yogglez.ae2.grid_booting",
					bootingText(pathing.isNetworkBooting())).withStyle(ChatFormatting.GRAY));
		} catch (Throwable t) {
			YogglezMod.LOGGER.warn("AE2 lens: reading network data failed", t);
			tooltip.add(Component.translatable("lens.yogglez.ae2.error").withStyle(ChatFormatting.RED));
		}
	}

	private static String nodeStatus(IGridNode node) {
		if (!node.isPowered())
			return "UNPOWERED";
		if (!node.isActive())
			return "INACTIVE";
		return "ACTIVE";
	}

	private static String bootingText(boolean booting) {
		return booting ? "YES" : "NO";
	}

	/** AE2 energy is in AE units; format with up to one decimal. */
	private static String formatEnergy(double ae) {
		return String.format("%.1f AE", ae);
	}
}
