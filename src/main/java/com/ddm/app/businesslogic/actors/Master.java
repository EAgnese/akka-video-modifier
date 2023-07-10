package com.ddm.app.businesslogic.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.DispatcherSelector;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.ddm.app.businesslogic.actors.patterns.Reaper;
import com.ddm.app.businesslogic.profiling.VideoSequencer;
import com.ddm.app.businesslogic.serialization.AkkaSerializable;
import lombok.NoArgsConstructor;

public class Master extends AbstractBehavior<Master.Message> {

    ////////////////////
    // Actor Messages //
    ////////////////////

    public interface Message extends AkkaSerializable {
    }

    @NoArgsConstructor
    public static class StartMessage implements Message {
        private static final long serialVersionUID = -1963913294517850454L;
    }

    @NoArgsConstructor
    public static class ShutdownMessage implements Message {
        private static final long serialVersionUID = 7516129288777469221L;
    }

    ////////////////////////
    // Actor Construction //
    ////////////////////////

    public static final String DEFAULT_NAME = "master";

    public static Behavior<Message> create() {
        return Behaviors.setup(Master::new);
    }

    private Master(ActorContext<Message> context) {
        super(context);
        Reaper.watchWithDefaultReaper(this.getContext().getSelf());

        this.videoSequencer = context.spawn(VideoSequencer.create(), VideoSequencer.DEFAULT_NAME, DispatcherSelector.fromConfig("akka.master-pinned-dispatcher"));
    }

    /////////////////
    // Actor State //
    /////////////////

    private final ActorRef<VideoSequencer.Message> videoSequencer;

    ////////////////////
    // Actor Behavior //
    ////////////////////

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
                .onMessage(StartMessage.class, this::handle)
                .onMessage(ShutdownMessage.class, this::handle)
                .build();
    }

    private Behavior<Message> handle(StartMessage message) {
        this.videoSequencer.tell(new VideoSequencer.StartMessage());
        return this;
    }

    private Behavior<Message> handle(ShutdownMessage message) {
        // If we expect the system to still be active when the a ShutdownMessage is issued,
        // we should propagate this ShutdownMessage to all active child actors so that they
        // can end their protocols in a clean way. Simply stopping this actor also stops all
        // child actors, but in a hard way!
        return Behaviors.stopped();
    }
}
