package it.mbolis.build.actor;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jetlang.fibers.Fiber;

public abstract class SchedulingActor<T> extends Actor<T> {

    private final ScheduledExecutorService scheduler;

    protected SchedulingActor(Fiber fiber, ScheduledExecutorService scheduler) {
        super(fiber);
        this.scheduler = scheduler;
    }

    private class EnqueueMessage implements Runnable {

        private final T message;

        public EnqueueMessage(T message) {
            this.message = message;
        }

        @Override
        public void run() {
            inbox.publish(message);
        }

    }

    public final void schedule(T message, int delay) {
        scheduler.schedule(new EnqueueMessage(message), delay, TimeUnit.MILLISECONDS);
    }

    @Override
    protected abstract void act(T message);
}