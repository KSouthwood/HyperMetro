package com.github.ksouthwood.hypermetro;

import java.util.ArrayList;
import java.util.List;

public class CommandParser {

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

        return parts;
    }
}
