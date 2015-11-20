package it.mbolis.explore.actors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.jetlang.core.BatchExecutor;
import org.jetlang.core.EventReader;
import org.jetlang.core.RunnableExecutorImpl;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.PoolFiberFactory;
import org.jetlang.fibers.ThreadFiber;

public class ActorContext {

    static interface FiberProvider<T> {
        Fiber createFiber(Consumer<T> callback);
    }

    public static interface ThreadedFiber<T> extends FiberProvider<T> {

        @Override
        default Fiber createFiber(Consumer<T> callback) {
            return new ThreadFiber(new RunnableExecutorImpl(new ActorExecutor<>(callback)), null, true);
        }

    }

    private static class ActorState {
        static final ThreadLocal<ReplyTo<?>> current = new ThreadLocal<>();
    }

    static class ReplyTo<T> {
        private final Consumer<T> actor;

        ReplyTo(Consumer<T> actor) {
            this.actor = actor;
        }

        public void send(T msg) {
            actor.accept(msg);
        }
    }

    static class ActorExecutor<T> implements BatchExecutor {
        private final ReplyTo<T> replyTo;

        ActorExecutor(Consumer<T> actor) {
            this.replyTo = new ReplyTo<T>(actor);
        }

        @Override
        public void execute(EventReader commands) {
            ActorState.current.set(replyTo);
            for (int index = 0; index < commands.size(); index++) {
                commands.get(index).run();
            }
            ActorState.current.set(null);
        }

    }

    static final class Pool {
        static final ExecutorService executors = Executors.newFixedThreadPool(Runtime.getRuntime()
                .availableProcessors());
        private static PoolFiberFactory fiberFactory = new PoolFiberFactory(executors);

        public static <T> Fiber create(Consumer<T> callback) {
            ActorExecutor<T> e = new ActorExecutor<>(callback);
            return fiberFactory.create(e);
        }

        public static void shutdown() {
            executors.shutdown();
        }
    }

    public interface PooledFiber<T> extends FiberProvider<T> {

        @Override
        default Fiber createFiber(Consumer<T> callback) {
            return Pool.create(callback);
        }
    }

    static class Pending<T> {
        private final T message;
        private final ReplyTo<T> sender;

        Pending(T message, ReplyTo<T> sender) {
            this.message = message;
            this.sender = sender;
        }

        @Override
        public String toString() {
            return message.toString();
        }
    }

    public static abstract class JetlangActor<T> implements FiberProvider<T> {
        private final Consumer<T> root = this::act;
        private Consumer<T> target = root;
        private final Fiber fiber = createFiber(this::receiveMsg);
        private final List<Pending<T>> pending = new ArrayList<>();
        private ReplyTo<T> sender = null;

        protected ReplyTo<T> sender() {
            return sender;
        }

        protected abstract void act(T message);

        public void receive(Consumer<T> newReact) {
            target = newReact;
        }

        public final void send(T msg) {
            receiveMsg(msg);
        }

        public final void receiveMsg(T msg) {
            ReplyTo<T> sentFrom = (ReplyTo<T>) ActorState.current.get();
            final Runnable runner = () -> {
                if (applyMsg(msg, sentFrom)) {
                    int found = -1;
                    do {
                        found = pendingMatch();
                        if (found >= 0) {
                            pending.remove(found);
                        }
                    } while (found >= 0);
                } else {
                    pending.add(new Pending<T>(msg, sentFrom));
                }
            };
            fiber.execute(runner);
        }

        public boolean applyMsg(T msg, ReplyTo<T> replyTo) {
            sender = replyTo;
            Consumer<T> current = target;
            target = root;
            current.accept(msg);
            sender = null;
            return true;
        }

        public int pendingMatch() {
            for (int index = 0; index < pending.size(); index++) {
                Pending<T> p = pending.get(index);
                if (applyMsg(p.message, p.sender)) {
                    return index;
                }
            }
            return -1;
        }

        public void start() {
            fiber.start();
        }

        public void exit() {
            fiber.dispose();
        }
    }

}
