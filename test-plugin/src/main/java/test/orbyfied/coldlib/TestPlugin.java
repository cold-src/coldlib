package test.orbyfied.coldlib;

import net.minecraft.world.entity.EquipmentSlot;
import net.orbyfied.coldlib.bukkit.item.HideFlag;
import net.orbyfied.coldlib.bukkit.item.ItemBuilder;
import net.orbyfied.coldlib.bukkit.item.AttributeModifier;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    void join(PlayerJoinEvent event) {
        ItemStack stack = ItemBuilder.create(Material.SHIELD, 1)
                .loreLegacy(ChatColor.RED + "Epic shield.")
                .nameLegacy(ChatColor.GOLD + "" + ChatColor.BOLD + "Legendary " + ChatColor.RED + "" + ChatColor.BOLD + "SHIELD")
                .addEnchantment("minecraft:unbreaking", 5)
                .addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, AttributeModifier.create(AttributeModifier.Operation.ADD, 3, EquipmentSlot.MAINHAND))
                .unbreakable(true)
                .hideFlags(true, HideFlag.ENCHANTMENTS, HideFlag.UNBREAKABLE)
                .durability(6)
                .build()
                ;

        event.getPlayer().getInventory().addItem(stack);
    }

}
