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
import com.ddm.app.utils.SubtitleFrameMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
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

    private final String audioPath;

    public static Behavior<Message> create(final int id, final File inputfile) {
        return Behaviors.setup(context -> new InputReader(context, id, inputfile));
    }

    private InputReader(ActorContext<Message> context, final int id, final File inputFile) {
        super(context);
        this.id = id;
        this.videoName = inputFile.getName().replace('.', '-');

        this.getContext().getLog().info("Creation of inputReader " + id);

        //Getting the video's audio
        this.audioPath = "result/" + this.videoName + "/audio";
        String[] cmdAudio = {"python3", "python/audio_extraction.py", "-p", inputFile.getPath(), "-x", this.audioPath};
        //String[] cmd = {"pwd"};

        for (String line : PythonScriptRunner.run(cmdAudio)) {
            this.getContext().getLog().info(line);
        }

        //Cut the video frame by frame
        String[] cmdImages = {"python3", "python/video_images_extraction.py", "-p", inputFile.getPath(), "-x", "data/images"};
        //String[] cmd = {"pwd"};

        for (String line : PythonScriptRunner.run(cmdImages)) {
            this.getContext().getLog().info(line);
        }

        SubtitleFrameMapper frameMapper = new SubtitleFrameMapper(30, "data/SWMG_subtitles.txt");
        this.subtitles = frameMapper.mapFramesToSubtitles();

    }

    /////////////////
    // Actor State //
    /////////////////

    private final int id;
    private final String videoName;

    private final Map<Integer, String> subtitles;


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

        this.getContext().getLog().info("Reading video " + this.id);

        message.getReplyTo().tell(new VideoSequencer.AudioMessage(this.audioPath));

        String path = "data/images/" + this.videoName + "/";

        File[] files = new File(path).listFiles();

        if (files == null) {
            throw new IOException("no images found");
        }

        for (File file : files) {
            byte[] content = Files.readAllBytes(file.toPath());
            int frameNumber = frameNumberExtraction(file.getName());
            String sub = this.subtitles.get(frameNumber) != null ? this.subtitles.get(frameNumber) : "";
            message.getReplyTo().tell(new VideoSequencer.ImageMessage(this.id, this.videoName, content, file.getName(), sub));
        }
        return this;
    }

    private Behavior<Message> handle(PostStop signal) /*throws IOException*/ {
        return this;
    }

    private int frameNumberExtraction(String fileName) {

        // Définir le modèle de l'expression régulière
        String pattern = ".*?(\\d+)\\.jpg$";

        // Créer un objet Pattern en compilant le modèle d'expression régulière
        Pattern regex = Pattern.compile(pattern);

        // Créer un objet Matcher en utilisant le modèle et le nom de fichier
        Matcher matcher = regex.matcher(fileName);

        // Vérifier si le nom de fichier correspond au modèle
        if (matcher.matches()) {
            // Extraire le numéro de frame à partir du groupe capturé
            String frameNumberStr = matcher.group(1);

            return Integer.parseInt(frameNumberStr);
        } else {
            return -1;
        }
    }
}