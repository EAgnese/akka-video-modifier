package com.ddm.app.businesslogic.configuration;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.ddm.app.businesslogic.singletons.InputConfigurationSingleton;
import com.ddm.app.businesslogic.utils.ConfigMaster;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Parameters(commandDescription = "Start a master ActorSystem.")
@Getter
public class CommandMaster extends Command implements ConfigMaster {

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


    @Override
    public String getHost() {
        return super.host;
    }

    @Override
    public int getPort() {
        return super.port;
    }

    @Override
    public int getNumWorkers() {
        return super.numWorkers;
    }

    @Override
    public String getPythoncommand() {
        return super.pythoncommand;
    }
}
