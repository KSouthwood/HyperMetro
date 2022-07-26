package com.github.ksouthwood.hypermetro;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandParser {
    private final BufferedReader reader;
    private final HashMap<String, MetroLine> metroLines;

    private final List<String> validCommands = List.of("/append", "/add-head", "/remove", "/output", "/exit",
                                                       "/connect");

    public CommandParser(HashMap<String, MetroLine> lines, BufferedReader reader) {
        this.metroLines = lines;
        this.reader = reader;
    }

    void start() {
        boolean processCommands = true;

        while (processCommands) {
            var command = getCommand();
            switch (command.get(0)) {
                case "/exit" -> processCommands = false;
                case "/output" -> {
                    if (command.size() == 2) {
                        metroLines.get(command.get(1)).printStations();
                    }
                }
                case "/append" -> {
                    if (command.size() == 3) {
                        metroLines.get(command.get(1)).append(command.get(2));
                    }
                }
                case "/add-head" -> {
                    if (command.size() == 3) {
                        metroLines.get(command.get(1)).addHead(command.get(2));
                    }
                }
                case "/remove" -> {
                    if (command.size() == 3) {
                        metroLines.get(command.get(1)).remove(command.get(2));
                    }
                }
            }
        }
    }

    /**
     * Read a line and parse it into command tokens.
     * <p>
     * Reads a line from the reader, then has it parsed into tokens. Will only
     * return a valid command.
     *
     * @return Valid command as a list of strings.
     */
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

    /**
     * Parse a string into parts.
     * <p>
     * Parse the supplied string into parts splitting it at spaces while keeping
     * anything inside single/double quotes together.
     *
     * @param commandLine String to be parsed.
     *
     * @return List of parts from the parsed string.
     */
    List<String> parseString(final String commandLine) {
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
