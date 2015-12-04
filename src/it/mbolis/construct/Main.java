package it.mbolis.construct;

import static java.lang.ClassLoader.getSystemResourceAsStream;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createFile;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.write;
import static javax.script.ScriptContext.ENGINE_SCOPE;
import static javax.script.ScriptEngine.FILENAME;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Main {

    public static interface Store {

        void store(Object obj);

        String dump();

        String dump(String key);

        List<String> list();

        boolean has(String key);
    }

    private static Pattern keyValue = Pattern.compile("(\\w+|'.*?'|\".*?\")(?:\\s+(.*))?");
    private static Pattern keyAttrValue = Pattern.compile("(\\w+|'.*?'|\".*?\"):(\\w+|'.*?'|\".*?\")(?:\\s+(.*))?");

    public static void main(String[] args) throws IOException {

        ScriptEngine js = new ScriptEngineManager().getEngineByExtension("js");
        Bindings bindings = js.createBindings();
        js.setBindings(bindings, ENGINE_SCOPE);

        // load utilities
        Store store;
        try (Reader utilReader = new InputStreamReader(getSystemResourceAsStream("util.js"), UTF_8)) {

            js.eval(utilReader);

            // expose utilities through interface
            store = ((Invocable) js).getInterface(js.eval("_store"), Store.class);

        } catch (ScriptException e) {
            System.err.println("unable to parse util.js");
            throw new RuntimeException(e);
        }

        // retrieve store
        Path storePath = Paths.get("store.js");
        if (!exists(storePath)) {
            createFile(storePath);
        }

        // load store content
        try (BufferedReader storeReader = newBufferedReader(storePath)) {

            Object content = js.eval(storeReader);
            store.store(content);

        } catch (ScriptException e) {
            System.err.println("unable to parse store.js");
            throw new RuntimeException(e);
        }

        bindings.put(FILENAME, "[construct]");

        try (Reader isr = new InputStreamReader(System.in); BufferedReader in = new BufferedReader(isr)) {
            System.out.print("> ");
            String line;
            cli: while ((line = in.readLine()) != null) {
                line = line.trim();
                String[] cmd = line.split("\\s+", 2);
                switch (cmd[0]) {
                case "ls":
                case "list":
                    List<String> list = store.list();
                    for (String s : list) {
                        System.out.println(s);
                    }
                    break;
                case "cat":
                    if (cmd.length > 1) {
                        System.out.println(store.dump(cmd[1]));
                    } else {
                        System.out.println("\u001b[31mnothing to display.\u001b[m");
                    }
                    break;
                case "set":
                    if (cmd.length > 1) {
                        Matcher m = keyAttrValue.matcher(cmd[1]);
                        if (m.matches()) {
                            String key = m.group(1).replaceAll("^[\"']|[\"']$", "");
                            String attr = m.group(2).replaceAll("^[\"']|[\"']$", "");
                            String value = m.group(3);
                            if (value == null) {
                                value = "";
                            }

                            StringBuilder def = new StringBuilder(value);
                            String defLine = "";
                            boolean valid = false;
                            String error = null;
                            do {
                                try {
                                    js.eval("(" + def.toString() + ")");
                                    valid = true;
                                } catch (ScriptException e) {
                                    valid = false;
                                    error = e.getMessage();
                                }
                                if (!valid) {
                                    System.out.print("| ");
                                    defLine = in.readLine();
                                    def.append('\n').append(defLine);
                                }
                            } while (!defLine.trim().isEmpty());

                            if (valid) {
                                // try {
                                //
                                //
                                // Object toStore = store.get(key);
                                //
                                // Object toStore = js.eval(format("", key,
                                // def.toString()));
                                // store.store(toStore);
                                //
                                // } catch (ScriptException e) {
                                // System.out.println("\u001b[31mempty
                                // input.\u001b[m");
                                // }
                            } else {
                                System.out.println("\u001b[31munrecognized object.");
                                System.out.println(error + "\u001b[m");
                            }
                        } else {
                            System.out.println("\u001b[31mbad input : " + cmd[1] + " .\u001b[m");
                        }
                    }
                    break;
                case "mk":
                case "make":
                    if (cmd.length > 1) {
                        Matcher m = keyValue.matcher(cmd[1]);
                        if (m.matches()) {
                            String key = m.group(1).replaceAll("^[\"']|[\"']$", "");
                            if (store.has(key)) {
                                System.out.println("\u001b[33m" + key + " already exists.\u001b[m");
                                break;
                            }

                            String value = m.group(2);
                            if (value == null) {
                                value = "";
                            }

                            StringBuilder def = new StringBuilder(value);
                            String defLine = "";
                            boolean valid = false;
                            String error = null;
                            do {
                                try {
                                    js.eval("(" + def.toString() + ")");
                                    valid = true;
                                } catch (ScriptException e) {
                                    valid = false;
                                    error = e.getMessage();
                                }
                                if (!valid) {
                                    System.out.print("| ");
                                    defLine = in.readLine();
                                    def.append('\n').append(defLine);
                                }
                            } while (!defLine.trim().isEmpty());

                            if (valid) {
                                try {

                                    Object toStore = js.eval(format("({'%s':%s})", key, def.toString()));
                                    store.store(toStore);

                                } catch (ScriptException e) {
                                    System.out.println("\u001b[31mempty input.\u001b[m");
                                }
                            } else {
                                System.out.println("\u001b[31munrecognized object.");
                                System.out.println(error + "\u001b[m");
                            }
                        } else {
                            System.out.println("\u001b[31mbad input : " + cmd[1] + " .\u001b[m");
                        }
                    } else {
                        System.out.println("\u001b[31mno key specified.\u001b[m");
                    }
                    break;
                case "type":
                    try {
                        System.out.println(js.eval(cmd[1]).getClass());
                    } catch (ScriptException e) {
                        e.printStackTrace();
                    }
                    break;
                case "q":
                case "quit":
                    break cli;
                }
                System.out.print("> ");
            }
            System.out.println("\nBye!");
        }

        // persist changes to store
        write(storePath, store.dump().getBytes(UTF_8));
    }
}
