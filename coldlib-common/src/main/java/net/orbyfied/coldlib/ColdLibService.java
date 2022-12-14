package net.orbyfied.coldlib;

public abstract class ColdLibService {

    /**
     * The library instance.
     */
    protected final ColdLib lib;

    /**
     * The service instance name.
     * This can be null to make it solely
     * resolvable by class.
     */
    protected final String instanceName;

    public ColdLibService(ColdLib lib,
                          String instanceName) {
        this.lib = lib;
        this.instanceName = instanceName;
    }

    public String getInstanceName() {
        return instanceName;
    }

}
