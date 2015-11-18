package it.mbolis.construct;

import static java.lang.ClassLoader.getSystemResourceAsStream;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.TreeMap;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class PrototypeRoom {

    private ScriptEngine js = new ScriptEngineManager().getEngineByExtension("js");
    private Invocable invoc = (Invocable) js;
    private Map<String, Object> prototypes = new TreeMap<>();

    {
        try (Reader utils = new InputStreamReader(getSystemResourceAsStream("protoroom.js"))) {
            js.eval(utils);
        } catch (ScriptException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    void look() {
        System.out.println("This is the prototype room.");
        if (!prototypes.isEmpty()) {
            System.out.println("Prototypes are scattered all around:");
            for (String prototypeName : prototypes.keySet()) {
                System.out.println("  " + prototypeName);
            }
        }
        System.out.println();
    }

    void create(String prototypeName) {
        if (prototypes.containsKey(prototypeName)) {
            System.out.println("prototype '" + prototypeName + "' already exists.");
        } else {
            try {
                Object prototype = js.eval("({_name:'" + prototypeName.replace("'", "\\'") + "'})");
                prototypes.put(prototypeName, prototype);
                System.out.println("you create a prototype '" + prototypeName + "'.\n");
            } catch (ScriptException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    void examine(String prototypeName) {
        Object prototype = prototypes.get(prototypeName);
        if (prototype == null) {
            System.out.println("you don't see '" + prototypeName + "'.\n");
        } else {
            try {
                Bindings bindings = js.createBindings();
                bindings.put("prototype", prototype);
                String[][] properties = (String[][]) js
                        .eval("var p=prototype;Java.to(p.keys().map(function(k) {return Java.to([k, JSON.stringify(p[k])], 'java.lang.String[]')}), 'java.lang.String[][]')",
                                bindings);
                for (String[] row : properties) {
                    System.out.println(row[0] + " : " + row[1]);
                }
            } catch (ScriptException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    void set(String key, Object value, @Preposition("on") String prototypeName) {
        System.out.println(key + "=" + value + " on " + prototypeName);
        Object prototype = prototypes.get(prototypeName);
        if (prototype == null) {
            System.out.println("you don't see '" + prototypeName + "'.\n");
        } else {
            try {
                Bindings bindings = js.createBindings();
                bindings.put("prototype", prototype);
                bindings.put("value", value);
                bindings.put("key", key);
                js.eval("prototype[key]=value", bindings);
            } catch (ScriptException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    void def(String key, @Preposition("on") String prototype, Deferred<?> function) {

    }

    void work(@Preposition("on") String prototype) {

    }

    void set(String key, Object value) {

    }

    void def(String key, Deferred<?> function) {

    }

    @FalseTarget("work")
    void stop() {

    }

    void store(String prototype) {

    }

}
