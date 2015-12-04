package it.mbolis.explore.actors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public abstract class ManyThreadedReactor<T> {

    private static class Done extends RuntimeException {
        private static final long serialVersionUID = 1416854025353877400L;
    }

    private static class ReceiverTask<T> implements Runnable {

        private final ManyThreadedReactor<T> actor;
        private final T message;

        public ReceiverTask(ManyThreadedReactor<T> actor, T message) {
            this.actor = actor;
            this.message = message;
        }

        @Override
        public void run() {
            try {
                actor.receiveMessage(message);
            } catch (Done e) {
            }
        }

    }

    private final BlockingQueue<T> mailbox = new LinkedBlockingQueue<>();
    private Consumer<T> continuation;
    private final ExecutorService scheduler;
    private boolean scheduled;

    public ManyThreadedReactor() {
        this.continuation = this::react;
        this.scheduler = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    protected abstract void react(T message);

    public synchronized void send(T message) {
        if (continuation != null && !scheduled) {
            scheduled = true;
            scheduler.submit(new ReceiverTask<>(this, message));
        } else {
            mailbox.add(message);
        }
    }

    protected void receiveMessage(T message) throws Done {
        Consumer<T> continuation = this.continuation;
        this.continuation = null;
        continuation.accept(message);
        this.scheduled = false;
        throw new Done();
    }

    protected void receive(Consumer<T> continuation) throws Done {
        T message = mailbox.poll();
        if (message != null) {
            this.continuation = null;
            continuation.accept(message);
        } else {
            this.continuation = continuation;
        }
        throw new Done();
    }

    public static void main(String[] args) {
        ManyThreadedReactor<String> reactor = new ManyThreadedReactor<String>() {

            @Override
            protected void react(String message) {
                System.out.println(message);
                receive(this::react);
            }
        };

        reactor.send("miaoooo");
        System.out.println("sent!");
        reactor.send("miaoooo");
    }
}
