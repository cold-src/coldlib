package test.orbyfied.coldlib;

import net.orbyfied.coldlib.bukkit.item.ItemBuilder;
import net.orbyfied.coldlib.bukkit.item.StoredEnchantmentsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.swing.plaf.TextUI;

public class TestPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    void join(PlayerJoinEvent event) {
        ItemStack stack = ItemBuilder.create(Material.SHIELD, 1)
                .loreLegacy(ChatColor.RED + "Epic shield.")
                .nameLegacy(ChatColor.RED + "" + ChatColor.BOLD + "SHIELD")
                .addEnchantment("minecraft:unbreaking", 5)
                .durability(6)
                .build();

        event.getPlayer().getInventory().addItem(stack);
    }

}
