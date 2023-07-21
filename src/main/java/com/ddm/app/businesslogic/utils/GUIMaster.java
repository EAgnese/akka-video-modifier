package com.ddm.app.businesslogic.utils;

import com.ddm.app.businesslogic.configuration.SystemConfiguration;
import com.ddm.app.businesslogic.singletons.InputConfigurationSingleton;
import com.ddm.app.businesslogic.singletons.SystemConfigurationSingleton;
import com.ddm.app.ui.interfaces.ProgressInterface;
import com.ddm.app.ui.singletons.JFXProgressSingleton;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class GUIMaster implements ConfigMaster {
    String host = SystemConfigurationSingleton.get().getHost();
    int port = SystemConfiguration.DEFAULT_MASTER_PORT;
    int numWorkers = SystemConfigurationSingleton.get().getNumWorkers();
    String pythoncommand = SystemConfiguration.DEFAULT_PYTHON_COMMAND;
    String inputPath = InputConfigurationSingleton.get().getInputPath();
    boolean cartoon = false;
    List<String> colors = new ArrayList<>();

    @Override
    public ProgressInterface getProgress(){
        return JFXProgressSingleton.get();
    }
}
