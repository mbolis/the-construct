package it.mbolis.build;

import java.util.Map;

public class Receptionist implements Helper {

    private final Map<String, String> guestBook;

    public Receptionist(Map<String, String> guestBook) {
        this.guestBook = guestBook;
    }

    @Override
    public void attend(Client client) {
        client.setClientState(ClientState.WAIT_IDENTIFY);
        client.send(Host.message("greet.ask.name"));
    }

    @Override
    public void serve(Client client, String request) {
        String input = request.trim();
        switch (client.getClientState()) {
        case WAIT_IDENTIFY:
            client.setUsername(input);
            client.setClientState(ClientState.WAIT_AUTHENTICATE);
            client.send(Host.message("greet.ask.password"));
            break;

        case WAIT_AUTHENTICATE:
            String username = client.getUsername();
            String password = guestBook.get(username);
            if (!input.equals(password)) {
                client.setClientState(ClientState.WAIT_IDENTIFY);
                client.send(Host.message("greet.reject.credentials"));
                client.send(Host.message("greet.ask.name"));
                break;
            }
            Host.introduce(client, MainHall.class);
            break;

        default:
            throw new IllegalStateException("Can't serve a client in " + client.getClientState() + " state.");
        }
    }

}
