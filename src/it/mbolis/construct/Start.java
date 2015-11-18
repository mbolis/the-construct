package it.mbolis.construct;

import static java.lang.Double.parseDouble;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Start {

    public static void main(String[] args) {
        PrototypeRoom room = new PrototypeRoom();

        List<Pattern> commandParsers = new ArrayList<>();
        List<Method> commands = new ArrayList<>();
        for (Method m : PrototypeRoom.class.getDeclaredMethods()) {

            FalseTarget falseTargetAnnotation = m.getAnnotation(FalseTarget.class);
            String falseTarget = null;
            if (falseTargetAnnotation != null) {
                falseTarget = falseTargetAnnotation.value();
            }

            StringBuilder pattern = new StringBuilder(m.getName());
            for (Parameter p : m.getParameters()) {
                if (p.getType().equals(Deferred.class)) {
                    break;
                }

                pattern.append("\\s+");
                Preposition preposition = p.getAnnotation(Preposition.class);
                if (preposition != null) {
                    pattern.append("(?:").append(preposition.value()).append("\\s+)?");
                }
                pattern.append("(\\S+|'[^']*'|\"[^\"]*\")");
            }
            commandParsers.add(Pattern.compile(pattern.toString()));
            commands.add(m);
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.print("> ");
            
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();

                for (int i = 0; i < commandParsers.size(); i++) {
                    Pattern re = commandParsers.get(i);

                    Matcher m = re.matcher(line);
                    if (m.matches()) {
                        Method cmd = commands.get(i);
                        List<Object> params = new ArrayList<>();
                        int g = 1;
                        for (Class<?> type : cmd.getParameterTypes()) {
                            if (type.equals(Deferred.class)) {
                                break;
                            }
                            String value = m.group(g++);
                            if (value.startsWith("'") && value.endsWith("'") || value.startsWith("\"'")
                                    && value.endsWith("\"'")) {
                                value = value.replaceFirst("^.(.*).$", "$1");
                            }
                            if (type.equals(String.class)) {
                                params.add(value);
                            } else if (type.equals(Object.class)) {
                                if (Pattern.matches("true|false", value)) {
                                    params.add(Boolean.valueOf(value));
                                } else {
                                    try {
                                        double num = parseDouble(value);
                                        params.add(num);
                                    } catch (NumberFormatException e) {
                                        params.add(value);
                                    }
                                }
                            }
                        }
                        try {
                            cmd.invoke(room, params.toArray());
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
                
                System.out.print("> ");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
