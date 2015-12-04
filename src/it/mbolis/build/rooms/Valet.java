package it.mbolis.build.rooms;

import it.mbolis.build.Client;

import java.lang.reflect.ParameterizedType;

public abstract class Valet<R extends Room<?>, S extends Enum<S>> {

    protected final R room;
    protected final Client client;
    protected S state;

    @SuppressWarnings("unchecked")
    public Valet(R room, Client client) {
        this.room = room;
        this.client = client;

        ParameterizedType genericType = (ParameterizedType) getClass().getGenericSuperclass();
        Class<S> enumTypeParam = (Class<S>) genericType.getActualTypeArguments()[1];
        this.state = enumTypeParam.getEnumConstants()[0];
    }

    public S getState() {
        return state;
    }

    public abstract void service(String request);

}
