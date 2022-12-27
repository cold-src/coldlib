package net.orbyfied.coldlib;

import net.orbyfied.coldlib.util.Container;
import net.orbyfied.coldlib.util.functional.ThrowingSupplier;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * The library instance.
 */
public class ColdLib {

    /**
     * Get the ColdLib instance from the provider.
     * @see ColdLibProvider#getLibrary()
     * @throws IllegalStateException If there is no provider set.
     * @return The ColdLib instance or null if unset.
     */
    public static ColdLib get() {
        return ColdLibProvider.getLibrary();
    }

    ////////////////////////////////////////////////////

    /**
     * The library provider.
     */
    final ColdLibProvider provider;

    // library constructor
    protected ColdLib(ColdLibProvider provider) {
        this.provider = provider;
    }

    /**
     * Get the library provider that
     * instantiated this library.
     * @return The provider instance.
     */
    public ColdLibProvider provider() {
        return provider;
    }

    /*

        Services

     */

    /**
     * The services by class and optionally name.
     */
    final Map<Class<?>,
            Map<String, Container<ColdLibService>>
            > serviceMap = new HashMap<>();

    /**
     * The services in a list.
     */
    final List<ColdLibService> services = new ArrayList<>();

    /**
     * Get the list of registered services.
     *
     * @return The immutable list of registered services.
     */
    public List<ColdLibService> getServices() {
        return Collections.unmodifiableList(services);
    }

    /**
     * Get or create a future reference to
     * the specified service instance. This
     * allows you to get, set, or await the
     * service instance in the future.
     *
     * @param sClass The service class.
     * @param instanceName The instance name (can be null).
     * @param <S> The service type.
     * @return The service reference.
     */
    @SuppressWarnings("unchecked")
    public <S extends ColdLibService> Container<S> referenceService(Class<S> sClass,
                                                                    String instanceName) {
        Objects.requireNonNull(sClass, "Service class can not be null");

        // get instance of class map
        Map<String, Container<ColdLibService>> map = serviceMap.computeIfAbsent(sClass,
                __ -> new HashMap<>());

        // get and return instance container
        return (Container<S>) map.computeIfAbsent(instanceName, __ ->
                Container.awaitable(Container.futureImmutable())
        );
    }

    /**
     * Get or create a future reference to
     * the specified service instance. This
     * allows you to get, set, or await the
     * service instance in the future.
     *
     * @param sClass The service class.
     * @param <S> The service type.
     * @return The service reference.
     *
     * @see ColdLib#referenceService(Class, String)
     * {@code instanceName} is defaulted to null.
     */
    public <S extends ColdLibService> Container<S> referenceService(Class<S> sClass) {
        return referenceService(sClass, null);
    }

    /**
     * Get a registered service.
     *
     * @param sClass The service class.
     * @param instanceName The name (optional).
     * @param <S> The service type.
     * @return The service or null if absent.
     */
    public <S extends ColdLibService> S getService(Class<S> sClass, String instanceName) {
        return referenceService(sClass, instanceName).get();
    }

    /**
     * Get a registered service by only class.
     * The {@code instanceName} argument will be set to null.
     *
     * @see ColdLib#getService(Class, String)
     * @return The service or null if absent.
     */
    public <S extends ColdLibService> S getService(Class<S> sClass) {
        return getService(sClass, null);
    }

    /**
     * Registers the provided service to
     * this ColdLib instance. It use the
     * class of the service object for the
     * service class and the instance name
     * from the service object (can be null)
     *
     * @param service The service instance.
     * @param <S> The service type.
     * @return The service (now registered).
     */
    @SuppressWarnings("unchecked")
    public <S extends ColdLibService> S withService(S service) {
        Objects.requireNonNull(service, "Service can not be null");
        this.referenceService((Class<S>) service.getClass(), service.getInstanceName())
                .set(service);
        return service;
    }

    /**
     * Creates and registers a new service instance
     * of type {@code sClass} with the provided (optional)
     * instance name.
     *
     * @param sClass The service class.
     * @param instanceName The instance name. (can be null)
     * @param <S> The service type.
     * @return The service instance (registered).
     */
    public <S extends ColdLibService> S createService(Class<S> sClass, String instanceName) {
        try {
            // get constructor
            Constructor<S> constructor = sClass.getDeclaredConstructor(
                    /* lib instance */  ColdLib.class,
                    /* instance name */ String.class
            );

            // create and register instance
            S service = constructor.newInstance(this, instanceName);
            withService(service);

            // return service
            return service;
        } catch (Exception e) {
            provider.getLog().newErr("create_service", "Failed to create service from " + sClass +
                    " with instance name '" + instanceName + "'")
                    .withError(e)
                    .push();
            return null;
        }
    }

}
