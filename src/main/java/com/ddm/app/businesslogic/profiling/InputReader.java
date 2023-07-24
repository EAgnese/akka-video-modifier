package com.ddm.app.businesslogic.profiling;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.ddm.app.businesslogic.serialization.AkkaSerializable;
import com.ddm.app.businesslogic.singletons.SystemConfigurationSingleton;
import com.ddm.app.businesslogic.utils.PythonScriptRunner;
import com.ddm.app.businesslogic.utils.PythonScripts;
import com.ddm.app.businesslogic.utils.SubtitleFrameMapper;
import com.ddm.app.businesslogic.utils.VideoFPSReader;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    private String audioPath;

    private final int nbrImages;

    public static Behavior<Message> create(final int id, final File inputfile) {
        return Behaviors.setup(context -> new InputReader(context, id, inputfile));
    }

    private InputReader(ActorContext<Message> context, final int id, final File inputFile) {
        super(context);
        this.id = id;
        this.videoName = inputFile.getName().replace('.', '-');

        this.getContext().getLog().info("Creation of inputReader " + id);

        //Getting the video's audio
        String audioDirectory = "result/" + this.videoName + "/audio";
        String pythoncommand = SystemConfigurationSingleton.get().getPythoncommand();
        String[] cmdAudio = {pythoncommand, PythonScripts.AUDIO_EXTRACTION.label, "-p", inputFile.getPath(), "-x", audioDirectory};
        //String[] cmd = {"pwd"};

        for (String line : PythonScriptRunner.run(cmdAudio)) {
            this.getContext().getLog().info(line);
        }

        for (File audioFile : Objects.requireNonNull(new File(audioDirectory).listFiles())){
            this.audioPath = audioFile.getPath();
        }

        //Cut the video frame by frame
        String[] cmdImages = {pythoncommand, PythonScripts.IMAGES_EXTRACTION.label, "-p", inputFile.getPath(), "-x", "data/images"};

        for (String line : PythonScriptRunner.run(cmdImages)) {
            this.getContext().getLog().info(line);
        }

        // get the Images directory
        String videoImagesPath = "data/images/"+ videoName +"/";
        File imagesDir = new File(videoImagesPath);

        // get the number of files in it, so the number of images
        this.nbrImages = Objects.requireNonNull(imagesDir.listFiles()).length;

        // create Hashmap of frames
        VideoFPSReader reader = VideoFPSReader.getInstance();
        HashMap<String, Integer> videoFPSMap = reader.getVideoFPS(inputFile.getParentFile().getPath(), "data/fps.json");
        int videoFps = videoFPSMap.get(this.videoName);

        // get the subtitles txt file and create a Hashmap of the subtitles
        File subtitlesFile = new File("data/subtitles/"+ videoName +".txt");
        if(subtitlesFile.exists()) {
            SubtitleFrameMapper frameMapper = new SubtitleFrameMapper(videoFps, subtitlesFile.getPath());
            this.subtitles = frameMapper.mapFramesToSubtitles();
        }
        else {
            this.subtitles = new HashMap<>();
        }

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

        message.getReplyTo().tell(new VideoSequencer.NbrImagesMessage(this.nbrImages, this.id));
        message.getReplyTo().tell(new VideoSequencer.AudioMessage(this.audioPath, this.id));

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

        // Define the regular expression pattern
        String pattern = ".*?(\\d+)\\.jpg$";

        // Create a Pattern object by compiling the regular expression pattern
        Pattern regex = Pattern.compile(pattern);

        // Create a Matcher object using the pattern and the file name
        Matcher matcher = regex.matcher(fileName);

        // Check if the file name matches the pattern
        if (matcher.matches()) {
            // Extract the frame number from the captured group
            String frameNumberStr = matcher.group(1);

            return Integer.parseInt(frameNumberStr);
        } else {
            return -1;
        }
    }
}