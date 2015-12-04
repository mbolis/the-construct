package it.mbolis.build;

import java.util.Map;

public class Cloakroom implements Helper {

    private final Map<String, Persona[]> personae;

    public Cloakroom(Map<String, Persona[]> personae) {
        this.personae = personae;
    }

    @Override
    public void attend(Client client) {
        client.send(Host.message("persona.select"));
        Persona[] personae = this.personae.computeIfAbsent(client.getUsername(), x -> new Persona[3]);
        StringBuilder personaMenu = new StringBuilder();
        for (int i = 0; i < personae.length;) {
            Persona persona = personae[i];
            personaMenu.append(++i + ") " + (persona != null ? persona.getName() : "** new persona **") + "\n");
        }
        client.send(personaMenu.toString());
    }

    @Override
    public void serve(Client client, String request) {

    }

}
