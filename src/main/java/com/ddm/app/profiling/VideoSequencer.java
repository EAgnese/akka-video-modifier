package com.ddm.app.profiling;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import com.ddm.app.Result;
import com.ddm.app.Task;
import com.ddm.app.actors.patterns.LargeMessageProxy;
import com.ddm.app.serialization.AkkaSerializable;
import com.ddm.app.singletons.InputConfigurationSingleton;
import com.ddm.app.utils.PythonScriptRunner;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class VideoSequencer extends AbstractBehavior<VideoSequencer.Message> {

    ////////////////////
    // Actor Messages //
    ////////////////////

    public interface Message extends AkkaSerializable, LargeMessageProxy.LargeMessage {
    }

    @NoArgsConstructor
    public static class StartMessage implements Message {
        private static final long serialVersionUID = -1963913294517850454L;
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageMessage implements Message {
        private static final long serialVersionUID = 4591192372652568030L;
        int id;
        byte[] image;
        String name;
        String subtitles;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegistrationMessage implements Message {
        private static final long serialVersionUID = -4025238529984914107L;
        ActorRef<ModificationWorker.Message> ModificationWorker;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompletionMessage implements Message {
        private static final long serialVersionUID = -7642425159675583598L;
        ActorRef<ModificationWorker.Message> ModificationWorker;
        Result result;
    }

    ////////////////////////
    // Actor Construction //
    ////////////////////////

    public static final String DEFAULT_NAME = "videoSequencer";

    public static final ServiceKey<Message> videoSequencerService = ServiceKey.create(VideoSequencer.Message.class, DEFAULT_NAME + "Service");

    public static Behavior<Message> create() {
        return Behaviors.setup(VideoSequencer::new);
    }

    private VideoSequencer(ActorContext<Message> context) {
        super(context);
        File[] inputFiles = InputConfigurationSingleton.get().getInputFiles();


        this.nbrImages = new ArrayList<>(inputFiles.length);
        this.modifiedImages = new ArrayList<>(inputFiles.length);
        this.inputReaders = new ArrayList<>(inputFiles.length);

        for (int id = 0; id < inputFiles.length; id++){
            this.inputReaders.add(context.spawn(InputReader.create(id, inputFiles[id]), InputReader.DEFAULT_NAME + "_" + id));
            this.nbrImages.add(0);
            this.modifiedImages.add(0);
        }
        this.resultCollector = context.spawn(ResultCollector.create(), ResultCollector.DEFAULT_NAME);
        this.largeMessageProxy = this.getContext().spawn(LargeMessageProxy.create(this.getContext().getSelf().unsafeUpcast()), LargeMessageProxy.DEFAULT_NAME);

        this.modificationWorkers = new ArrayList<>();

        context.getSystem().receptionist().tell(Receptionist.register(videoSequencerService, context.getSelf()));
    }

    /////////////////
    // Actor State //
    /////////////////

    private long startTime;
    private final List<ActorRef<InputReader.Message>> inputReaders;
    private final List<ActorRef<ModificationWorker.Message>> modificationWorkers;
    private final ActorRef<ResultCollector.Message> resultCollector;
    private final ActorRef<LargeMessageProxy.Message> largeMessageProxy;

    private ArrayList<Integer> nbrImages;

    private ArrayList<Integer> modifiedImages;

    private final Queue<Task> unassignedTasks = new LinkedList<>();
    private final Queue<ActorRef<ModificationWorker.Message>> idleWorkers = new LinkedList<>();
    private final Map<ActorRef<ModificationWorker.Message>, Task> busyWorkers = new HashMap<>();


    ////////////////////
    // Actor Behavior //
    ////////////////////

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
                .onMessage(StartMessage.class, this::handle)
                .onMessage(ImageMessage.class, this::handle)
                .onMessage(RegistrationMessage.class, this::handle)
                .onMessage(CompletionMessage.class, this::handle)
                .onSignal(Terminated.class, this::handle)
                .build();
    }

    private Behavior<Message> handle(StartMessage message) {
        for (ActorRef<InputReader.Message> inputReader : this.inputReaders)
            inputReader.tell(new InputReader.ReadVideoMessage(this.getContext().getSelf()));

        this.startTime = System.currentTimeMillis();
        return this;
    }

    private Behavior<Message> handle(ImageMessage message) {

        this.nbrImages.set(message.getId(), this.nbrImages.get(message.getId()) + 1)  ;
        this.getContext().getLog().info(String.valueOf(this.nbrImages.get(message.getId())));

        Task task = new Task(message.getImage(), message.getName(), message.getSubtitles(), false, message.getId());

        if (!this.idleWorkers.isEmpty()){
            ActorRef<ModificationWorker.Message> newModificationWorker = this.idleWorkers.remove();
            this.busyWorkers.put(newModificationWorker, task);
            newModificationWorker.tell(new ModificationWorker.TaskMessage(this.largeMessageProxy, task));
        }else {

            this.unassignedTasks.add(task);
        }

        return this;
    }

    private Behavior<Message> handle(RegistrationMessage message) {
        ActorRef<ModificationWorker.Message> modificationWorker = message.getModificationWorker();

        if (this.busyWorkers.containsKey(modificationWorker) || this.idleWorkers.contains(modificationWorker))
            return this;

        this.getContext().watch(modificationWorker);
        this.modificationWorkers.add(modificationWorker);
        this.getContext().getLog().info("Registration of worker {}", this.modificationWorkers.size()-1);

        if (this.unassignedTasks.isEmpty()) {
            this.idleWorkers.add(modificationWorker);
            return this;
        }

        Task task = this.unassignedTasks.remove();
        this.busyWorkers.put(modificationWorker, task);
        modificationWorker.tell(new ModificationWorker.TaskMessage(this.largeMessageProxy, task));
        return this;
    }

    private Behavior<Message> handle(CompletionMessage message) {
        ActorRef<ModificationWorker.Message> modificationWorker = message.getModificationWorker();
        Result result = message.getResult();

        File file = new File("result/" + result.getVideoId() + "/images/" + result.getImgName());

        File parentDirectory = file.getParentFile();
        if (parentDirectory != null) {
            parentDirectory.mkdirs(); // Créer les répertoires parents si nécessaire
        }


        // Save the image we get through the result
        try (FileOutputStream outputStream = new FileOutputStream(file)){
            outputStream.write(result.getImg());
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.modifiedImages.set(result.getVideoId(), this.modifiedImages.get(result.getVideoId()) + 1);
        this.getContext().getLog().info(String.valueOf(this.modifiedImages.get(result.getVideoId())));

        if (Objects.equals(this.modifiedImages.get(result.getVideoId()), this.nbrImages.get(result.getVideoId()))) {

            String resultFolder = "result/" + result.getVideoId();

            String[] cmdCartoon = {"python3", "python/video_export.py",
                                    "-f", resultFolder + "/images",
                                    "-a", resultFolder + "/audio/audio_SWMG.mp4.wav",
                                    "-x", resultFolder + "/videos"};

            for (String line : PythonScriptRunner.run(cmdCartoon)){
                this.getContext().getLog().info(line);
            }
            if(this.isAllVideoExported()) {
                this.end();
            }
        }

        if (this.unassignedTasks.isEmpty()){
            this.idleWorkers.add(modificationWorker);
            return this;
        }
        // I still don't know what task the worker could help me to solve ... but let me keep her busy.

        Task task = this.unassignedTasks.remove();
        this.busyWorkers.put(modificationWorker, task);
        modificationWorker.tell(new ModificationWorker.TaskMessage(this.largeMessageProxy, task));
        return this;
    }

    private boolean isAllVideoExported() {
        int i = 0;
        while (i < this.modifiedImages.size()) {
            if (!Objects.equals(this.modifiedImages.get(i), this.nbrImages.get(i))) {
                return false;
            }
            i++;
        }
        return true;
    }

    private void end() {
        this.resultCollector.tell(new ResultCollector.FinalizeMessage());
        long discoveryTime = System.currentTimeMillis() - this.startTime;
        this.getContext().getLog().info("Finished mining within {} ms!", discoveryTime);
    }

    private Behavior<Message> handle(Terminated signal) {
        ActorRef<ModificationWorker.Message> modificationWorker = signal.getRef().unsafeUpcast();

        if (idleWorkers.remove(modificationWorker))
            return this;

        Task task = this.busyWorkers.remove(modificationWorker);

        if(this.idleWorkers.isEmpty()){
            this.unassignedTasks.add(task);
            return this;
        }

        ActorRef<ModificationWorker.Message> newModificationWorker = this.idleWorkers.remove();
        this.busyWorkers.put(newModificationWorker, task);
        newModificationWorker.tell(new ModificationWorker.TaskMessage(this.largeMessageProxy, task));
        return this;
    }
}