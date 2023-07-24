package com.ddm.app.businesslogic.profiling;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.ddm.app.businesslogic.Result;
import com.ddm.app.businesslogic.actors.Guardian;
import com.ddm.app.businesslogic.serialization.AkkaSerializable;
import com.ddm.app.businesslogic.singletons.InputConfigurationSingleton;
import com.ddm.app.businesslogic.singletons.SystemConfigurationSingleton;
import com.ddm.app.businesslogic.utils.PythonScriptRunner;
import com.ddm.app.businesslogic.utils.PythonScripts;
import com.ddm.app.businesslogic.utils.VideoFPSReader;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;

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
        Result result;
        String audioPath;
        ActorRef<VideoSequencer.Message> replyTo;
    }

    @NoArgsConstructor
    public static class FinalizeMessage implements Message {
        private static final long serialVersionUID = -6603856949941810321L;
    }

    ////////////////////////
    // Actor Construction //
    ////////////////////////

    public static final String DEFAULT_NAME = "resultCollector";

    public int nbrVideosExported;

    public static Behavior<Message> create() {
        return Behaviors.setup(ResultCollector::new);
    }

    private ResultCollector(ActorContext<Message> context)  {
        super(context);
        this.nbrVideosExported = 0;
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

    private Behavior<Message> handle(ResultMessage message) {

        Result result = message.getResult();
        int videoId = result.getVideoId();
        String videoName = result.getVideoName();

        this.getContext().getLog().info("Merging the video number {}", videoId);

        String resultFolder = "result/" + result.getVideoName();

        String pythoncommand = SystemConfigurationSingleton.get().getPythoncommand();

        VideoFPSReader reader = VideoFPSReader.getInstance();
        HashMap<String, Integer> videoFPSMap = reader.getVideoFPS("", "data/fps.json");
        int videoFps = videoFPSMap.get(videoName);

        String[] cmdExport = {
                pythoncommand, PythonScripts.VIDEO_EXPORT.label,
                "-f", resultFolder + "/images",
                "-a", message.getAudioPath(),
                "-x", "result/",
                "-F", Integer.toString(videoFps)
        };

        for (String line : PythonScriptRunner.run(cmdExport)){
            this.getContext().getLog().info(line);
        }
        this.nbrVideosExported++;
        if (this.nbrVideosExported == InputConfigurationSingleton.get().getInputFiles().length){
            message.getReplyTo().tell(new VideoSequencer.EndMessage());
        }
        return this;
    }

    private Behavior<Message> handle(FinalizeMessage message) {
        this.getContext().getLog().info("Received FinalizeMessage!");
        this.getContext().getSystem().unsafeUpcast().tell(new Guardian.ShutdownMessage());
        return this;
    }

    private Behavior<Message> handle(PostStop signal)  {
        //TODO : close safely all the things that was opened
        return this;
    }
}
