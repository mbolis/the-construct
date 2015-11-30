package it.mbolis.explore.session;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.AsynchronousSocketChannel;

public interface SessionFactory {

	Session createSession(Socket socket) throws IOException;

	AsyncSession createSession(AsynchronousSocketChannel socketChannel);
}
