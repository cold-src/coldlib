package net.orbyfied.coldlib.plugin;

import net.orbyfied.coldlib.ColdLibProvider;
import net.orbyfied.j8.util.logging.EventLogHandler;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BukkitColdLibProvider extends ColdLibProvider {

    /**
     * Get the {@link ColdLibPlugin} from the active
     * {@link ColdLibProvider}.
     * @throws IllegalStateException If the provider is not a {@link BukkitColdLibProvider}
     * @return The plugin.
     */
    public static ColdLibPlugin getActivePlugin() {
        ColdLibProvider provider = get();
        if (!(provider instanceof BukkitColdLibProvider b))
            throw new IllegalStateException();
        return b.plugin;
    }

    //////////////////////////////////////

    // the plugin
    final ColdLibPlugin plugin;

    BukkitColdLibProvider(ColdLibPlugin plugin) {
        this.plugin = plugin;
        init();
    }

    @Override
    protected void load() {
        // call super
        super.load();

        // initialize bukkit based logger
        logGroup.withInitializer(eventLog -> {
            final Logger logger = plugin.getLogger();
            eventLog.withHandler(new EventLogHandler("logger", event -> {
                Level level = BukkitLoggerUtil.getLogLevel(event.getLevel());
                logger.log(level, Objects.toString(event.getMessage()));
                event.getErrors().forEach(Throwable::printStackTrace);
            }));
        });
    }

    @Override
    protected void enable() {
        // call super
        super.enable();
    }

    @Override
    protected void unload() {
        // call super
        super.unload();
    }

}
