package com.ddm.app.singletons;

import akka.actor.typed.ActorRef;
import com.ddm.app.actors.patterns.Reaper;

public class ReaperSingleton {

    private static ActorRef<Reaper.Message> singleton;

    public static ActorRef<Reaper.Message> get() {
        return singleton;
    }

    public static void set(ActorRef<Reaper.Message> instance) {
        singleton = instance;
    }
}

