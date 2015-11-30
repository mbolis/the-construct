package it.mbolis.explore.session;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;

public class Connection extends Thread implements Closeable {

    public static enum Status {
        IDENTIFY, AUTHENTICATE, READY
    }

    private static final Charset UTF8 = Charset.forName("utf8");

    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final MessageDispatcher dispatcher;

    private Status status;

    public Connection(Socket socket, MessageDispatcher dispatcher) throws IOException {
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF8));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF8));
        this.dispatcher = dispatcher;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public void run() {
        try {
            String line;
            while (!interrupted() && (line = reader.readLine()) != null) {
                dispatcher.dispatch(this, line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void send(String message) {
        try {
            if (!interrupted()) {
                writer.write(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void close() throws IOException {
        reader.close();
        writer.close();
        interrupt();
    }

}
