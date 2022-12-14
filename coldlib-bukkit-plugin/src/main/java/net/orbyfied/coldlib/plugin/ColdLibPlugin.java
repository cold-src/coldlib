package net.orbyfied.coldlib.plugin;

import net.orbyfied.coldlib.ColdLib;
import net.orbyfied.coldlib.ColdLibProvider;
import net.orbyfied.coldlib.ColdLibService;
import org.bukkit.plugin.java.JavaPlugin;

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
        // create library provider
        provider = ColdLibProvider.setInstance(new BukkitColdLibProvider(this));
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
