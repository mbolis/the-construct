package it.mbolis.explore;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import it.mbolis.explore.session.AsyncSession;
import it.mbolis.explore.session.SessionFactory;

public class SessionContainer implements Runnable {

	private static class SessionTask {

		final CompletableFuture<AsyncSession> future = new CompletableFuture<>();
		final AsynchronousSocketChannel socketChannel;

		SessionTask(AsynchronousSocketChannel socketChannel) {
			this.socketChannel = socketChannel;
		}
	}

	private final Map<String, AsyncSession> activeSessions = new ConcurrentHashMap<>();
	private final SessionFactory sessionFactory;

	private final BlockingQueue<SessionTask> inbox = new LinkedBlockingQueue<>();
	private final ExecutorService scheduler;

	public SessionContainer(SessionFactory sessionFactory) {
		this(sessionFactory, Executors.newSingleThreadExecutor());
	}

	public SessionContainer(SessionFactory sessionFactory, ExecutorService scheduler) {
		this.sessionFactory = sessionFactory;
		this.scheduler = scheduler;

	}

	public synchronized Future<AsyncSession> register(AsynchronousSocketChannel clientSocket) {
		SessionTask task = new SessionTask(clientSocket);
		inbox.add(task);
		return task.future;
	}

	public void start() {
		scheduler.submit(this);
	}

	@Override
	public void run() {
		SessionTask task;
		try {
			task = inbox.take();
		} catch (InterruptedException e) {
			return;
		}
		if (task != null) {
			scheduler.submit(this);
			AsyncSession session = sessionFactory.createSession(task.socketChannel);
			if (session.getName() == null || session.getName().isEmpty()) {
				IllegalStateException exception = new IllegalStateException("Empty session name.");
				task.future.completeExceptionally(exception);
				throw exception;
			}
			activeSessions.put(session.getName(), session);
			task.future.complete(session);
		}
	}

	public synchronized void shutdown() throws InterruptedException {
		scheduler.shutdownNow();
		scheduler.awaitTermination(1, TimeUnit.SECONDS);
	}
}
