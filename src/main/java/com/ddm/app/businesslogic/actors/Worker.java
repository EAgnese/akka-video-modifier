package com.ddm.app.businesslogic.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.DispatcherSelector;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.ddm.app.businesslogic.actors.patterns.Reaper;
import com.ddm.app.businesslogic.profiling.ModificationWorker;
import com.ddm.app.businesslogic.serialization.AkkaSerializable;
import com.ddm.app.businesslogic.singletons.SystemConfigurationSingleton;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class Worker extends AbstractBehavior<Worker.Message> {

    ////////////////////
    // Actor Messages //
    ////////////////////

    public interface Message extends AkkaSerializable {
    }

    @NoArgsConstructor
    public static class ShutdownMessage implements Message {
        private static final long serialVersionUID = 7516129288777469221L;
    }

    ////////////////////////
    // Actor Construction //
    ////////////////////////

    public static final String DEFAULT_NAME = "worker";

    public static Behavior<Message> create() {
        return Behaviors.setup(Worker::new);
    }

    private Worker(ActorContext<Message> context) {
        super(context);
        Reaper.watchWithDefaultReaper(this.getContext().getSelf());

        final int numWorkers = SystemConfigurationSingleton.get().getNumWorkers();

        this.workers = new ArrayList<>(numWorkers);
        for (int id = 0; id < numWorkers; id++)
            this.workers.add(context.spawn(ModificationWorker.create(), ModificationWorker.DEFAULT_NAME + "_" + id, DispatcherSelector.fromConfig("akka.worker-pool-dispatcher")));

    }

    /////////////////
    // Actor State //
    /////////////////

    final List<ActorRef<ModificationWorker.Message>> workers;

    ////////////////////
    // Actor Behavior //
    ////////////////////

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
                .onMessage(ShutdownMessage.class, this::handle)
                .build();
    }

    private Behavior<Message> handle(ShutdownMessage message) {
        // If we expect the system to still be active when the a ShutdownMessage is issued,
        // we should propagate this ShutdownMessage to all active child actors so that they
        // can end their protocols in a clean way. Simply stopping this actor also stops all
        // child actors, but in a hard way!
        for(ActorRef<ModificationWorker.Message> worker : this.workers){
            worker.tell(new ModificationWorker.ShutdownMessage());
        }

        return Behaviors.stopped();
    }
}
