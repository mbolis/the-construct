package it.mbolis.explore.handler;

import java.io.IOException;

import it.mbolis.explore.session.Session;

public abstract class SessionHandler extends Thread {

    private final Session session;

    protected SessionHandler(Session session) {
        this.session = session;
    }

    protected abstract void handle(Session session) throws Exception;

    @Override
    public void run() {
        while (session.isOpen()) {
            try {
                handle(session);
            } catch (Exception e) {
                try {
                    System.out.println("Closing session " + session.getName() + ": " + e.getMessage());
                    session.close();
                } catch (IOException ex) {
                    System.err.println("Unexpected error closing session: " + ex.getMessage());
                }
            }
        }
    }
}