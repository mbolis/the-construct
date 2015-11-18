package it.mbolis.explore.session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Session {

    private final BufferedReader reader;
    private final PrintWriter writer;

    private String name;
    private boolean open = true;

    Session(Socket socket) throws IOException {
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public boolean isOpen() {
        return open;
    }

    public String prompt(String message) throws IOException {
        writer.printf("%s: ", message);
        return reader.readLine();
    }

    public void send(String message) throws IOException {
        writer.println(message);
    }

    public String receive() throws IOException {
        return reader.readLine();
    }

    public void close() throws IOException {
        open = false;
        writer.close();
        reader.close();
    }

}
