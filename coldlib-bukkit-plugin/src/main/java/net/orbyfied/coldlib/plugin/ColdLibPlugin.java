package net.orbyfied.coldlib.plugin;

import net.orbyfied.coldlib.ColdLibProvider;
import org.bukkit.plugin.java.JavaPlugin;

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

}
