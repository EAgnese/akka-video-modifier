package com.ddm.app.profiling;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.ddm.app.serialization.AkkaSerializable;
import com.ddm.app.utils.PythonScriptRunner;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class InputReader extends AbstractBehavior<InputReader.Message> {

    ////////////////////
    // Actor Messages //
    ////////////////////

    public interface Message extends AkkaSerializable {
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReadVideoMessage implements Message {
        private static final long serialVersionUID = -7915854043207237318L;
        ActorRef<VideoSequencer.Message> replyTo;
    }

    ////////////////////////
    // Actor Construction //
    ////////////////////////

    public static final String DEFAULT_NAME = "inputReader";

    public static Behavior<Message> create(final int id, final File inputfile) {
        return Behaviors.setup(context -> new InputReader(context, id, inputfile));
    }

    private InputReader(ActorContext<Message> context,final int id, final File inputFile) throws IOException {
        super(context);
        this.id = id;

        this.getContext().getLog().info("Creation of inputReader "+id);

        String[] cmd = {"python3", "python/video_images_extraction.py", "-p", inputFile.getPath(), "-x", "data/images"};
        this.getContext().getLog().info(inputFile.getPath());
        //String[] cmd = {"pwd"};

        for (String line : PythonScriptRunner.run(cmd)){
            this.getContext().getLog().info(line);
        }

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
                .onMessage(ReadVideoMessage.class, this::handle)
                .onSignal(PostStop.class, this::handle)
                .build();
    }

    private Behavior<Message> handle(ReadVideoMessage message) throws IOException {

        File[] files = new File("/data/images/video" + this.id).listFiles();

        if (files == null){
            throw new IOException("no images found");
        }

        for (File file : files){
            byte[] content = Files.readAllBytes(file.toPath());
            message.getReplyTo().tell(new VideoSequencer.ImageMessage(this.id, content));
        }
        return this;
    }

    private Behavior<Message> handle(PostStop signal) throws IOException {
        return this;
    }
}