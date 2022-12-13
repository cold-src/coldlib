package net.orbyfied.coldlib;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * The library instance.
 */
public class ColdLib {

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
            Map<String, ColdLibService>
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
     * Get a registered service.
     *
     * @param sClass The service class.
     * @param instanceName The name (optional).
     * @param <S> The service type.
     * @return The service or null if absent.
     */
    @SuppressWarnings("unchecked")
    public <S extends ColdLibService> S getService(Class<S> sClass, String instanceName) {
        Objects.requireNonNull(sClass, "Service class can not be null");
        Map<String, ColdLibService> map = serviceMap.get(sClass);
        if (map == null)
            return null;
        return (S) map.get(instanceName);
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
    public <S extends ColdLibService> S withService(S service) {
        Objects.requireNonNull(service, "Service can not be null");
        serviceMap.computeIfAbsent(service.getClass(), __ -> new HashMap<>())
                .put(service.getInstanceName(), service);
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
