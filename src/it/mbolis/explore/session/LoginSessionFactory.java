package it.mbolis.explore.session;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginSessionFactory implements SessionFactory {

	private final Map<String, Session> activeSessions = new ConcurrentHashMap<>();
	private final Map<String, String> auth;

	public LoginSessionFactory(Map<String, String> auth) {
		this.auth = auth;
	}

	@Override
	public Session createSession(Socket socket) throws IOException {
		Session session = new Session(socket);

		String login;
		do {
			login = session.prompt("username");
			if (login == null) {
				throw new IOException();
			}
			login = login.trim();
		} while (login.isEmpty());

		String password;
		do {
			password = session.prompt("password");
			if (password == null) {
				throw new IOException();
			}
			password = password.trim();
		} while (password.isEmpty());

		if (!password.equals(auth.get(login))) {
			session.close();
			throw new IOException();
		}

		Session oldSession = activeSessions.get(login);
		if (oldSession != null) {
			oldSession.close();
		}
		session.setName(login + "@" + System.currentTimeMillis());
		activeSessions.put(login, session);
		return session;
	}

	@Override
	public AsyncSession createSession(AsynchronousSocketChannel socketChannel) {
		AsyncSession session = new AsyncSession(socketChannel);
		session.setName("miao");
		return session;
	}
}