package dev.yogglez.registry;

import dev.yogglez.YogglezMod;
import dev.yogglez.block.YogglezTestMachineBlock;
import dev.yogglez.block.YogglezTestMachineBlockEntity;
import dev.yogglez.item.LensItem;
import dev.yogglez.item.YogglezGogglesItem;
import dev.yogglez.lens.YogglezLenses;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Central code-based registry for all yogglez content.
 * All items/blocks are "JSON-ready": models, blockstates and lang files live under
 * src/main/resources/assets/yogglez and are wired up by id, no datagen required.
 */
public final class YogglezRegistries {

	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(YogglezMod.MODID);
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(YogglezMod.MODID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
		DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, YogglezMod.MODID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
		DeferredRegister.create(Registries.CREATIVE_MODE_TAB, YogglezMod.MODID);

	// ------------------------------------------------------------------ items

	/** The modular goggles. Worn in the head slot, stores installed lenses in NBT. */
	public static final DeferredItem<YogglezGogglesItem> YOGGLEZ_GOGGLES =
		ITEMS.register("yogglez_goggles", () -> new YogglezGogglesItem(new Item.Properties().stacksTo(1)));

	/** Base lens item; the concrete lens type is carried as NBT (LensId). */
	public static final DeferredItem<LensItem> LENS =
		ITEMS.register("lens", () -> new LensItem(new Item.Properties().stacksTo(16)));

	// --------------------------------------------------- blocks + block entity

	/** M2 core proof: demo block with a BlockEntity that implements Create's IHaveGoggleInformation. */
	public static final DeferredBlock<YogglezTestMachineBlock> TEST_MACHINE =
		BLOCKS.register("test_machine", YogglezTestMachineBlock::new);

	public static final DeferredItem<BlockItem> TEST_MACHINE_ITEM =
		ITEMS.registerSimpleBlockItem("test_machine", TEST_MACHINE);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<YogglezTestMachineBlockEntity>> TEST_MACHINE_BE =
		BLOCK_ENTITIES.register("test_machine",
			() -> BlockEntityType.Builder.of(YogglezTestMachineBlockEntity::new, TEST_MACHINE.get()).build(null));

	// ------------------------------------------------------------ creative tab

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_TABS.register("yogglez",
		() -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.yogglez"))
			.icon(() -> YOGGLEZ_GOGGLES.toStack())
			.displayItems((parameters, output) -> {
				output.accept(YOGGLEZ_GOGGLES.get());
				// lens variants (same item, different NBT lens id - POC simplification, see README)
				output.accept(LensItem.withLens(new ItemStack(LENS.get()), YogglezLenses.DEMO));
				output.accept(LensItem.withLens(new ItemStack(LENS.get()), YogglezLenses.AE2_NETWORK));
				output.accept(TEST_MACHINE_ITEM.get());
			})
			.build());
}
