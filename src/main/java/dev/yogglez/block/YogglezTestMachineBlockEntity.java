package dev.yogglez.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.utility.CreateLang;

import dev.yogglez.registry.YogglezRegistries;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * M2 core proof: this BlockEntity implements Create's
 * {@link IHaveGoggleInformation} (public API). When the player wears the Yogglez
 * Goggles (or any Create goggles) and looks at this block, Create's real goggle
 * overlay renders the info added here - no custom rendering involved.
 *
 * <p>The values are simulated and animated with the world time to make the POC
 * demo lively. {@link #getIcon} replaces the overlay icon with our own goggles.
 *
 * <p>Tooltip content is generated server-safe in {@link #collectGoggleContent()}
 * (also used by the headless gametests); {@link #addToGoggleTooltip} styles the
 * same lines with Create's {@code forGoggles} indent logic, which is client-only.
 */
public class YogglezTestMachineBlockEntity extends BlockEntity implements IHaveGoggleInformation {

	public YogglezTestMachineBlockEntity(BlockPos pos, BlockState state) {
		super(YogglezRegistries.TEST_MACHINE_BE.get(), pos, state);
	}

	// ------------------------------------------------------- simulated process

	private double temperature() {
		long t = level == null ? 0 : level.getGameTime();
		return 180 + 40 * Math.sin(t / 40.0) + 15 * Math.sin(t / 7.0);
	}

	private double pressure() {
		long t = level == null ? 0 : level.getGameTime();
		return 3.2 + 0.8 * Math.sin(t / 25.0);
	}

	private double throughput() {
		long t = level == null ? 0 : level.getGameTime();
		return 42 + 12 * Math.sin(t / 13.0);
	}

	private int heatLevel() {
		double temp = temperature();
		if (temp > 210)
			return 3; // critical
		if (temp > 195)
			return 2; // hot
		if (temp > 180)
			return 1; // normal
		return 0; // cold
	}

	private String statusText() {
		return switch (heatLevel()) {
			case 3 -> "CRITICAL";
			case 2 -> "HOT";
			case 1 -> "RUNNING";
			default -> "COLD";
		};
	}

	private static String fmt(double v) {
		return String.format(Locale.ROOT, "%.1f", v);
	}

	/**
	 * Server-safe tooltip content, identical to what the Create goggle overlay
	 * shows. Used by the overlay and by the headless gametests.
	 */
	public List<Component> collectGoggleContent() {
		List<Component> out = new ArrayList<>();
		out.add(Component.translatable("gui.yogglez.test_machine.status", statusText())
			.withStyle(ChatFormatting.GRAY));
		out.add(Component.translatable("gui.yogglez.test_machine.temperature", fmt(temperature()))
			.withStyle(ChatFormatting.GOLD));
		out.add(Component.translatable("gui.yogglez.test_machine.pressure", fmt(pressure()))
			.withStyle(ChatFormatting.AQUA));
		out.add(Component.translatable("gui.yogglez.test_machine.throughput", fmt(throughput()))
			.withStyle(ChatFormatting.GREEN));
		return out;
	}

	// ------------------------------------------------------------- goggle info

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		CreateLang.blockName(getBlockState())
			.style(ChatFormatting.GRAY)
			.forGoggles(tooltip);

		for (Component line : collectGoggleContent())
			CreateLang.builder()
				.add(line)
				.forGoggles(tooltip, 1);

		if (isPlayerSneaking) {
			CreateLang.translate("gui.yogglez.test_machine.debug",
					"yogglez:test_machine", getBlockPos().toShortString())
				.style(ChatFormatting.DARK_GRAY)
				.forGoggles(tooltip, 1);
		}

		return true;
	}

	@Override
	public ItemStack getIcon(boolean isPlayerSneaking) {
		return YogglezRegistries.YOGGLEZ_GOGGLES.toStack();
	}
}
