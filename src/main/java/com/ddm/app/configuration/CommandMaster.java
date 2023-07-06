package com.ddm.app.configuration;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.ddm.app.singletons.InputConfigurationSingleton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Parameters(commandDescription = "Start a master ActorSystem.")
public class CommandMaster extends Command {

    @Override
    int getDefaultPort() {
        return SystemConfiguration.DEFAULT_MASTER_PORT;
    }
    @Parameter(names = {"-ip", "--inputPath"}, description = "Input path for the input data; all files in this folder are considered", arity = 1)
    String inputPath = InputConfigurationSingleton.get().getInputPath();

    @Parameter(names = {"-c", "--cartoon"}, description = "Enable the videos\' modification into a cartoon", arity = 0)
    boolean cartoon;

    @Parameter(names = {"-o", "--one-color"}, description = "Enable the one color effect between [RED, GREEN, BLUE]", variableArity=true)
    List<String> colors = new ArrayList<>();


}
