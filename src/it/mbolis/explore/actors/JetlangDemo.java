package it.mbolis.explore.actors;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jetlang.core.Disposable;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.PoolFiberFactory;

import fi.jumi.actors.ActorRef;

public class JetlangDemo {

    public static class MiaoImpl implements Miao {
        private final int id = ++count;

        @Override
        public void miao() {
            while (true) {
                System.out.println(id + ": miaoooooooo");
            }
        }
    }

    public static interface Miao {
        public void miao();
    }

    private static int count = 0;

    public static void main(String[] args) {
        ExecutorService actorsThreadPool = Executors.newFixedThreadPool(2);
        PoolFiberFactory fiberFactory = new PoolFiberFactory(actorsThreadPool);
        CountDownLatch onstop = new CountDownLatch(2);
        Disposable dispose = onstop::countDown;

        Fiber fiber = fiberFactory.create();
        fiber.add(dispose);

        ActorRef<Miao> demo1 = actorThread.bindActor(Miao.class, new MiaoImpl());
        ActorRef<Miao> demo2 = actorThread.bindActor(Miao.class, new MiaoImpl());
        demo1.tell().miao();
        demo2.tell().miao();
        System.out.println("actors started.");

        actorThread.stop();
        actorsThreadPool.shutdown();
    }
}
