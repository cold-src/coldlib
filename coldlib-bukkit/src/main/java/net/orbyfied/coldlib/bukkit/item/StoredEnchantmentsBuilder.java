package net.orbyfied.coldlib.bukkit.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.orbyfied.coldlib.util.Container;
import net.orbyfied.coldlib.util.Self;
import org.bukkit.enchantments.Enchantment;

import static net.orbyfied.coldlib.bukkit.nms.NbtUtil.getOrCreateList;

/**
 * Builder adapter for modifying the stored enchantments
 * (enchantments on enchanted books).
 *
 * @param <S> Self type.
 */
public interface StoredEnchantmentsBuilder<S extends Container<ItemStack>>
        extends Container<ItemStack>, Self<S>
{

    /**
     * Adds a stored enchantment of type {@code id}
     * with level {@code level} to the item stack.
     * This will not remove any previous enchantments
     * set with the same identifier. This does not
     * check for the max level of the enchantment.
     *
     * @param id The enchantment type identifier.
     * @param level The enchantment level.
     * @return This.
     */
    default S addStoredEnchantment(String id,
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
     * @see StoredEnchantmentsBuilder#addStoredEnchantment(String, int)
     */
    default S addStoredEnchantment(Enchantment enchantment, int level) {
        return addStoredEnchantment(enchantment.getKey().toString(), level);
    }

    /**
     * Remove all stored enchantments set with
     * the given identifier.
     *
     * @param id The enchantment type identifier.
     * @return This.
     */
    default S removeStoredEnchantment(String id) {
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
     * @see StoredEnchantmentsBuilder#removeStoredEnchantment(String)
     */
    default S removeStoredEnchantment(Enchantment enchantment) {
        return removeStoredEnchantment(enchantment.getKey().toString());
    }

    /**
     * Get the level of a stored enchantment if present,
     * otherwise it returns null.
     *
     * @param id The enchantment type identifier.
     * @return The level or null if absent.
     */
    default Integer getStoredEnchantmentLevel(String id) {
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
     * @see StoredEnchantmentsBuilder#getStoredEnchantmentLevel(String)
     */
    default Integer getStoredEnchantmentLevel(Enchantment enchantment) {
        return getStoredEnchantmentLevel(enchantment.getKey().toString());
    }

}
