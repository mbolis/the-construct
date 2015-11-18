package it.mbolis.explore.handler;

public class ClientDisconnectedException extends Exception {

    private static final long serialVersionUID = -1240000343283101487L;

    public ClientDisconnectedException() {
        super("Connection closed by client.");
    }

}
