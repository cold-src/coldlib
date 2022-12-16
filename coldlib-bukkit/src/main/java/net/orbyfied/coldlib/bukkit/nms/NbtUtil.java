package net.orbyfied.coldlib.bukkit.nms;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class NbtUtil {

    public static CompoundTag getOrCreateCompound(CompoundTag tag,
                                                  String key) {
        // get from tag
        Tag t = tag.get(key);
        // check for null and type
        if (!(t instanceof CompoundTag)) {
            // create and put
            t = new CompoundTag();
            tag.put(key, t);
        }

        // return
        return (CompoundTag) t;
    }

    public static ListTag getOrCreateList(CompoundTag tag,
                                          String key,
                                          int type) {
        // get from tag
        Tag t = tag.get(key);
        // check for null and type
        if (!(t instanceof ListTag)) {
            // create and put
            t = tag.getList(key, type);
            tag.put(key, t);
        }

        // return
        return (ListTag) t;
    }

}
