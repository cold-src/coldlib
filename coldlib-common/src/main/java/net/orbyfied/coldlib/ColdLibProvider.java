package net.orbyfied.coldlib;

import net.orbyfied.coldlib.util.Container;
import net.orbyfied.j8.util.logging.EventLog;
import net.orbyfied.j8.util.logging.EventLogs;

/**
 * Responsible for loading, bootstrapping
 * and providing ColdLib to the VM.
 */
public abstract class ColdLibProvider {

    /**
     * The container for the provider instance.
     */
    private static final Container<ColdLibProvider> providerContainer =
            Container.futureImmutable();

    /**
     * Set the provider instance.
     * Will error if it has already been provided.
     * @throws UnsupportedOperationException If a provider has already been set.
     * @param provider The provider.
     */
    public static <P extends ColdLibProvider> P setInstance(P provider) {
        if (providerContainer.isSet())
            throw new UnsupportedOperationException("A provider instance is already set");
        providerContainer.set(provider);
        return provider;
    }

    /**
     * Get the library provider instance.
     * @return The provider or null if uninitialized.
     */
    public static ColdLibProvider get() {
        return providerContainer.get();
    }

    /**
     * Get the library instance.
     * @throws IllegalStateException If no ColdLib provider has been set.
     * @return The library instance.
     */
    public static ColdLib getLibrary() {
        ColdLibProvider provider = get();
        if (provider == null)
            throw new IllegalStateException("No ColdLib provider set");
        return provider.instance();
    }

    ////////////////////////////////////////////////////

    // constructor
    protected ColdLibProvider() {
        // instantiate the library
        this.lib = new ColdLib(this);

        // load the provider
        load();

        // get the event log
        log = logGroup.getOrCreate("ColdLib");
    }

    /**
     * The library instance.
     */
    protected ColdLib lib;

    /**
     * The library event log group.
     */
    protected EventLogs logGroup
            = new EventLogs();

    /**
     * The main library log.
     */
    protected EventLog log;

    /**
     * Get the library instance.
     * @return The library instance.
     */
    public ColdLib instance() {
        return lib;
    }

    /**
     * Get the main event log.
     * @return The event log.
     */
    public EventLog getLog() {
        return log;
    }

    /**
     * Get the main event log group.
     * @return The event log group.
     */
    public EventLogs getLogGroup() {
        return logGroup;
    }

    /**
     * Loads the library provider.
     */
    protected void load() { }

    /**
     * Enables the library provider.
     */
    protected void enable() { }

    /**
     * Unloads the library provider.
     */
    protected void unload() { }

}
