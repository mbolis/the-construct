package it.mbolis.explore.actors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fi.jumi.actors.ActorRef;
import fi.jumi.actors.ActorThread;
import fi.jumi.actors.Actors;
import fi.jumi.actors.MultiThreadedActors;
import fi.jumi.actors.eventizers.dynamic.DynamicEventizerProvider;
import fi.jumi.actors.listeners.CrashEarlyFailureHandler;
import fi.jumi.actors.listeners.NullMessageListener;

public class JumiDemo {

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
        Actors actors = new MultiThreadedActors(actorsThreadPool, new DynamicEventizerProvider(),
                new CrashEarlyFailureHandler(), new NullMessageListener());
        ActorThread actorThread = actors.startActorThread();

        ActorRef<Miao> demo1 = actorThread.bindActor(Miao.class, new MiaoImpl());
        ActorRef<Miao> demo2 = actorThread.bindActor(Miao.class, new MiaoImpl());
        demo1.tell().miao();
        demo2.tell().miao();
        System.out.println("actors started.");

        actorThread.stop();
        actorsThreadPool.shutdown();
    }
}
