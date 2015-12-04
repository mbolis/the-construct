package it.mbolis.explore.session;

public class CommandDispatcher implements MessageDispatcher {

    private final CommandParser parser = new CommandParser();

    @Override
    public void dispatch(Connection connection, String line) {
        parser.parse(line);
    }

}
