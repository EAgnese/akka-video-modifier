package com.ddm.app.configuration;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.ddm.app.singletons.InputConfigurationSingleton;
import com.ddm.app.singletons.SystemConfigurationSingleton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Command {

    abstract int getDefaultPort();

    @Parameter(names = {"-h", "--host"}, description = "This machine's host name or IP that we use to bind this application against", required = false)
    String host = SystemConfigurationSingleton.get().getHost();

    @Parameter(names = {"-p", "--port"}, description = "This machines port that we use to bind this application against", required = false)
    int port = this.getDefaultPort();

    @Parameter(names = {"-w", "--numWorkers"}, description = "The number of workers (indexers/validators) to start locally; should be at least one if the algorithm is started standalone (otherwise there are no workers to run the discovery)", required = false)
    int numWorkers = SystemConfigurationSingleton.get().getNumWorkers();

    @Parameter(names = {"-pc", "--pythoncommand"}, description = "Python command used to launch python script", required = false)
    String pythoncommand = SystemConfiguration.DEFAULT_PYTHON_COMMAND;

    public static void applyOn(String[] args) {
        CommandMaster commandMaster = new CommandMaster();
        CommandWorker commandWorker = new CommandWorker();
        JCommander jCommander = JCommander.newBuilder()
                .addCommand(SystemConfiguration.MASTER_ROLE, commandMaster)
                .addCommand(SystemConfiguration.WORKER_ROLE, commandWorker)
                .build();

        try {
            jCommander.parse(args);

            if (jCommander.getParsedCommand() == null)
                throw new ParameterException("No command given.");

            switch (jCommander.getParsedCommand()) {
                case SystemConfiguration.MASTER_ROLE:
                    SystemConfigurationSingleton.get().update(commandMaster);
                    InputConfigurationSingleton.get().update(commandMaster);
                    String color = InputConfigurationSingleton.get().getColor();

                    List<String> colors = new ArrayList<>(Arrays.asList("RED", "GREEN", "BLUE"));

                    if(color != null && !colors.contains(color)) {
                        throw new ParameterException("the color given '" +color+ "' is not in " + colors);
                    }
                    break;
                case SystemConfiguration.WORKER_ROLE:
                    SystemConfigurationSingleton.get().update(commandWorker);
                    InputConfigurationSingleton.set(null);
                    break;
                default:
                    throw new AssertionError();
            }
        } catch (ParameterException e) {
            System.out.printf("Could not parse args: %s\n", e.getMessage());
            jCommander.usage();
            System.exit(1);
        }
    }
}
