package it.mbolis.explore.actors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public abstract class SingleThreadedReactor<T> {

	private static class Done extends RuntimeException {
		private static final long serialVersionUID = -3621702083290495838L;
	}

	private final BlockingQueue<T> mailbox = new LinkedBlockingQueue<>();
	private Consumer<T> continuation;

	public SingleThreadedReactor() {
		this.continuation = this::react;
	}

	protected abstract void react(T message);

	public void send(T message) {
		mailbox.add(message);
		if (continuation != null) {
			try {
				receive(continuation);
			} catch (Done e) {
			}
		}
	}

	protected void receive(Consumer<T> continuation) throws Done {
		T message = mailbox.poll();
		if (message != null) {
			this.continuation = null;
			continuation.accept(message);
		} else {
			this.continuation = continuation;
		}
		throw new Done();
	}

	public static void main(String[] args) {
		SingleThreadedReactor<String> reactor = new SingleThreadedReactor<String>() {

			@Override
			protected void react(String message) {
				System.out.println(message);
				receive(this::react);
			}
		};

		reactor.send("miaoooo");
		System.out.println("sent!");
		reactor.send("miaoooo");
	}
}
