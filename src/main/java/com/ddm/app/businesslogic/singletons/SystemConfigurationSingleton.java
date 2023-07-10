package com.ddm.app.businesslogic.singletons;

import com.ddm.app.businesslogic.configuration.SystemConfiguration;

public class SystemConfigurationSingleton {

    private static SystemConfiguration singleton = new SystemConfiguration();

    public static SystemConfiguration get() {
        return singleton;
    }

    public static void set(SystemConfiguration instance) {
        singleton = instance;
    }
}
