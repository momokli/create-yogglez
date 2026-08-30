package dev.yogglez.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dev.yogglez.client.provider.DemoLensProvider;
import dev.yogglez.lens.LensBindings;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Game-environment tests for the client lens registry (NeoGradle
 * {@code testJunit} / forgejunitdev): vanilla Minecraft classes are
 * bootstrapped headless (no display, no GL, server-side classpath only).
 *
 * <p>These tests prove the provider wiring against real
 * {@link BlockEntityType} instances and that the {@link DemoLensProvider}
 * (pure vanilla) really extracts block name / BE type / NBT overview from a
 * live block entity. The AE2 provider cannot run here (AE2 is not on the
 * JUnit classpath); its data logic is covered headless by
 * {@code Ae2NetworkInfoTest}.
 *
 * <p>Component assertions compare against freshly built expected components
 * (equality covers contents + style), so no client-only chat classes like
 * {@code TranslatableContents} are referenced at compile time.
 */
@Tag("game")
class LensProviderRegistryGameTest {

	private static BlockEntityType<?> type(String id) {
		BlockEntityType<?> type = BuiltInRegistries.BLOCK_ENTITY_TYPE.get(ResourceLocation.parse(id));
		assertNotNull(type, "block entity type not registered: " + id);
		return type;
	}

	@Test
	void registeredProviderIsReturnedForSameType() {
		DemoLensProvider provider = new DemoLensProvider();
		BlockEntityType<?> furnace = type("minecraft:furnace");
		LensProviderRegistry.register(furnace, provider);
		assertSame(provider, LensProviderRegistry.get(furnace));
	}

	@Test
	void unknownTypeHasNoProvider() {
		// beacon is deliberately not bound to any lens in LensBindings
		assertNull(LensProviderRegistry.get(type("minecraft:beacon")));
	}

	@Test
	void allDemoLensBindingsResolveToRegisteredTypes() {
		for (String typeId : LensBindings.blockEntityTypesFor(LensBindings.DEMO_LENS)) {
			DemoLensProvider provider = new DemoLensProvider();
			BlockEntityType<?> type = type(typeId);
			LensProviderRegistry.register(type, provider);
			assertSame(provider, LensProviderRegistry.get(type), "no provider for " + typeId);
		}
	}

	@Test
	void bindingTypeIdsExistInGameRegistries() {
		// the pure LensBindings table must not contain stale/misspelled ids:
		// every id must resolve against the real vanilla registry
		for (String typeId : LensBindings.blockEntityTypesFor(LensBindings.DEMO_LENS))
			assertNotNull(BuiltInRegistries.BLOCK_ENTITY_TYPE.get(ResourceLocation.parse(typeId)),
				"unknown type id in LensBindings: " + typeId);
	}

	@Test
	void demoProviderGathersRealFurnaceData() {
		BlockEntity furnace = new FurnaceBlockEntity(BlockPos.ZERO, Blocks.FURNACE.defaultBlockState());
		List<Component> tooltip = new ArrayList<>();
		new DemoLensProvider().gatherInfo(furnace,
			new BlockHitResult(Vec3.ZERO, Direction.UP, BlockPos.ZERO, false), tooltip);

		assertFalse(tooltip.isEmpty(), "furnace must produce lens tooltip lines");

		// independently compute the expected furnace tag (same code path as the
		// provider: no level -> RegistryAccess.EMPTY) and the expected lines
		CompoundTag tag = furnace.saveWithoutMetadata(RegistryAccess.EMPTY);
		List<String> expectedKeys = new ArrayList<>(tag.getAllKeys());
		expectedKeys.sort(String::compareTo);
		int shown = Math.min(8, expectedKeys.size());

		// header
		assertEquals(
			Component.translatable("lens.yogglez.demo.header").withStyle(ChatFormatting.WHITE),
			tooltip.get(0));

		// block name - same extraction as the provider, so the assertion is
		// independent of whether the locale is initialized in this environment
		String expectedBlockName = Blocks.FURNACE.getName().getString();
		assertEquals(
			Component.translatable("lens.yogglez.demo.block", expectedBlockName).withStyle(ChatFormatting.GRAY),
			tooltip.get(1));

		// block entity type id
		assertEquals(
			Component.translatable("lens.yogglez.demo.type", "minecraft:furnace").withStyle(ChatFormatting.GRAY),
			tooltip.get(2));

		// position
		assertEquals(
			Component.translatable("lens.yogglez.demo.pos", "0", "0", "0").withStyle(ChatFormatting.DARK_GRAY),
			tooltip.get(3));

		// NBT key count + key lines
		assertEquals(
			Component.translatable("lens.yogglez.demo.nbt_keys", String.valueOf(expectedKeys.size()))
				.withStyle(ChatFormatting.GRAY),
			tooltip.get(4));
		for (int i = 0; i < shown; i++)
			assertEquals(
				Component.literal(" - " + expectedKeys.get(i)).withStyle(ChatFormatting.DARK_GRAY),
				tooltip.get(5 + i));

		// exact line count: header/block/type/pos/nbt-count + shown keys (+ ellipsis)
		int expectedLines = 5 + shown + (expectedKeys.size() > 8 ? 1 : 0);
		assertEquals(expectedLines, tooltip.size());
		assertTrue(expectedKeys.size() >= 1, "furnace NBT tag should expose keys");
	}
}
