package dev.yogglez.block;

import javax.annotation.Nullable;

import dev.yogglez.registry.YogglezRegistries;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/**
 * Simple demo block ("Yogglez Test Machine") that hosts the M2 core-proof
 * BlockEntity. No rotation/kinetics - it only exists to prove that a yogglez
 * BlockEntity shows custom info in the real Create goggle overlay.
 */
public class YogglezTestMachineBlock extends Block implements EntityBlock {

	public YogglezTestMachineBlock() {
		super(Properties.of()
			.mapColor(MapColor.METAL)
			.strength(2.0F, 6.0F));
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return YogglezRegistries.TEST_MACHINE_BE.get().create(pos, state);
	}
}

