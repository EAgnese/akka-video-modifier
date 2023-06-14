package com.ddm.app;

import akka.actor.typed.ActorSystem;
import com.ddm.app.actors.Guardian;
import com.ddm.app.configuration.Command;
import com.ddm.app.configuration.SystemConfiguration;
import com.ddm.app.singletons.SystemConfigurationSingleton;

public class App 
{
    public static void main( String[] args )
    {
        Command.applyOn(args);

        SystemConfiguration config = SystemConfigurationSingleton.get();

        final ActorSystem<Guardian.Message> guardian = ActorSystem.create(Guardian.create(), config.getActorSystemName(), config.toAkkaConfig());

        if (config.getRole().equals(SystemConfiguration.MASTER_ROLE)) {
            guardian.tell(new Guardian.StartMessage());
        }
    }
}

