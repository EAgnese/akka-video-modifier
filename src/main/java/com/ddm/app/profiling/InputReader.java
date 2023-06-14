package com.ddm.app.profiling;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.ddm.app.serialization.AkkaSerializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InputReader extends AbstractBehavior<InputReader.Message> {

    ////////////////////
    // Actor Messages //
    ////////////////////

    public interface Message extends AkkaSerializable {
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReadHeaderMessage implements Message {
        private static final long serialVersionUID = 1729062814525657711L;
        ActorRef<VideoSequencer.Message> replyTo;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReadBatchMessage implements Message {
        private static final long serialVersionUID = -7915854043207237318L;
        ActorRef<VideoSequencer.Message> replyTo;
    }

    ////////////////////////
    // Actor Construction //
    ////////////////////////

    public static final String DEFAULT_NAME = "inputReader";

    public static Behavior<Message> create(final int id, final File inputFile) {
        return Behaviors.setup(context -> new InputReader(context, id, inputFile));
    }

    private InputReader(ActorContext<Message> context,final int id, final File inputFile /*or by path ?*/) throws IOException {
        //TODO : do something with the video
        super(context);
        this.id = id;
    }

    /////////////////
    // Actor State //
    /////////////////

    private final int id;

    ////////////////////
    // Actor Behavior //
    ////////////////////

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
                .onMessage(ReadBatchMessage.class, this::handle)
                .onSignal(PostStop.class, this::handle)
                .build();
    }

    private Behavior<Message> handle(ReadBatchMessage message) throws IOException {
        List<String[]> batch = new ArrayList<>(10000);
//        for (int i = 0; i < this.batchSize; i++) {
//            String[] line = this.reader.readNext();
//            if (line == null)
//                break;
//            batch.add(line);
//        }

        //TODO : read the video (via python scripts)


        message.getReplyTo().tell(new VideoSequencer.BatchMessage(this.id, batch));
        return this;
    }

    private Behavior<Message> handle(PostStop signal) throws IOException {
        return this;
    }
}