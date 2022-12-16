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
import org.bukkit.enchantments.Enchantment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Basic meta builder for general item meta,
 * which is applicable for (almost) every item.
 *
 * @param <S> The self return type.
 */
@SuppressWarnings("rawtypes")
public interface BasicMetaBuilder<S extends ItemBuilder>
        extends Container<ItemStack>, Self<S>
{

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
        CompoundTag displayTag = tag.getCompound("display");
        ListTag loreTag = displayTag.getList("Lore", Tag.TAG_STRING);

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
                .map(LegacyNmsHelper::getComponentFromString)
                .collect(Collectors.toList())
        );
    }

    /**
     * @see BasicMetaBuilder#lore(List)
     */
    default S loreLegacy(String... list) {
        return lore(Arrays.stream(list)
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
        CompoundTag displayTag = tag.getCompound("display");

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
    default S name(String legacyText) {
        return name(LegacyNmsHelper.getComponentFromString(legacyText));
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
        ListTag enchantmentList = tag.getList("Enchantments", Tag.TAG_COMPOUND);

        CompoundTag enchantmentTag = new CompoundTag();
        enchantmentTag.putString("id", id);
        enchantmentTag.putInt("level", level);

        enchantmentList.add(enchantmentTag);

        // return this
        return self();
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

}
