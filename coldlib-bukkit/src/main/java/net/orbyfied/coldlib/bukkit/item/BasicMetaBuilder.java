package net.orbyfied.coldlib.bukkit.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.orbyfied.coldlib.bukkit.nms.LegacyNmsHelper;
import net.orbyfied.coldlib.util.Container;
import net.orbyfied.coldlib.util.Self;
import net.orbyfied.coldlib.util.logic.BitFlag;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.orbyfied.coldlib.bukkit.nms.NbtUtil.getOrCreateCompound;
import static net.orbyfied.coldlib.bukkit.nms.NbtUtil.getOrCreateList;

/**
 * Basic meta builder for general item meta,
 * which is applicable for (almost) every item.
 *
 * @param <S> The self return type.
 */
@SuppressWarnings("rawtypes")
public interface BasicMetaBuilder<S extends BasicMetaBuilder>
        extends Container<ItemStack>, Self<S>
{

    /**
     * Set the damage value on the item.
     *
     * @param value The damage.
     * @return This.
     */
    default S damage(int value) {
        get().setDamageValue(value);
        return self();
    }

    /**
     * Set the durability left on the item.
     * This is calculated using {@link ItemStack#getMaxDamage()}.
     *
     * @param value The durability to be left.
     * @return This.
     */
    default S durability(int value) {
        ItemStack stack = get();
        stack.setDamageValue(stack.getMaxDamage() - value);
        return self();
    }

    /**
     * Set the if the item is unbreakable.
     *
     * @param value True/false.
     * @return This.
     */
    default S unbreakable(boolean value) {
        ItemStack stack = get();
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean("Unbreakable", value);
        return self();
    }

    /**
     * Set the items custom model data
     * tag to the provided value.
     *
     * @param value The value.
     * @return This.
     */
    default S customModelData(int value) {
        ItemStack stack = get();
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("CustomModelData", value);
        return self();
    }

    /**
     * Set the provided {@link HideFlag} to the
     * given value in the item tag.
     *
     * @param flag The hide/bit flag.
     * @param value The value to set.
     * @return This.
     */
    default S hideFlag(BitFlag flag, boolean value) {
        ItemStack stack = get();
        CompoundTag tag = stack.getOrCreateTag();

        int i = tag.contains("HideFlags", Tag.TAG_INT) ? tag.getInt("HideFlags") : 0;
        i = flag.set(i, value);

        tag.putInt("HideFlags", i);

        // return self
        return self();
    }

    /**
     * Set all provided flags to the same
     * given value in the item tag.
     *
     * @param value The value to set them all to.
     * @param flags The flags to set.
     * @return This.
     */
    default S hideFlags(boolean value, HideFlag... flags) {
        ItemStack stack = get();
        CompoundTag tag = stack.getOrCreateTag();

        int i = tag.contains("HideFlags", Tag.TAG_INT) ? tag.getInt("HideFlags") : 0;
        for (HideFlag flag : flags)
            i = flag.set(i, value);

        tag.putInt("HideFlags", i);

        // return self
        return self();
    }

    /**
     * Set the repair cost in levels for this item.
     *
     * @param cost The repair cost.
     * @return This.
     */
    default S repairCost(int cost) {
        ItemStack stack = get();
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("RepairCost", cost);

        // return self
        return self();
    }

    /**
     * Adds an attribute modifier for the
     * specified attribute.
     *
     * @param attribute The identifier of the attribute to apply to.
     * @param modifier The modifier to apply.
     * @return This.
     */
    default S addAttributeModifier(String attribute, AttributeModifier modifier) {
        ItemStack stack = get();
        CompoundTag tag = stack.getOrCreateTag();
        ListTag modsTag = getOrCreateList(tag, "AttributeModifiers", Tag.TAG_COMPOUND);

        CompoundTag modTag = new CompoundTag();
        modTag.putString("AttributeName", attribute);
        modifier.save(modTag);

        modsTag.add(modTag);

        // return self
        return self();
    }

    /**
     * @see BasicMetaBuilder#addAttributeModifier(String, AttributeModifier)
     */
    default S addAttributeModifier(Attribute attribute, AttributeModifier modifier) {
        return addAttributeModifier(attribute.getKey().toString(), modifier);
    }

    /**
     * Set the lore on the item stack
     * to the given list of chat components.
     *
     * @param list The list of components.
     *             Each item is one line of lore.
     * @return This.
     */
    default S lore(List<Component> list) {
        ItemStack stack = get();
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag displayTag = getOrCreateCompound(tag, "display");
        ListTag loreTag = getOrCreateList(displayTag, "Lore", Tag.TAG_STRING);

        for (Component c : list) {
            String json = Component.Serializer.toJson(c);
            loreTag.add(StringTag.valueOf(json));
        }

        // return self
        return self();
    }

    /**
     * @see BasicMetaBuilder#lore(List)
     */
    default S lore(Component... list) {
        this.loreLegacy("d", "d");
        return lore(Arrays.asList(list));
    }

    /**
     * @see BasicMetaBuilder#lore(List)
     */
    default S loreLegacy(List<String> list) {
        return lore(list.stream()
                .map(s -> ChatColor.RESET + s)
                .map(LegacyNmsHelper::getComponentFromString)
                .collect(Collectors.toList())
        );
    }

    /**
     * @see BasicMetaBuilder#lore(List)
     */
    default S loreLegacy(String... list) {
        return lore(Arrays.stream(list)
                .map(s -> ChatColor.RESET + s)
                .map(LegacyNmsHelper::getComponentFromString)
                .collect(Collectors.toList())
        );
    }

    /**
     * Set the display name for the item.
     *
     * @param component The chat component to use as name.
     * @return This.
     */
    default S name(Component component) {
        ItemStack stack = get();
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag displayTag = getOrCreateCompound(tag, "display");

        displayTag.putString("Name", Component.Serializer.toJson(component));

        // return self
        return self();
    }

    /**
     * Set the display name for the item.
     *
     * @param legacyText The legacy text to use as name.
     * @return This.
     */
    default S nameLegacy(String legacyText) {
        return name(LegacyNmsHelper.getComponentFromString(ChatColor.RESET + legacyText));
    }

    /**
     * Adds an enchantment of type {@code id}
     * with level {@code level} to the item stack.
     * This will not remove any previous enchantments
     * set with the same identifier. This does not
     * check for the max level of the enchantment
     * and does practically the same as
     * {@link org.bukkit.inventory.ItemStack#addUnsafeEnchantment(Enchantment, int)}
     *
     * @param id The enchantment type identifier.
     * @param level The enchantment level.
     * @return This.
     */
    default S addEnchantment(String id,
                             int level) {
        ItemStack stack = get();
        CompoundTag tag = stack.getOrCreateTag();
        ListTag enchantmentList = getOrCreateList(tag, "Enchantments", Tag.TAG_COMPOUND);

        CompoundTag enchantmentTag = new CompoundTag();
        enchantmentTag.putString("id", id);
        enchantmentTag.putInt("lvl", level);

        enchantmentList.add(enchantmentTag);

        // return this
        return self();
    }

    /**
     * @see BasicMetaBuilder#addEnchantment(String, int)
     */
    default S addEnchantment(Enchantment enchantment, int level) {
        return addEnchantment(enchantment.getKey().toString(), level);
    }

    /**
     * Remove all enchantments set with
     * the given identifier.
     *
     * @param id The enchantment type identifier.
     * @return This.
     */
    default S removeEnchantment(String id) {
        ItemStack stack = get();
        CompoundTag tag = stack.getOrCreateTag();
        ListTag enchantmentList = tag.getList("Enchantments", Tag.TAG_COMPOUND);

        enchantmentList.removeIf(tag1 -> {
            if (!(tag1 instanceof CompoundTag enchTag)) return false;
            return id.equals(enchTag.getString("id"));
        });

        // return this
        return self();
    }

    /**
     * @see BasicMetaBuilder#removeEnchantment(String)
     */
    default S removeEnchantment(Enchantment enchantment) {
        return removeEnchantment(enchantment.getKey().toString());
    }

    /**
     * Get the level of an enchantment if present,
     * otherwise it returns null.
     *
     * @param id The enchantment type identifier.
     * @return The level or null if absent.
     */
    default Integer getEnchantmentLevel(String id) {
        ItemStack stack = get();
        CompoundTag tag = stack.getOrCreateTag();
        ListTag enchantmentList = tag.getList("Enchantments", Tag.TAG_COMPOUND);

        for (Tag tag1 : enchantmentList) {
            if (!(tag1 instanceof CompoundTag enchTag)) continue;

            if (id.equals(enchTag.getString("id")))
                return enchTag.getInt("lvl");
        }

        return null;
    }

    /**
     * @see BasicMetaBuilder#getEnchantmentLevel(String)
     */
    default Integer getEnchantmentLevel(Enchantment enchantment) {
        return getEnchantmentLevel(enchantment.getKey().toString());
    }

}
