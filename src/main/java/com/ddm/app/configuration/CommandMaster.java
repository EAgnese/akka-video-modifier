package com.ddm.app.configuration;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.ddm.app.singletons.InputConfigurationSingleton;


@Parameters(commandDescription = "Start a master ActorSystem.")
public class CommandMaster extends Command {

    @Override
    int getDefaultPort() {
        return SystemConfiguration.DEFAULT_MASTER_PORT;
    }
    @Parameter(names = {"-ip", "--inputPath"}, description = "Input path for the input data; all files in this folder are considered", required = false, arity = 1)
    String inputPath = InputConfigurationSingleton.get().getInputPath();

}
