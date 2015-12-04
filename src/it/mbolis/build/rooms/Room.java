package it.mbolis.build.rooms;

import static java.lang.invoke.MethodHandles.lookup;
import static java.lang.invoke.MethodType.methodType;

import java.lang.Thread.State;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.util.Arrays;

import javax.management.RuntimeErrorException;

import it.mbolis.build.Client;

public abstract class Room<V extends Valet<?, ?>> {

    private final MethodHandle valetConstructor;

    public Room() {
        try {
            Constructor<?>[] declaredConstructors = valetType().getDeclaredConstructors();
            System.out.println(Arrays.toString(declaredConstructors));
            // valetConstructor = lookup().findConstructor(valetType(), methodType(Void.class, Room.class,
            // Client.class));
            valetConstructor = lookup().unreflectConstructor(declaredConstructors[0]);
        } catch (SecurityException | IllegalAccessException e) {
            e.printStackTrace();
            throw new IllegalStateException("No constructor available for this Valet type.");
        }
    }

    protected abstract Class<V> valetType();

    public V createValet(Client client) {
        try {
            return (V) valetConstructor.invoke(this, client);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static class Hall extends Room<Page> {

        @Override
        protected Class<it.mbolis.build.rooms.Room.Page> valetType() {
            return Page.class;
        }
    }

    private static class Page extends Valet<Hall, State> {

        public Page(Hall room, Client client) {
            super(room, client);
        }

        @Override
        public void service(String request) {
            System.out.println(request);
        }
    }

    public static void main(String[] args) {
        new Hall().createValet(null).service("miaooooo");
        ;
    }
}
