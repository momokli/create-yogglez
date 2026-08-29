package dev.yogglez.item;

import java.util.List;

import dev.yogglez.lens.YogglezLenses;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

/**
 * The base "Lens" item. The concrete lens type is carried in NBT
 * ({@value #TAG_LENS_ID}, a {@link ResourceLocation} string, see {@link YogglezLenses}).
 *
 * <p>POC simplification: there is a single Lens item; the creative tab offers
 * pre-configured variants (Insight Lens, AE2 Network Lens). In a full
 * implementation (M4+) these become distinct items/recipes.
 *
 * <p>Usage: hold the goggles in the offhand and right-click with a lens in the
 * main hand - the lens is installed into the goggles and activated.
 */
public class LensItem extends Item {

	public static final String TAG_ROOT = "yogglez";
	public static final String TAG_LENS_ID = "LensId";

	public LensItem(Properties properties) {
		super(properties);
	}

	public static ItemStack withLens(ItemStack stack, ResourceLocation lensId) {
		CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		CompoundTag tag = data.copyTag();
		CompoundTag root = tag.contains(TAG_ROOT, Tag.TAG_COMPOUND) ? tag.getCompound(TAG_ROOT) : new CompoundTag();
		root.putString(TAG_LENS_ID, lensId.toString());
		tag.put(TAG_ROOT, root);
		stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
		return stack;
	}

	public static ResourceLocation getLensId(ItemStack stack) {
		if (stack.isEmpty() || !(stack.getItem() instanceof LensItem))
			return null;
		CompoundTag root = getYogglezTag(stack);
		if (root == null || !root.contains(TAG_LENS_ID))
			return null;
		return ResourceLocation.tryParse(root.getString(TAG_LENS_ID));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack lensStack = player.getItemInHand(hand);
		ResourceLocation lensId = getLensId(lensStack);
		if (lensId == null)
			return InteractionResultHolder.pass(lensStack);

		ItemStack goggles = player.getOffhandItem();
		if (!(goggles.getItem() instanceof YogglezGogglesItem))
			return InteractionResultHolder.pass(lensStack);

		if (!level.isClientSide) {
			if (YogglezGogglesItem.installLens(goggles, lensId)) {
				YogglezGogglesItem.setActiveLens(goggles, lensId);
				lensStack.shrink(1);
				level.playSound(null, player.getX(), player.getY(), player.getZ(),
					SoundEvents.ARMOR_EQUIP_CHAIN, SoundSource.PLAYERS, 1.0F, 1.0F);
			}
		}
		return InteractionResultHolder.success(lensStack);
	}

	private static CompoundTag getYogglezTag(ItemStack stack) {
		CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		if (data.isEmpty())
			return null;
		CompoundTag tag = data.copyTag();
		return tag.contains(TAG_ROOT, Tag.TAG_COMPOUND) ? tag.getCompound(TAG_ROOT) : null;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
		TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		ResourceLocation lensId = getLensId(stack);
		if (lensId != null) {
			tooltipComponents.add(Component.translatable("item.yogglez.lens.tooltip",
				YogglezLenses.getDisplayName(lensId)).withStyle(ChatFormatting.GRAY));
			tooltipComponents.add(Component.translatable("item.yogglez.lens.tooltip.howto")
				.withStyle(ChatFormatting.DARK_GRAY));
		}
	}
}
