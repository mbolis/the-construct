package it.mbolis.explore.actors;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.core.Callback;
import org.jetlang.core.Disposable;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.PoolFiberFactory;

import fi.jumi.actors.ActorRef;

public class JetlangDemo {

    public static abstract class Actor<T> {
        private final Fiber fiber;
        private final Channel<T> inbox;

        public Actor(Fiber fiber) {
            this(fiber, new MemoryChannel<T>());
        }

        public Actor(Fiber fiber, Channel<T> inbox) {
            this.fiber = fiber;
            this.inbox = inbox;
            inbox.subscribe(fiber, this::receive);
        }

        protected abstract void receive(T message);

        public final void send(T message) {
            inbox.publish(message);
        }
    }

    private static int count = 0;

    public static class Miao extends Actor<String> {

        private final int id = ++count;

        public Miao(Fiber fiber) {
            super(fiber);
        }

        @Override
        protected void receive(String message) {
            System.out.println(id + ": " + message);
        }
    }

    public static void main(String[] args) {
        ExecutorService actorsThreadPool = Executors.newFixedThreadPool(2);
        PoolFiberFactory fiberFactory = new PoolFiberFactory(actorsThreadPool);

        Fiber fiber = fiberFactory.create();
        Miao demo1 = new Miao(fiber);
        Miao demo2 = new Miao(fiber);

        fiber.start();
        demo1.send("miaoooo");
        demo2.send("miaoooo");
        System.out.println("started!");

        fiber.dispose();
    }
}
