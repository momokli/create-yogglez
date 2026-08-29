package dev.yogglez.gametest;

import java.util.ArrayList;
import java.util.List;

import dev.yogglez.YogglezMod;
import dev.yogglez.block.YogglezTestMachineBlockEntity;
import dev.yogglez.item.YogglezGogglesItem;
import dev.yogglez.lens.YogglezLenses;
import dev.yogglez.registry.YogglezRegistries;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.gametest.GameTestHolder;

/**
 * Automated verification for the POC (runs headless via {@code ./gradlew runGameTestServer}):
 * <ol>
 *   <li>M2: the demo block entity produces goggle tooltip lines (Create overlay data path)</li>
 *   <li>M1: lens install / activate / cycle NBT logic on the goggles item</li>
 *   <li>lens registry contains the known lens ids</li>
 * </ol>
 */
@GameTestHolder(YogglezMod.MODID)
public final class YogglezGameTests {

	private YogglezGameTests() {}

	/** M2 core proof (server-side part): the goggle tooltip content contains the expected lines. */
	@GameTest(template = "empty")
	public static void test_machine_goggle_tooltip(GameTestHelper helper) {
		BlockPos pos = new BlockPos(3, 1, 3);
		helper.setBlock(pos, YogglezRegistries.TEST_MACHINE.get().defaultBlockState());

		var be = helper.getBlockEntity(pos);
		if (!(be instanceof YogglezTestMachineBlockEntity machine)) {
			helper.fail("expected YogglezTestMachineBlockEntity, got " + be);
			return;
		}

		List<Component> tooltip = machine.collectGoggleContent();
		if (tooltip.isEmpty()) {
			helper.fail("goggle tooltip content empty");
			return;
		}

		String joined = tooltip.stream().map(Component::getString).reduce("", (a, b) -> a + " | " + b);
		if (!joined.contains("Temperature")) {
			helper.fail("tooltip missing 'Temperature' line: " + joined);
			return;
		}
		if (!joined.contains("Status")) {
			helper.fail("tooltip missing 'Status' line: " + joined);
			return;
		}

		helper.succeed();
	}

	/** M1: lens install / activate / cycle logic. */
	@GameTest(template = "empty")
	public static void test_goggles_lens_lifecycle(GameTestHelper helper) {
		ItemStack goggles = new ItemStack(YogglezRegistries.YOGGLEZ_GOGGLES.get());

		if (!YogglezGogglesItem.installLens(goggles, YogglezLenses.DEMO)) {
			helper.fail("install demo lens failed");
			return;
		}
		// duplicate install must be a no-op
		if (YogglezGogglesItem.installLens(goggles, YogglezLenses.DEMO)) {
			helper.fail("duplicate lens install must not succeed");
			return;
		}
		YogglezGogglesItem.installLens(goggles, YogglezLenses.AE2_NETWORK);
		YogglezGogglesItem.setActiveLens(goggles, YogglezLenses.AE2_NETWORK);

		if (!YogglezLenses.AE2_NETWORK.equals(YogglezGogglesItem.getActiveLens(goggles))) {
			helper.fail("active lens not set to AE2 network lens");
			return;
		}

		YogglezGogglesItem.cycleLens(goggles);
		if (!YogglezLenses.DEMO.equals(YogglezGogglesItem.getActiveLens(goggles))) {
			helper.fail("cycle did not advance to demo lens");
			return;
		}
		YogglezGogglesItem.cycleLens(goggles);
		if (!YogglezLenses.AE2_NETWORK.equals(YogglezGogglesItem.getActiveLens(goggles))) {
			helper.fail("cycle did not wrap around to AE2 lens");
			return;
		}

		helper.succeed();
	}

	/** Lens registry contains the known lenses. */
	@GameTest(template = "empty")
	public static void test_lens_registry(GameTestHelper helper) {
		if (!YogglezLenses.isRegistered(YogglezLenses.DEMO)) {
			helper.fail("demo lens missing from registry");
			return;
		}
		if (!YogglezLenses.isRegistered(YogglezLenses.AE2_NETWORK)) {
			helper.fail("ae2 network lens missing from registry");
			return;
		}
		helper.succeed();
	}
}
