package it.mbolis.explore;

import static java.util.Collections.singletonMap;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Scanner;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import it.mbolis.explore.session.AsyncSession;
import it.mbolis.explore.session.LoginSessionFactory;
import it.mbolis.explore.session.SessionFactory;

public class Server extends Thread {

	private final AsynchronousServerSocketChannel serverSocket;

	private final SessionContainer sessionContainer;

	private Server(SessionFactory sessionFactory) {
		this(sessionFactory, 0);
	}

	private Server(SessionFactory sessionFactory, int port) {
		this.sessionContainer = new SessionContainer(sessionFactory);

		AsynchronousServerSocketChannel server = null;
		try {
			server = AsynchronousServerSocketChannel.open();
			InetSocketAddress address = new InetSocketAddress(port);
			server.bind(address);
			address = (InetSocketAddress) server.getLocalAddress();
			System.out.println("Listening on port " + address.getPort());
		} catch (IOException e) {
			System.err.println("Could not start server.");
			e.printStackTrace();
			System.exit(1);
		}
		serverSocket = server;
	}

	private volatile boolean running;
	private volatile Future<AsynchronousSocketChannel> accepting;

	@Override
	public void run() {

		sessionContainer.start();
		running = true;

		new Thread(() -> {
			try (Scanner commandline = new Scanner(System.in)) {
				String cmd;
				while ((cmd = commandline.nextLine()) != null) {
					if ("quit".equals(cmd)) {
						running = false;
						accepting.cancel(true);
						return;
					}
				}
			}
		}).start();

		while (running) {
			accepting = serverSocket.accept();
			AsynchronousSocketChannel clientSocket;
			try {
				clientSocket = accepting.get();
			} catch (InterruptedException | CancellationException e) {
				break;
			} catch (ExecutionException e) {
				e.printStackTrace();
				continue;
			}
			try {
				AsyncSession session = sessionContainer.register(clientSocket).get();
				session.readLine(System.out::println);
				session.send("OK");
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				break;
			}
			// new EchoSessionHandler(session).start();
			// StatusPushSessionHandler statusPush = new
			// StatusPushSessionHandler(session);
			// new Thread(() -> {
			// while (session.isOpen()) {
			// statusPush.push("tic");
			// try {
			// Thread.sleep(1000);
			// } catch (InterruptedException e) {
			// break;
			// }
			// }
			// }).start();
			// statusPush.start();
		}

		try {
			sessionContainer.shutdown();
		} catch (Exception e) {
		}
	}

	public static void main(String[] args) {
		Server server = new Server(new LoginSessionFactory(singletonMap("mbolis", "miao")));
		server.start();
	}
}
