package it.mbolis.explore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.PoolFiberFactory;

public class Micromodel {

    private static abstract class Actor<T> {
        final Fiber fiber;
        final Channel<T> inbox = new MemoryChannel<>();

        Actor(Fiber fiber) {
            this.fiber = fiber;
        }

        final void send(T message) {
            inbox.publish(message);
        }

        final void start() {
            inbox.subscribe(fiber, this::receive);
            fiber.start();
        }

        final void receive(T message) {
            act(message);
            if ("!DIE".equals(message)) {
                fiber.dispose();
            }
        }

        abstract void act(T message);
    }

    private static class SocketListener extends Thread {

        final BufferedReader reader;
        final Actor<String> commandParser;

        public SocketListener(Reader reader, Actor<String> commandParser) {
            if (reader instanceof BufferedReader) {
                this.reader = (BufferedReader) reader;
            } else {
                this.reader = new BufferedReader(reader);
            }
            this.commandParser = commandParser;
        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    commandParser.send(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class CommandParser extends Actor<String> {

        final ActionScheduler scheduler;

        int delay;

        CommandParser(Fiber fiber, ActionScheduler scheduler) {
            super(fiber);
            this.scheduler = scheduler;
        }

        @Override
        void act(String message) {
            scheduler.send(world -> world.put("message", message), delay);
        }
    }

    private static class ActionScheduler extends Actor<Consumer<Map<String, String>>> {

        final ScheduledExecutorService scheduler;

        ActionScheduler(Fiber fiber, ScheduledExecutorService scheduler) {
            super(fiber);
            this.scheduler = scheduler;
        }

        public void send(Consumer<Map<String, String>> action, int delay) {
            scheduler.schedule(, delay, TimeUnit.MILLISECONDS);
        }

        @Override
        void act(Consumer<Map<String, String>> message) {
            // TODO Auto-generated method stub

        }
    }

    public static void main(String[] args) throws IOException {
        try (PipedReader pipeIn = new PipedReader();
                PipedWriter pipeOut = new PipedWriter(pipeIn);
                Scanner input = new Scanner(System.in)) {

            ExecutorService scheduler = Executors.newFixedThreadPool(4);
            PoolFiberFactory factory = new PoolFiberFactory(scheduler);

            CommandParser commandParser = new CommandParser(factory.create(), scheduler);
            commandParser.start();

            SocketListener socketListener = new SocketListener(pipeIn, commandParser);
            socketListener.start();

            while (input.hasNextLine()) {
                String message = input.nextLine();
                pipeOut.write(message + "\n");
                pipeOut.flush();
            }

            commandParser.send("!DIE");
            scheduler.shutdown();
        }

    }
}
