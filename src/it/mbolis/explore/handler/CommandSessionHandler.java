package it.mbolis.explore.handler;

import it.mbolis.explore.session.Session;

public class CommandSessionHandler extends SessionHandler {

    public CommandSessionHandler(Session session) {
        super(session);
    }

    @Override
    protected void handle(Session session) throws Exception {
        String msg = session.receive();
        if (msg == null || "quit".equals(msg.trim())) {
            throw new ClientDisconnectedException();
        }

        // TODO : Parse commands, update stuff, save the world

    }

}
