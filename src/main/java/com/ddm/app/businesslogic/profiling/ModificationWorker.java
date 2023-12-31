package com.ddm.app.businesslogic.profiling;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.receptionist.Receptionist;
import com.ddm.app.businesslogic.Result;
import com.ddm.app.businesslogic.Task;
import com.ddm.app.businesslogic.actors.patterns.LargeMessageProxy;
import com.ddm.app.businesslogic.singletons.SystemConfigurationSingleton;
import com.ddm.app.businesslogic.serialization.AkkaSerializable;
import com.ddm.app.businesslogic.utils.PythonScriptRunner;
import com.ddm.app.businesslogic.utils.PythonScripts;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;

public class ModificationWorker extends AbstractBehavior<ModificationWorker.Message> {

    ////////////////////
    // Actor Messages //
    ////////////////////

    public interface Message extends AkkaSerializable {
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReceptionistListingMessage implements Message {
        private static final long serialVersionUID = -5246338806092216222L;
        Receptionist.Listing listing;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskMessage implements Message {
        private static final long serialVersionUID = -4667745204456518160L;
        ActorRef<LargeMessageProxy.Message> videoSequencerLargeMessageProxy;
        Task task;
    }

    @NoArgsConstructor
    public static class ShutdownMessage implements Message {
        private static final long serialVersionUID = 2352310112323L;
    }

    ////////////////////////
    // Actor Construction //
    ////////////////////////

    public static final String DEFAULT_NAME = "modificationWorker";

    public static Behavior<Message> create() {
        return Behaviors.setup(ModificationWorker::new);
    }

    private ModificationWorker(ActorContext<Message> context) {
        super(context);

        final ActorRef<Receptionist.Listing> listingResponseAdapter = context.messageAdapter(Receptionist.Listing.class, ReceptionistListingMessage::new);
        context.getSystem().receptionist().tell(Receptionist.subscribe(VideoSequencer.videoSequencerService, listingResponseAdapter));

        this.largeMessageProxy = this.getContext().spawn(LargeMessageProxy.create(this.getContext().getSelf().unsafeUpcast()), LargeMessageProxy.DEFAULT_NAME);
    }

    /////////////////
    // Actor State //
    /////////////////

    private final ActorRef<LargeMessageProxy.Message> largeMessageProxy;

//    private boolean haveToShutDown = false;

    ////////////////////
    // Actor Behavior //
    ////////////////////

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
                .onMessage(ReceptionistListingMessage.class, this::handle)
                .onMessage(TaskMessage.class, this::handle)
                .onMessage(ShutdownMessage.class, this::handle)
                .build();
    }

    private Behavior<Message> handle(ReceptionistListingMessage message) {
        Set<ActorRef<VideoSequencer.Message>> videoSequencers = message.getListing().getServiceInstances(VideoSequencer.videoSequencerService);
        for (ActorRef<VideoSequencer.Message> videoSequencer : videoSequencers)
            videoSequencer.tell(new VideoSequencer.RegistrationMessage(this.getContext().getSelf()));
        return this;
    }

    private Behavior<Message> handle(TaskMessage message) {


        Task task = message.getTask();
        String imgName = task.getImgName();

        String pythoncommand = SystemConfigurationSingleton.get().getPythoncommand();

        //save the image we get through the Task
        try (FileOutputStream outputStream = new FileOutputStream(imgName)){
            outputStream.write(task.getImg());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //If the video has to be cartoonified, launch the cartoon script
        if (task.isCartoon()){
            String[] cmdCartoon = {pythoncommand, PythonScripts.CARTOON.label, "-p", imgName};

            for (String line : PythonScriptRunner.run(cmdCartoon)){
                this.getContext().getLog().info(line);
            }
        }

        if (!task.getColors().isEmpty()) {
            String[] cmdOneColorArray = {
                    pythoncommand, PythonScripts.BLACK_AND_WHITE_COLORED.label,
                    "-p", imgName,
                    "-c",
            };

            String[] cmdOneColor = Arrays.copyOf(cmdOneColorArray, cmdOneColorArray.length + task.getColors().size());

            for (int i = 0; i < task.getColors().size(); i++) {
                cmdOneColor[cmdOneColorArray.length + i] = task.getColors().get(i);
            }

            for (String line : PythonScriptRunner.run(cmdOneColor)){
                this.getContext().getLog().info(line);
            }
        }

        if(!task.getSubtitles().isEmpty()) {
            String[] cmdSubtitles = {pythoncommand, PythonScripts.SUBTITLES.label, "-p", imgName, "-s", task.getSubtitles()};
            //String[] cmd = {"pwd"};

            for (String line : PythonScriptRunner.run(cmdSubtitles)){
                this.getContext().getLog().info(line);
            }
        }
        //Script for the subtitles

        Result result;

        try {
            byte[] content = Files.readAllBytes(Paths.get(imgName));
            result = new Result(content,imgName,message.getTask().getVideoId(), message.getTask().getVideoName());
        } catch (IOException e) {
            this.getContext().getLog().error(e.getMessage());
            byte[] content = {};
            result = new Result(content,imgName,message.getTask().getVideoId(), message.getTask().getVideoName());
        }

        // TODO : handle correctly a fail in this worker

        LargeMessageProxy.LargeMessage completionMessage = new VideoSequencer.CompletionMessage(this.getContext().getSelf(), result);
        this.largeMessageProxy.tell(new LargeMessageProxy.SendMessage(completionMessage, message.getVideoSequencerLargeMessageProxy()));

        File img = new File(imgName);
        if(!img.delete()){
            this.getContext().getLog().error("Unable to delete : " + imgName);
        }


        return this;
    }

    private Behavior<Message> handle(ShutdownMessage message) {
        return Behaviors.stopped();
    }

}
