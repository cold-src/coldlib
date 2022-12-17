package net.orbyfied.coldlib.bukkit.item;

import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * A representation of an attribute modifier.
 */
public record AttributeModifier(
        String name, UUID uuid,
        Operation operation,
        double amount, EquipmentSlot slot
) {

    public enum Operation {

        /**
         * Simply adds the amount configured to
         * the modifier value.
         */
        ADD(0),

        /**
         * No docs. Refer to: https://minecraft.fandom.com/wiki/Attribute#Modifiers
         */
        MULTIPLY_BASE(1),

        /**
         * No docs. Refer to: https://minecraft.fandom.com/wiki/Attribute#Modifiers
         */
        MULTIPLY(2)

        ;

        // properties
        final int tagValue;

        Operation(int tagValue) {
            this.tagValue = tagValue;
        }

        /**
         * Get the tag value, this is the
         * value set to the operation tag
         * when serializing the modifier.
         *
         * @return The tag value.
         */
        public int getTagValue() {
            return tagValue;
        }

    }

    ///////////////////////////////////////////////////

    /**
     * Convert a {@link org.bukkit.attribute.AttributeModifier} to
     * this type of modifier.
     *
     * @param modifier The input modifier.
     * @return The new instance of {@link AttributeModifier}.
     */
    public static AttributeModifier from(org.bukkit.attribute.AttributeModifier modifier) {
        return new AttributeModifier(
                modifier.getName(),
                modifier.getUniqueId(),

                Operation.values()[modifier.getOperation().ordinal()],
                modifier.getAmount(),
                modifier.getSlot() == null ? null : EquipmentSlot.valueOf(modifier.getSlot().name())
        );
    }

    /**
     * Name counter.
     */
    private static int nCt = 0;

    /**
     * Creates a new modifier, without specifying a name or UUID.
     * They will be randomly generated.
     *
     * @param operation The operation.
     * @param amount The amount/magnitude of the operation.
     * @param slot The slot to apply to.
     * @return The new instance.
     */
    public static AttributeModifier create(Operation operation, double amount, @Nullable EquipmentSlot slot) {
        return new AttributeModifier(
                Integer.toHexString(nCt),
                UUID.randomUUID(),

                operation,
                amount,
                slot
        );
    }

    /**
     * @see AttributeModifier#create(Operation, double, EquipmentSlot)
     *
     * {@code slot} is defaulted to null.
     */
    public static AttributeModifier create(Operation operation, double amount) {
        return create(operation, amount, null);
    }

    ////////////////////////////////////////////////////////////

    /**
     * Full constructor, specifying name
     * and UUID as well.
     *
     * @param name      The name.
     * @param uuid      The unique ID.
     * @param operation The operation.
     * @param amount    The amount/magnitude of the operation.
     * @param slot      The slot to apply it to.
     */
    public AttributeModifier { }

    /**
     * Save the properties set to the given tag.
     *
     * @param tag The compound tag to save to.
     */
    public void save(CompoundTag tag) {
        tag.putString("Name", name);
        if (slot != null)
            tag.putString("Slot", slot.getName());
        tag.putInt("Operation", operation.getTagValue());
        tag.putDouble("Amount", amount);
        tag.putIntArray("UUID", UUIDUtil.uuidToIntArray(uuid));
    }

}
