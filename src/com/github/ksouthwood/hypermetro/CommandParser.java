package com.github.ksouthwood.hypermetro;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandParser {
    private final BufferedReader reader;

    private final List<String> validCommands = List.of("/append", "/add-head", "/remove", "/output", "/exit");

    public CommandParser(BufferedReader reader) {
        this.reader = reader;
    }

    List<String> getCommand() {
        List<String> command = new ArrayList<>(List.of(""));

        while (!validCommands.contains(command.get(0))) {
            try {
                var input = reader.readLine();
                if (input != null && !input.isEmpty()) {
                    command = parseString(input);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return command;
    }

    private List<String> parseString(final String commandLine) {
        StringBuilder segment      = new StringBuilder();
        List<String>  parts        = new ArrayList<>();
        boolean       doubleQuotes = false;
        boolean       singleQuotes = false;

        for (var ch : commandLine.toCharArray()) {
            switch (ch) {
                case ' ' -> {
                    if (singleQuotes || doubleQuotes) {
                        segment.append(ch);
                        break;
                    }

                    if (!segment.isEmpty()) {
                        parts.add(segment.toString());
                        segment.setLength(0);
                    }
                }

                case '"' -> {
                    if (singleQuotes) {
                        segment.append(ch);
                        break;
                    }

                    if (doubleQuotes) {
                        parts.add(segment.toString());
                        segment.setLength(0);
                        doubleQuotes = false;
                        break;
                    }

                    doubleQuotes = true;
                }

                case '\'' -> {
                    if (doubleQuotes) {
                        segment.append(ch);
                        break;
                    }

                    if (singleQuotes) {
                        parts.add(segment.toString());
                        segment.setLength(0);
                        singleQuotes = false;
                        break;
                    }

                    singleQuotes = true;
                }

                case '\n' -> {
                    if (!segment.isEmpty()) {
                        parts.add(segment.toString());
                        segment.setLength(0);
                    }
                }

                default -> segment.append(ch);
            }
        }

        if (!segment.isEmpty()) {
            parts.add(segment.toString());
        }

        return parts;
    }
}
