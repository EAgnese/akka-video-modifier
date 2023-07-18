package com.ddm.app.businesslogic.configuration;

import com.ddm.app.businesslogic.utils.ConfigMaster;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.Data;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Data
public class SystemConfiguration {

    public static final String MASTER_ROLE = "master";
    public static final String WORKER_ROLE = "worker";

    public static final int DEFAULT_MASTER_PORT = 7877;
    public static final int DEFAULT_WORKER_PORT = 7879;

    public static final String DEFAULT_PYTHON_COMMAND = "python3";

    private String role = MASTER_ROLE;                 // This machine's role in the cluster.

    private String host = getDefaultHost();            // This machine's host name or IP that we use to bind this application against
    private int port = DEFAULT_MASTER_PORT;            // This machines port that we use to bind this application against

    private String masterHost = getDefaultHost();      // The host name or IP of the master; if this is a master, masterHost = host
    private int masterPort = DEFAULT_MASTER_PORT;      // The port of the master; if this is a master, masterPort = port

    private String actorSystemName = "ddm";            // The name of this application

    private int numWorkers = 4;                        // The number of workers to start locally; should be at least one if the algorithm is started standalone (otherwise there are no workers to run the application)


    private String pythoncommand = DEFAULT_PYTHON_COMMAND; // The python command used to launch python script
    private static String getDefaultHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "localhost";
        }
    }

    public void update(ConfigMaster guiMaster) {
        this.role = MASTER_ROLE;
        this.host = guiMaster.getHost();
        this.port = guiMaster.getPort();
        this.masterHost = guiMaster.getHost();
        this.masterPort = guiMaster.getPort();
        this.numWorkers = guiMaster.getNumWorkers();
        this.pythoncommand = guiMaster.getPythoncommand();
    }


    public void update(CommandWorker commandWorker) {
        this.role = WORKER_ROLE;
        this.host = commandWorker.host;
        this.port = commandWorker.port;
        this.masterHost = commandWorker.masterhost;
        this.masterPort = commandWorker.masterport;
        this.numWorkers = commandWorker.numWorkers;
        this.pythoncommand = commandWorker.pythoncommand;
    }


    public Config toAkkaConfig() {
        return ConfigFactory.parseString("akka.remote.artery.canonical.hostname = \"" + this.host + "\"\n" +
                        "akka.remote.artery.canonical.port = " + this.port + "\n" +
                        "akka.cluster.roles = [" + this.role + "]\n" +
                        "akka.cluster.seed-nodes = [\"akka://" + this.actorSystemName + "@" + this.masterHost + ":" + this.masterPort + "\"]")
                .withFallback(ConfigFactory.load("application"));
    }

    public Config toAkkaTestConfig() {
        return ConfigFactory.parseString("akka.remote.artery.canonical.hostname = \"" + this.host + "\"\n" +
                        "akka.remote.artery.canonical.port = " + this.port + "\n" +
                        "akka.cluster.roles = [" + this.role + "]")
                .withFallback(ConfigFactory.load("application"));
    }
}

