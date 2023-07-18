package com.ddm.app.businesslogic.utils;

import com.ddm.app.businesslogic.singletons.InputConfigurationSingleton;

import java.util.ArrayList;
import java.util.List;


public interface ConfigMaster {

    String inputPath = InputConfigurationSingleton.get().getInputPath();
    boolean cartoon = false;
    List<String> colors = new ArrayList<>();

    String getHost();
    int getPort();
    int getNumWorkers();
    String getPythoncommand();
    String getInputPath();
    boolean isCartoon();
    List<String> getColors();

}
