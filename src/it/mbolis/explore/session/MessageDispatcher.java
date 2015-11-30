package it.mbolis.explore.session;

public interface MessageDispatcher {

    void dispatch(Connection connection, String line);

}
