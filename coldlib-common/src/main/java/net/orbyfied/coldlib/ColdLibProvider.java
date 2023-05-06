package net.orbyfied.coldlib;

import coldsrc.coldlib.util.Container;

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
    protected void init() {
        // instantiate the library
        this.lib = new ColdLib(this);

        // load the provider
        load();
    }

    /**
     * The library instance.
     */
    protected ColdLib lib;

    /**
     * Get the library instance.
     * @return The library instance.
     */
    public ColdLib instance() {
        return lib;
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
