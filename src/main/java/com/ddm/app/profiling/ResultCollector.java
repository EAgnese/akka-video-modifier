package com.ddm.app.profiling;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.ddm.app.actors.Guardian;
import com.ddm.app.serialization.AkkaSerializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;

public class ResultCollector extends AbstractBehavior<ResultCollector.Message> {

    ////////////////////
    // Actor Messages //
    ////////////////////

    public interface Message extends AkkaSerializable {
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultMessage implements Message {
        private static final long serialVersionUID = -7070569202900845736L;
        //TODO : store an image
        String name;
    }

    @NoArgsConstructor
    public static class FinalizeMessage implements Message {
        private static final long serialVersionUID = -6603856949941810321L;
    }

    ////////////////////////
    // Actor Construction //
    ////////////////////////

    public static final String DEFAULT_NAME = "resultCollector";

    public static Behavior<Message> create() {
        return Behaviors.setup(ResultCollector::new);
    }

    private ResultCollector(ActorContext<Message> context) throws IOException {
        super(context);
    }

    /////////////////
    // Actor State //
    /////////////////


    ////////////////////
    // Actor Behavior //
    ////////////////////

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
                .onMessage(ResultMessage.class, this::handle)
                .onMessage(FinalizeMessage.class, this::handle)
                .onSignal(PostStop.class, this::handle)
                .build();
    }

    private Behavior<Message> handle(ResultMessage message) throws IOException {
        //TODO : save the image somewhere
        return this;
    }

    private Behavior<Message> handle(FinalizeMessage message) throws IOException {
        this.getContext().getLog().info("Received FinalizeMessage!");

        this.getContext().getSystem().unsafeUpcast().tell(new Guardian.ShutdownMessage());
        return this;
    }

    private Behavior<Message> handle(PostStop signal) throws IOException {
        //TODO : close safely all the things that was opened
        return this;
    }
}
