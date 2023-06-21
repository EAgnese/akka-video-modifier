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
import com.ddm.app.actors.patterns.LargeMessageProxy;
import com.ddm.app.serialization.AkkaSerializable;
import com.ddm.app.singletons.InputConfigurationSingleton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;
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
        //TODO: Change Integer by Result
        Integer result;
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
        this.inputFiles = InputConfigurationSingleton.get().getInputFiles();

        this.inputReaders = new ArrayList<>(inputFiles.length);
        for (int id = 0; id < this.inputFiles.length; id++)
            this.inputReaders.add(context.spawn(InputReader.create(id), InputReader.DEFAULT_NAME + "_" + id));

        this.resultCollector = context.spawn(ResultCollector.create(), ResultCollector.DEFAULT_NAME);
        this.largeMessageProxy = this.getContext().spawn(LargeMessageProxy.create(this.getContext().getSelf().unsafeUpcast()), LargeMessageProxy.DEFAULT_NAME);

        this.modificationWorkers = new ArrayList<>();

        context.getSystem().receptionist().tell(Receptionist.register(videoSequencerService, context.getSelf()));
    }

    /////////////////
    // Actor State //
    /////////////////

    private long startTime;

    private final File[] inputFiles;
    private final List<ActorRef<InputReader.Message>> inputReaders;
    private final List<ActorRef<ModificationWorker.Message>> modificationWorkers;
    private final ActorRef<ResultCollector.Message> resultCollector;
    private final ActorRef<LargeMessageProxy.Message> largeMessageProxy;

    //TODO : change Integer by Task
    private final Queue<Integer> unassignedTasks = new LinkedList<>();
    private final Queue<ActorRef<ModificationWorker.Message>> idleWorkers = new LinkedList<>();
    //TODO : same here
    private final Map<ActorRef<ModificationWorker.Message>, Integer> busyWorkers = new HashMap<>();

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

        Integer task = 0;

        //TODO : use python script to modify the image

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

        Integer task = this.unassignedTasks.remove();
        this.busyWorkers.put(modificationWorker, task);
        modificationWorker.tell(new ModificationWorker.TaskMessage(this.largeMessageProxy, task));
        return this;
    }

    private Behavior<Message> handle(CompletionMessage message) {
        ActorRef<ModificationWorker.Message> modificationWorker = message.getModificationWorker();
        Integer result = message.getResult();
        // If this was a reasonable result, I would probably do something with it and potentially generate more work ... for now, let's just generate a random, binary IND.

        if (this.unassignedTasks.isEmpty()){
            this.idleWorkers.add(modificationWorker);
            return this;
        }
        // I still don't know what task the worker could help me to solve ... but let me keep her busy.


        // Once I found all unary INDs, I could check if this.discoverNaryDependencies is set to true and try to detect n-ary INDs as well

        Integer task = this.unassignedTasks.remove();
        this.busyWorkers.put(modificationWorker, task);
        modificationWorker.tell(new ModificationWorker.TaskMessage(this.largeMessageProxy, task));
        return this;
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

        Integer task = this.busyWorkers.remove(modificationWorker);

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