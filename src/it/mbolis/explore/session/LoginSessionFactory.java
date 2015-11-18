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
        String login = session.getLogin();
        String password = session.getPassword();

        if (!password.equals(auth.get(login))) {
            session.close();
            throw new IOException();
        }

        Session oldSession = sessions.get(login);
        if (oldSession != null) {
            oldSession.close();
        }
        sessions.put(login, session);
        return session;
    }
}