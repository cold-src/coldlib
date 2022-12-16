package test.orbyfied.coldlib;

import net.orbyfied.coldlib.bukkit.item.ItemBuilder;
import net.orbyfied.coldlib.bukkit.item.StoredEnchantmentsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    void join(PlayerJoinEvent event) {
        ItemStack stack = ItemBuilder.create(Material.ENCHANTED_BOOK, 1)
                .loreLegacy("a", "b", "c")
                .nameLegacy("Your fat mom")
                .addEnchantment("minecraft:sharpness", 5)
                .<StoredEnchantmentsBuilder<?>>use(StoredEnchantmentsBuilder.class, b -> {
                    b.addStoredEnchantment("minecraft:unbreaking", 3);
                })
                .build();

        event.getPlayer().getInventory().addItem(stack);
    }

}
