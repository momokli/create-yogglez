package dev.yogglez.client.provider;

import java.util.List;

import dev.yogglez.client.LensInfoProvider;
import dev.yogglez.lens.data.DemoLensInfo;
import dev.yogglez.lens.data.NbtOverview;
import dev.yogglez.lens.data.TooltipLine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

/**
 * "Insight Lens" (yogglez:demo): generic block analysis that works for any
 * vanilla block entity without mod-specific knowledge - shows the block name,
 * the block entity type id and an NBT key overview.
 *
 * <p>Registered for a few vanilla block entity types (furnace, chest, hopper,
 * brewing stand) in the client init (see {@code LensBindings}).
 *
 * <p>All data is extracted here (game classes) and delegated to the pure
 * {@link DemoLensInfo} record, which is unit-tested headless.
 */
public class DemoLensProvider implements LensInfoProvider {

	@Override
	public void gatherInfo(BlockEntity blockEntity, BlockHitResult hitResult, List<Component> tooltip) {
		BlockPos pos = blockEntity.getBlockPos();

		// block name (localized) and block entity type registry id
		String blockName = blockEntity.getBlockState().getBlock().getName().getString();
		ResourceLocation typeId = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType());

		// NBT key overview (client-side copy of the block entity data)
		RegistryAccess registryAccess = blockEntity.getLevel() != null
			? blockEntity.getLevel().registryAccess()
			: RegistryAccess.EMPTY;
		CompoundTag tag = blockEntity.saveWithoutMetadata(registryAccess);
		List<String> keys = tag.getAllKeys().stream().toList();

		DemoLensInfo info = new DemoLensInfo(
			blockName,
			typeId != null ? typeId.toString() : "unregistered",
			pos.getX(), pos.getY(), pos.getZ(),
			NbtOverview.of(keys));

		for (TooltipLine line : info.lines())
			tooltip.add(LensInfoProvider.toComponent(line));
	}
}
