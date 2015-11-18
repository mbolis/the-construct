package it.mbolis.explore;

import static java.util.Collections.singletonMap;
import it.mbolis.explore.session.LoginSessionFactory;
import it.mbolis.explore.session.Session;
import it.mbolis.explore.session.SessionFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

    private final ServerSocket serverSocket;

    private SessionFactory sessionFactory;

    private Server() {
        this(0);
    }

    private Server(int port) {
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            System.out.println("Listening on port " + server.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        serverSocket = server;
    }

    @Override
    public void run() {
        while (true) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
                Session session = sessionFactory.createSession(clientSocket);
                session.start();
            } catch (IOException e) {
                if (clientSocket != null) {
                    try {
                        clientSocket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.sessionFactory = new LoginSessionFactory(singletonMap("mbolis", "miao"));
        server.start();
    }
}
