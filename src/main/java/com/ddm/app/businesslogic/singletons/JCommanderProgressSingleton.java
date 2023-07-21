package com.ddm.app.businesslogic.singletons;

import com.ddm.app.businesslogic.configuration.JCommanderProgress;

public class JCommanderProgressSingleton {
    private static JCommanderProgress singleton = new JCommanderProgress();

    public static JCommanderProgress get() {
        return singleton;
    }

    public static void set(JCommanderProgress instance) {
        singleton = instance;
    }
}
