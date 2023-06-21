package com.ddm.app.profiling;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.receptionist.Receptionist;
import com.ddm.app.actors.patterns.LargeMessageProxy;
import com.ddm.app.serialization.AkkaSerializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
        //TODO : change Integer by Task
        Integer task;
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
                //.onMessage(ShutdownMessage.class, this::handle)
                .build();
    }

    private Behavior<Message> handle(ReceptionistListingMessage message) {
        Set<ActorRef<VideoSequencer.Message>> videoSequencers = message.getListing().getServiceInstances(VideoSequencer.videoSequencerService);
        for (ActorRef<VideoSequencer.Message> videoSequencer : videoSequencers)
            videoSequencer.tell(new VideoSequencer.RegistrationMessage(this.getContext().getSelf()));
        return this;
    }

    private Behavior<Message> handle(TaskMessage message) {
        this.getContext().getLog().info("Working!");
        // I should probably know how to solve this task, but for now I just pretend some work...
        this.getContext().getLog().info("Task : {}", message.getTask());
        Integer result = 0;

        // Solving the task

        LargeMessageProxy.LargeMessage completionMessage = new VideoSequencer.CompletionMessage(this.getContext().getSelf(), result);
        this.largeMessageProxy.tell(new LargeMessageProxy.SendMessage(completionMessage, message.getVideoSequencerLargeMessageProxy()));

//        if (this.haveToShutDown){
//            return Behaviors.stopped();
//        }
        return this;
    }

//    private Behavior<Message> handle(ShutdownMessage message) {
//        this.haveToShutDown = true;
//        return this;
//    }

}
