package it.mbolis.build;

import java.util.Map;

public class MainHall implements Helper {

    public static class Exit {
        private final String code;
        private final Class<? extends Helper> toHelper;

        public Exit(String code, Class<? extends Helper> toHelper) {
            this.code = code;
            this.toHelper = toHelper;
        }
    }

    private final Map<String, Exit> exits;
    private String options;

    public MainHall(Map<String, Exit> exits) {
        this.exits = exits;
        this.options = exits.entrySet().stream().map(e -> e.getKey() + ") " + Host.message(e.getValue().code))
                .reduce((l, r) -> l + "\n" + r).get() + "\n";
    }

    @Override
    public void attend(Client client) {
        client.setClientState(ClientState.RUNNING);
        client.send(options);
        client.send(Host.message("menu.prompt"));
    }

    @Override
    public void serve(Client client, String request) {
        String option = request.trim();
        Exit exit = exits.get(option);
        if (exit != null) {
            Host.introduce(client, exit.toHelper);
        } else {
            client.send(Host.message("menu.invalid.option"));
            client.send(options);
            client.send(Host.message("menu.prompt"));
        }
    }

}
