package com.ddm.app.singletons;

import com.ddm.app.configuration.InputConfiguration;

public class InputConfigurationSingleton {

    private static InputConfiguration singleton = new InputConfiguration();

    public static InputConfiguration get() {
        return singleton;
    }

    public static void set(InputConfiguration instance) {
        singleton = instance;
    }
}
