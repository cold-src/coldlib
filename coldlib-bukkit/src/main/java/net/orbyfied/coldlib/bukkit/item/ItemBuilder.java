package net.orbyfied.coldlib.bukkit.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.orbyfied.coldlib.bukkit.nms.LegacyNmsHelper;
import net.orbyfied.coldlib.util.Container;
import net.orbyfied.coldlib.util.Self;
import net.orbyfied.j8.util.reflect.Reflector;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Consumer;

/**
 * A builder for creating {@link ItemStack}s with
 * minimal code/work.
 *
 * @param <S> The type of self.
 */
@SuppressWarnings("rawtypes")
public class ItemBuilder<S extends ItemBuilder> implements Container<ItemStack>, Self<S>,
        BasicMetaBuilder<S>
{

    /**
     * Deep clones the provided item stack.
     *
     * @param in The stack to clone.
     * @return The cloned stack.
     */
    public static ItemStack cloneItemStack(ItemStack in) {
        // create new output stack
        // with correct item type and count
        ItemStack out = new ItemStack(
                in.getItem(),
                in.getCount()
        );

        // set pop time
        out.setPopTime(in.getPopTime());
        // clone NBT tag
        CompoundTag tag = in.getTag();
        if (tag != null) {
            out.setTag(tag.copy());
        }

        // return
        return out;
    }

    /* Constructors */

    /**
     * Wrap the provided stack for modification,
     * for clarification: This will mutate the given
     * stack. If you don't want that, make sure to clone
     * it first using {@link } or use {@link }
     *
     * @param stack The item stack to wrap.
     * @return The item builder.
     */
    public static ItemBuilder of(ItemStack stack) {
        return new ItemBuilder<>(stack);
    }

    /**
     * Create a new item builder to modify a new item
     * with the specified item and amount.
     *
     * @param item The item type.
     * @param amount The stack count.
     * @return The builder.
     */
    @SuppressWarnings("unchecked")
    public static <S extends ItemBuilder<S>> S create(Item item, int amount) {
        return (S) new ItemBuilder<S>(new ItemStack(item, amount));
    }

    /**
     * Create a new item builder to modify a new item
     * with the specified item and amount.
     *
     * @param material The item type.
     * @param amount The stack count.
     * @return The builder.
     */
    @SuppressWarnings("unchecked")
    public static <S extends ItemBuilder<S>> S create(Material material, int amount) {
        return (S) create(LegacyNmsHelper.getNMSMaterial(material), amount);
    }

    //////////////////////////////////////////////

    /** The reflector utility. */
    protected static final Reflector REFLECTOR = new Reflector("ItemBuilder");

    /**
     * Reflected (Method):
     * {@link Container#get()}
     */
    protected static final Method METHOD_Container_get =
            REFLECTOR.reflectDeclaredMethod(Container.class, "get", new Class<?>[0]);

    // internal constructor
    // just mirrors an already existent stack
    ItemBuilder(ItemStack nmsStack) {
        this.nmsStack = nmsStack;
    }

    /**
     * The Minecraft {@link ItemStack}.
     * This is the instance that will be
     * modified by the builder.
     */
    ItemStack nmsStack;

    /**
     * Get an adapter of type {@code A} mirroring
     * and modifying this instance.
     *
     * @param aClass The adapter class.
     * @param <A> The adapter type.
     * @return The adapter.
     */
    @SuppressWarnings("all")
    public <A extends Container<ItemStack>> A use(Class<? super A> aClass) {
        try {
            // create proxy
            A instance = (A) Proxy.newProxyInstance(aClass.getClassLoader(),
                    new Class[]{ aClass }, ((proxy, method, args) -> {
                        // check if method equals Container#get
                        if (method.equals(METHOD_Container_get)) {
                            // return this instance's get result
                            return ItemBuilder.this.get();
                        }

                        if (method.isDefault()) {
                            // invoke method normally
                            return InvocationHandler.invokeDefault(
                                    proxy,
                                    method,
                                    args
                            );
                        } else {
                            // unknown method
                            return null;
                        }
                    }));

            // return proxy instance
            return instance;
        } catch (Exception e) {
            // rethrow error
            throw new RuntimeException(e);
        }
    }

    /**
     * Get an adapter of type {@code A} mirroring
     * and modifying this instance.
     *
     * @param aClass The adapter class.
     * @param consumer The consumer to use the instance.
     * @param <A> The adapter type.
     * @return The adapter.
     */
    @SuppressWarnings("all")
    public <A extends Container<ItemStack>> S use(Class<? super A> aClass,
                                                  Consumer<A> consumer) {
        // create instance
        A instance = use(aClass);

        // consume instance
        if (consumer != null)
            consumer.accept(instance);

        // return this
        return self();
    }

    /**
     * Get or create the compound tag of
     * the item.
     *
     * @return The compound tag.
     */
    public CompoundTag tag() {
        return nmsStack.getOrCreateTag();
    }

    /**
     * Gets the NMS item stack and mirrors
     * it to a {@link org.bukkit.inventory.ItemStack}
     * to be used in Bukkit API code.
     *
     * @return The Bukkit stack.
     */
    public org.bukkit.inventory.ItemStack build() {
        return LegacyNmsHelper.getBukkitItemMirror(nmsStack);
    }

    /**
     * Get the NMS item stack with all
     * properties applied.
     *
     * @return The item stack.
     */
    @Override
    public ItemStack get() {
        return nmsStack;
    }

    /**
     * Clones this item builder by cloning
     * the item stack it wraps and initializing
     * a new instance with it.
     *
     * @return The new instance.
     */
    @Override
    @SuppressWarnings("all")
    public ItemBuilder clone() {
        return new ItemBuilder(cloneItemStack(nmsStack));
    }

    /* Container Implementation */

    @Override
    public boolean isSet() {
        return true;
    }

    @Override
    public Container<ItemStack> set(ItemStack val) {
        return new ItemBuilder<>(val);
    }

    @Override
    public Mutability mutability() {
        return Mutability.FORK;
    }

}
