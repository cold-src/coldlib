package net.orbyfied.coldlib.plugin;

import net.orbyfied.coldlib.ColdLib;
import net.orbyfied.coldlib.ColdLibProvider;
import net.orbyfied.coldlib.ColdLibService;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Executable;
import java.util.Locale;

/**
 * The main ColdLib plugin file.
 * This plugin is responsible for loading
 * and bootstrapping the ColdLib libraries.
 */
public class ColdLibPlugin extends JavaPlugin {

    // provider instance
    protected BukkitColdLibProvider provider;

    @Override
    public void onLoad() {
        try {
            // create library provider
            provider = new BukkitColdLibProvider(this);
            // register provider instance
            ColdLibProvider.setInstance(provider);
        } catch (Exception e) {
            getLogger().severe("Failed to load ColdLib Bukkit");
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        // enable provider
        provider.enable();
    }

    @Override
    public void onDisable() {
        // unload provider
        provider.unload();
    }

}
