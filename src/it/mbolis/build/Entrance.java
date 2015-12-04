package it.mbolis.build;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Entrance extends Thread implements Closeable {

    private final ServerSocket serverSocket;

    private volatile boolean open;

    public Entrance() {
        this(0);
    }

    public Entrance(int portNumber) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNumber);
            System.out.println("Listening on port " + serverSocket.getLocalPort());
        } catch (IOException e) {
            System.err.println("Could not start server.");
            e.printStackTrace();
            System.exit(1);
        }
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {

        open = true;

        Thread cmdline = new Thread(() -> {
            try (Scanner commandline = new Scanner(System.in)) {
                String cmd;
                while ((cmd = commandline.nextLine()) != null) {
                    if ("quit".equals(cmd)) {
                        try {
                            close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                }
            }
        });
        cmdline.setDaemon(true);
        cmdline.start();

        while (open) {
            try {
                Socket clientSocket = serverSocket.accept();
                Client client = new Client(clientSocket);
                Host.introduce(client, Receptionist.class);
                client.start();

            } catch (IOException e) {
                if (open) {
                    // TODO : Handle reconnection
                    e.printStackTrace();
                }
            }

        }

    }

    @Override
    public void close() throws IOException {
        open = false;
        serverSocket.close();
    }

    public static void main(String[] args) {
        Host.open();
    }
}
