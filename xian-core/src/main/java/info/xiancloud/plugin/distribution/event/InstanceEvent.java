package info.xiancloud.plugin.distribution.event;

import info.xiancloud.plugin.distribution.service_discovery.Instance;

/**
 * @author happyyangyuan
 */
public class InstanceEvent<T> {

    private Instance<T> instance;

    public Instance<T> getInstance() {
        return instance;
    }

    public InstanceEvent<T> setInstance(Instance<T> instance) {
        this.instance = instance;
        return this;
    }
}
