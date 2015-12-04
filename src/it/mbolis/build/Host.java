package it.mbolis.build;

import static java.util.Collections.unmodifiableMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.mbolis.build.MainHall.Exit;

public class Host {

    private static final Host HOST = new Host();

    static {

        Map<String, String> messages = new HashMap<>();
        HOST.messages = unmodifiableMap(messages);

        messages.put("greet.ask.name", "By what name would you like to be known? ");
        messages.put("greet.ask.password", "Password? ");
        messages.put("greet.reject.credentials", "I do not recognize you...\n");
        messages.put("menu.start.game", "Start game");
        messages.put("menu.prompt", "Option: ");
        messages.put("menu.invalid.option", "Invalid option.\n\n");

        HOST.entrance = new Entrance();

        Map<Class<? extends Helper>, Helper> helpers = new HashMap<>();
        HOST.helpers = unmodifiableMap(helpers);

        Map<String, String> guestBook = new ConcurrentHashMap<>();
        guestBook.put("mbolis", "miao");

        Receptionist receptionist = new Receptionist(guestBook);
        helpers.put(Receptionist.class, receptionist);

        Map<String, Exit> mainHallExits = new LinkedHashMap<>();
        mainHallExits.put("1", new Exit("menu.start.game", Cloakroom.class));

        MainHall mainHall = new MainHall(unmodifiableMap(mainHallExits));
        helpers.put(MainHall.class, mainHall);

        Map<String, Persona[]> personae = new ConcurrentHashMap<>();
        personae.put("mbolis", new Persona[] { new Persona("bOZziL"), null, null });

        Cloakroom cloakroom = new Cloakroom(personae);
        helpers.put(Cloakroom.class, cloakroom);
    }

    public static final Entrance open() {
        Entrance entrance = HOST.entrance;
        entrance.start();
        return entrance;
    }

    public static final void crash() {
        try {
            HOST.entrance.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final <T extends Helper> T introduce(Client client, Class<T> toHelper) {
        T helper = toHelper.cast(HOST.helpers.get(toHelper));
        helper.attend(client);
        client.setHelper(helper);
        return helper;
    }

    public static final String message(String code) {
        String message = HOST.messages.get(code);
        if (message == null) {
            return "{{" + code + "}}";
        }
        return message;
    }

    private Entrance entrance;
    private Map<Class<? extends Helper>, Helper> helpers;
    private Map<String, String> messages;

    private Host() {
    }
}
