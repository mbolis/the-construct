package it.mbolis.explore.session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Session extends Thread {

    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;

    private final String login;
    private final String password;

    private boolean open = true;

    public Session(Socket socket) throws IOException {
        this.socket = socket;

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String login = reader.readLine();
        if (login == null) {
            throw new IOException();
        }

        login = login.trim();
        System.out.println(login);
        if (login.isEmpty()) {
            throw new IOException();
        }

        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

        writer.println(login);
        writer.flush();
        String password = reader.readLine();
        if (password == null) {
            throw new IOException();
        }

        this.login = login;
        this.password = password.trim();
        setName(login + "@" + System.currentTimeMillis());
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public void run() {
        while (open) {
            try {
                String cmd = reader.readLine();
                if (cmd == null) {
                    close();
                    break;
                }

                System.out.println(cmd);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() throws IOException {
        open = false;
        writer.close();
        reader.close();
    }
}
