package dev.yogglez.client.provider;

import java.util.List;

import dev.yogglez.client.LensInfoProvider;

import net.minecraft.ChatFormatting;
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
 * brewing stand) in the client init.
 */
public class DemoLensProvider implements LensInfoProvider {

	@Override
	public void gatherInfo(BlockEntity blockEntity, BlockHitResult hitResult, List<Component> tooltip) {
		BlockPos pos = blockEntity.getBlockPos();

		tooltip.add(Component.translatable("lens.yogglez.demo.header").withStyle(ChatFormatting.WHITE));

		tooltip.add(Component.translatable("lens.yogglez.demo.block")
			.append(blockEntity.getBlockState().getBlock().getName())
			.withStyle(ChatFormatting.GRAY));

		ResourceLocation typeId = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType());
		tooltip.add(Component.translatable("lens.yogglez.demo.type", typeId).withStyle(ChatFormatting.GRAY));

		tooltip.add(Component.translatable("lens.yogglez.demo.pos", pos.getX(), pos.getY(), pos.getZ())
			.withStyle(ChatFormatting.DARK_GRAY));

		// NBT key overview (client-side copy of the block entity data)
		RegistryAccess registryAccess = blockEntity.getLevel() != null
			? blockEntity.getLevel().registryAccess()
			: RegistryAccess.EMPTY;
		CompoundTag tag = blockEntity.saveWithoutMetadata(registryAccess);
		List<String> keys = tag.getAllKeys().stream().sorted().toList();

		tooltip.add(Component.translatable("lens.yogglez.demo.nbt_keys", keys.size()).withStyle(ChatFormatting.GRAY));
		int shown = Math.min(8, keys.size());
		for (int i = 0; i < shown; i++)
			tooltip.add(Component.literal(" - " + keys.get(i)).withStyle(ChatFormatting.DARK_GRAY));
		if (keys.size() > shown)
			tooltip.add(Component.literal(" ...").withStyle(ChatFormatting.DARK_GRAY));
	}
}
