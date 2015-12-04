package it.mbolis.build.actor;

import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.Fiber;

public abstract class Actor<T> {

    private final Fiber fiber;
    final Channel<T> inbox = new MemoryChannel<>();

    protected Actor(Fiber fiber) {
        this.fiber = fiber;
    }

    public final void send(T message) {
        inbox.publish(message);
    }

    public final void start() {
        inbox.subscribe(fiber, this::receive);
        fiber.start();
    }

    private final void receive(T message) {
        act(message);
        if ("!DIE".equals(message)) {
            fiber.dispose();
        }
    }

    protected abstract void act(T message);
}