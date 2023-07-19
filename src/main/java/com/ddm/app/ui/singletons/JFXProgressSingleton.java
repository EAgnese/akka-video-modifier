package com.ddm.app.ui.singletons;

import com.ddm.app.ui.interfaces.JFXProgress;

public class JFXProgressSingleton {

    private static JFXProgress singleton = new JFXProgress();

    public static JFXProgress get() {
        return singleton;
    }

    public static void set(JFXProgress instance) {
        singleton = instance;
    }
}

