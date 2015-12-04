package it.mbolis.explore;

import static java.util.Arrays.copyOfRange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.Fiber;

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
        final CommandParser commandParser;
        final ActionScheduler scheduler;

        int delay;

        public SocketListener(Reader reader, CommandParser commandParser, ActionScheduler scheduler) {
            if (reader instanceof BufferedReader) {
                this.reader = (BufferedReader) reader;
            } else {
                this.reader = new BufferedReader(reader);
            }
            this.commandParser = commandParser;
            this.scheduler = scheduler;
        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    CommandParser.Command cmd = commandParser.parse(message);
                    scheduler.send(cmd, delay);
                    delay = 1000;
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

    private static class CommandParser {

        static class Command {
            final String name;
            final String[] arguments;

            public Command(String name, String... arguments) {
                this.name = name;
                this.arguments = arguments;
            }

            @Override
            public String toString() {
                return "Cmd:" + name + " " + Arrays.toString(arguments);
            }
        }

        Command parse(String input) {
            String[] parts = input.trim().split("\\s+");
            return new Command(parts[0], copyOfRange(parts, 1, parts.length));
        }

    }

    private static class ActionScheduler {

        final ScheduledExecutorService scheduler;

        ActionScheduler(ScheduledExecutorService scheduler) {
            this.scheduler = scheduler;
        }

        public void send(CommandParser.Command action, int delay) {
            scheduler.schedule(() -> System.out.println(action), delay, TimeUnit.MILLISECONDS);
        }
    }

    public static void main(String[] args) throws IOException {
        try (PipedReader pipeIn = new PipedReader();
                PipedWriter pipeOut = new PipedWriter(pipeIn);
                Scanner input = new Scanner(System.in)) {

            ExecutorService executor = Executors.newFixedThreadPool(4);

            CommandParser commandParser = new CommandParser();

            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
            ActionScheduler actionScheduler = new ActionScheduler(scheduler);

            SocketListener socketListener = new SocketListener(pipeIn, commandParser, actionScheduler);
            socketListener.start();

            while (input.hasNextLine()) {
                String message = input.nextLine();
                pipeOut.write(message + "\n");
                pipeOut.flush();
            }

            executor.shutdown();
            scheduler.shutdown();

            try {
                scheduler.awaitTermination(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
