package it.mbolis.build;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;

public class Client extends Thread implements Closeable {

    private static final Charset UTF8 = Charset.forName("utf8");

    private final BufferedReader reader;
    private final BufferedWriter writer;

    private String username;

    private ClientState clientState = ClientState.ENTERED;
    private Helper helper;

    public Client(Socket socket) throws IOException {
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF8));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF8));

        setDaemon(true);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ClientState getClientState() {
        return clientState;
    }

    public void setClientState(ClientState clientState) {
        this.clientState = clientState;
    }

    public void setHelper(Helper helper) {
        this.helper = helper;
    }

    @Override
    public void run() {
        if (helper == null) {
            throw new IllegalStateException("No helper is taking care of this client.");
        }

        try {
            String request;
            while (!interrupted() && (request = reader.readLine()) != null) {
                helper.serve(this, request);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Host.crash();
        }
    }

    public void send(String message) {
        try {
            synchronized (this) {
                if (!interrupted()) {
                    writer.write(message);
                    writer.flush();
                }
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
