package net.orbyfied.coldlib.bukkit.item;

import coldsrc.coldlib.util.logic.BitFlag;

/**
 * Representation of hide flags for items,
 * which hides certain parts of the display
 * for the viewer.
 *
 * No full docs yet.
 */
public enum HideFlag implements BitFlag {

    ENCHANTMENTS(0),
    ATTRIBUTES(1),
    UNBREAKABLE(2),
    CAN_DESTROY(3),
    CAN_PLACE_ON(4),

    /**
     * From: https://minecraft.fandom.com/wiki/Player.dat_format#Item_structure
     * "For various other information (including potion effects, "StoredEnchantments",
     * written book "generation" and "author", "Explosion", "Fireworks", and map tooltips)."
     */
    OTHER(5),

    DYED(6),

    ;

    // the bit offset
    int bitOffset;

    HideFlag(int bitOffset) {
        this.bitOffset = bitOffset;
    }

    @Override
    public int getBitOffset() {
        return bitOffset;
    }

}
