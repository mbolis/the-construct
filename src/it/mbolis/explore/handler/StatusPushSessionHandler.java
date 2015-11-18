package it.mbolis.explore.handler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import it.mbolis.explore.session.Session;

public class StatusPushSessionHandler extends SessionHandler {

    private final BlockingQueue<String> statusUpdates = new LinkedBlockingQueue<String>();

    public StatusPushSessionHandler(Session session) {
        super(session);
    }

    public void push(String status) {
        statusUpdates.add(status);
    }

    @Override
    protected void handle(Session session) throws Exception {
        String status = statusUpdates.poll(500, TimeUnit.MILLISECONDS);
        if (status != null) {
            session.send(status);
        }
    }

}
