package com.ddm.app.singletons;

import com.ddm.app.configuration.SystemConfiguration;

public class SystemConfigurationSingleton {

    private static SystemConfiguration singleton = new SystemConfiguration();

    public static SystemConfiguration get() {
        return singleton;
    }

    public static void set(SystemConfiguration instance) {
        singleton = instance;
    }
}
