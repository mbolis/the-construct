package it.mbolis.explore.session;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginSessionFactory implements SessionFactory {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
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

        Session oldSession = sessions.get(login);
        if (oldSession != null) {
            oldSession.close();
        }
        session.setName(login + "@" + System.currentTimeMillis());
        sessions.put(login, session);
        return session;
    }
}