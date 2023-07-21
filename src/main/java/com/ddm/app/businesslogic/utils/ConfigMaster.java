package com.ddm.app.businesslogic.utils;

import com.ddm.app.ui.interfaces.ProgressInterface;

import java.util.List;


public interface ConfigMaster {

    boolean cartoon = false;

    String getHost();
    int getPort();
    int getNumWorkers();
    String getPythoncommand();
    String getInputPath();
    boolean isCartoon();
    List<String> getColors();

    ProgressInterface getProgress();

}
