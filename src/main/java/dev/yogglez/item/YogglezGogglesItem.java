package dev.yogglez.item;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.content.equipment.goggles.GogglesItem;

import dev.yogglez.lens.YogglezLenses;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

/**
 * The modular "Yogglez Goggles".
 *
 * <p>Extends Create's {@link GogglesItem} so it can be worn in the head slot and
 * swapped with a right-click like the Engineer's Goggles. Create recognizes it as
 * goggles via {@code GogglesItem.addIsWearingPredicate(...)} (registered in
 * {@link dev.yogglez.YogglezMod}) - that is what turns on the real Create goggle
 * overlay for our own BlockEntities (M2 core proof).
 *
 * <p>Modularity lives in the item NBT ({@value #TAG_ROOT}, stored in the
 * {@code minecraft:custom_data} component):
 * <ul>
 *   <li>{@value #TAG_LENSES} - list of installed lens ids ({@link ResourceLocation} strings)</li>
 *   <li>{@value #TAG_ACTIVE_LENS} - index of the currently active lens</li>
 * </ul>
 * Lenses are installed by right-clicking a {@link LensItem} while the goggles are
 * held in the offhand. The active lens is cycled with the "Cycle Lens" keybind.
 */
public class YogglezGogglesItem extends GogglesItem {

	public static final String TAG_ROOT = "yogglez";
	public static final String TAG_LENSES = "Lenses";
	public static final String TAG_ACTIVE_LENS = "ActiveLens";

	public YogglezGogglesItem(Properties properties) {
		super(properties);
	}

	// ------------------------------------------------------------------ NBT API

	public static List<String> getInstalledLenses(ItemStack stack) {
		List<String> lenses = new ArrayList<>();
		if (stack.isEmpty() || !(stack.getItem() instanceof YogglezGogglesItem))
			return lenses;
		CompoundTag root = getYogglezTag(stack);
		if (root == null || !root.contains(TAG_LENSES, Tag.TAG_LIST))
			return lenses;
		ListTag list = root.getList(TAG_LENSES, Tag.TAG_STRING);
		for (int i = 0; i < list.size(); i++)
			lenses.add(list.getString(i));
		return lenses;
	}

	public static boolean hasLens(ItemStack stack, ResourceLocation lens) {
		return getInstalledLenses(stack).contains(lens.toString());
	}

	/** @return true if the lens was newly installed */
	public static boolean installLens(ItemStack stack, ResourceLocation lens) {
		if (stack.isEmpty() || !(stack.getItem() instanceof YogglezGogglesItem))
			return false;
		String lensId = lens.toString();
		CompoundTag tag = getOrCreateCustomTag(stack);
		CompoundTag root = tag.getCompound(TAG_ROOT);
		ListTag list = root.getList(TAG_LENSES, Tag.TAG_STRING);
		for (int i = 0; i < list.size(); i++)
			if (list.getString(i).equals(lensId))
				return false; // already installed
		list.add(StringTag.valueOf(lensId));
		root.put(TAG_LENSES, list);
		saveCustomTag(stack, tag);
		return true;
	}

	/** @return the active lens id, or null if no lens is installed */
	public static ResourceLocation getActiveLens(ItemStack stack) {
		if (stack.isEmpty() || !(stack.getItem() instanceof YogglezGogglesItem))
			return null;
		List<String> lenses = getInstalledLenses(stack);
		if (lenses.isEmpty())
			return null;
		CompoundTag root = getYogglezTag(stack);
		int index = root != null && root.contains(TAG_ACTIVE_LENS, Tag.TAG_INT) ? root.getInt(TAG_ACTIVE_LENS) : 0;
		if (index < 0 || index >= lenses.size())
			index = 0;
		return ResourceLocation.tryParse(lenses.get(index));
	}

	public static void setActiveLens(ItemStack stack, ResourceLocation lens) {
		if (stack.isEmpty() || !(stack.getItem() instanceof YogglezGogglesItem))
			return;
		int index = getInstalledLenses(stack).indexOf(lens.toString());
		if (index < 0)
			return;
		CompoundTag tag = getOrCreateCustomTag(stack);
		tag.getCompound(TAG_ROOT).putInt(TAG_ACTIVE_LENS, index);
		saveCustomTag(stack, tag);
	}

	/** Cycles to the next installed lens (wraps around). */
	public static void cycleLens(ItemStack stack) {
		if (stack.isEmpty() || !(stack.getItem() instanceof YogglezGogglesItem))
			return;
		List<String> lenses = getInstalledLenses(stack);
		if (lenses.isEmpty())
			return;
		CompoundTag tag = getOrCreateCustomTag(stack);
		CompoundTag root = tag.getCompound(TAG_ROOT);
		int index = root.contains(TAG_ACTIVE_LENS, Tag.TAG_INT) ? root.getInt(TAG_ACTIVE_LENS) : -1;
		root.putInt(TAG_ACTIVE_LENS, (index + 1) % lenses.size());
		saveCustomTag(stack, tag);
	}

	// ------------------------------------------------------------ custom data

	private static CompoundTag getYogglezTag(ItemStack stack) {
		CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		if (data.isEmpty())
			return null;
		CompoundTag tag = data.copyTag();
		return tag.contains(TAG_ROOT, Tag.TAG_COMPOUND) ? tag.getCompound(TAG_ROOT) : null;
	}

	/**
	 * Returns a mutable copy of the {@code minecraft:custom_data} tag with an
	 * empty {@value #TAG_ROOT} compound ensured. Callers must mutate the returned
	 * tag and then call {@link #saveCustomTag(ItemStack, CompoundTag)}.
	 * (CustomData.of copies, so mutating after saving would be lost.)
	 */
	private static CompoundTag getOrCreateCustomTag(ItemStack stack) {
		CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		CompoundTag tag = data.copyTag();
		if (!tag.contains(TAG_ROOT, Tag.TAG_COMPOUND))
			tag.put(TAG_ROOT, new CompoundTag());
		return tag;
	}

	private static void saveCustomTag(ItemStack stack, CompoundTag tag) {
		stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
	}

	// ------------------------------------------------------------- item behavior

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
		TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		List<String> lenses = getInstalledLenses(stack);
		if (lenses.isEmpty()) {
			tooltipComponents.add(Component.translatable("item.yogglez.yogglez_goggles.tooltip.empty")
				.withStyle(ChatFormatting.GRAY));
		} else {
			ResourceLocation active = getActiveLens(stack);
			for (String lensId : lenses) {
				ResourceLocation id = ResourceLocation.tryParse(lensId);
				if (id == null)
					continue;
				Component name = YogglezLenses.getDisplayName(id);
				if (id.equals(active))
					tooltipComponents.add(Component.literal("> ").append(name).withStyle(ChatFormatting.GOLD));
				else
					tooltipComponents.add(Component.literal("  ").append(name).withStyle(ChatFormatting.DARK_GRAY));
			}
		}
	}
}
