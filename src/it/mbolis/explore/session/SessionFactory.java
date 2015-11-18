package it.mbolis.explore.session;

import java.io.IOException;
import java.net.Socket;

public interface SessionFactory {

    Session createSession(Socket socket) throws IOException;
}
