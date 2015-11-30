package it.mbolis.explore.actors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class ThreadedActor<T> extends Thread {

	public static <T> ThreadedActor<T> start(Consumer<T> onMessage) {
		ThreadedActor<T> actor = new ThreadedActor<>();
		actor.handler = onMessage;
		actor.start();
		return actor;
	}

	public static <T> void receive(Consumer<T> onMessage) {
		Thread currentThread = currentThread();
		if (currentThread instanceof ThreadedActor) {
			@SuppressWarnings("unchecked")
			ThreadedActor<T> actor = (ThreadedActor<T>) currentThread;
			actor.handler = onMessage;
		} else {
			throw new UnsupportedOperationException("receive can be called only by Actor.");
		}
	}

	public static <T> void loop() {
		Thread currentThread = currentThread();
		if (currentThread instanceof ThreadedActor) {
			@SuppressWarnings("unchecked")
			ThreadedActor<T> actor = (ThreadedActor<T>) currentThread;
			actor.handler = actor.handling;
		} else {
			throw new UnsupportedOperationException("loop can be called only by Actor.");
		}
	}

	private final BlockingQueue<T> mailbox = new LinkedBlockingQueue<>();
	private Consumer<T> handler, handling;

	private ThreadedActor() {
	}

	public void send(T message) {
		try {
			mailbox.put(message);
		} catch (InterruptedException e) {
			return;
		}
	}

	@Override
	public void run() {
		while (handler != null) {
			try {
				T message = mailbox.take();
				handling = this.handler;
				this.handler = null;
				handling.accept(message);
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	public static void main(String[] args) {
		ThreadedActor<String> actor = ThreadedActor.start(message -> {
			if (!"quit".equals(message)) {
				System.out.println(message);
				loop();
			}
		});
		System.out.println("started.");
		actor.send("miaoooo");
		actor.send("miaooooooooooo");
		actor.send("miaoooooooooooooooooo");
		actor.send("miaooooooooooooooooooooooooo");
		try {
			ThreadedActor.receive(x -> {
			});
		} finally {
			actor.send("quit");
		}
	}
}
