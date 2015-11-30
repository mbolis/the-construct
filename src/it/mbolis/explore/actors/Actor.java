package it.mbolis.explore.actors;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public abstract class Actor {

	private static class Task implements Runnable {

		private final Actor actor;
		private final Object message;

		Task(Actor actor, Object message) {
			this.actor = actor;
			this.message = message;
		}

		@Override
		public void run() {
			actor.messageReceiver.accept(message);
			actor.working = false;
		}

	}

	private final ExecutorService scheduler;
	private final Queue<Object> mailbox = new LinkedList<>();
	private Consumer<Object> messageReceiver;
	private boolean working;

	public Actor(ExecutorService executor) {
		this.scheduler = executor;
		this.messageReceiver = this::act;
	}

	public synchronized void send(Object message) {
		if (messageReceiver != null && !working) {
			working = true;
			scheduler.submit(new Task(this, message));
		} else {
			mailbox.add(message);
		}
	}

	protected abstract void act(Object message);

	protected void receive(Consumer<Object> onMessage) {
		Object message = mailbox.poll();
		if (message != null) {
			this.messageReceiver = null;
			onMessage.accept(message);
		} else {
			this.messageReceiver = onMessage;
		}
	}

}
