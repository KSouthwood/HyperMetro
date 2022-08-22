package com.github.ksouthwood.hypermetro;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class CommandParser {
    private final BufferedReader             reader;
    private final HashMap<String, MetroLine> metroLines;

    private final List<String> validCommands = List.of("/append", "/add-head", "/remove", "/output", "/exit",
                                                       "/connect", "/route");

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

                // command(1) is line name to output
                case "/output" -> {
                    if (command.size() == 2) {
                        metroLines.get(command.get(1)).printStations();
                    }
                }
                // for /append, /add-head and /remove,
                // command(1) is line name to append station to
                // command(2) is the station name to append
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

                // command(1) and command(3) are the line names to connect,
                // command(2) and command(4) are the station names
                case "/connect" -> {
                    if (command.size() == 5) {
                        Station stationFrom = metroLines.get(command.get(1))
                                                        .getStation(command.get(2));
                        Station stationTo = metroLines.get(command.get(3))
                                                      .getStation(command.get(4));
                        stationFrom.setTransfers(stationTo);
                        stationTo.setTransfers(stationFrom);
                    }
                }

                // command(1) and command(2) are the starting line and station name (respectively) of the route
                // to find to command(3) and command(4), the ending line and station name (respectively)
                case "/route" -> {
                    if (command.size() == 5) {
                        printRoute(getRoute(command.get(1), command.get(2), command.get(3), command.get(4)));
                    }
                }
            }
        }
    }

    /**
     * Read a line and parse it into command tokens.
     * <p>
     * Reads a line from the reader, then has it parsed into tokens. Will only return a valid command.
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
     * Parse the supplied string into parts splitting it at spaces while keeping anything inside single/double quotes
     * together.
     *
     * @param commandLine
     *         String to be parsed.
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

    /**
     * Find a route between two stations using breadth first search
     * <p>
     * Takes two stations and attempts to find a route between them. If successful, returns a linked list containing all
     * the stations on the path. Otherwise, we return null indicating failure to find a route.
     *
     * @param startLine
     *         name of the line the start station is on
     * @param startStation
     *         name of the station to begin the search from
     * @param destLine
     *         name of the line the destination station is on
     * @param destStation
     *         name of the destination station
     *
     * @return a linked list containing all the stations on the route or null if we couldn't find a route
     */
    LinkedList<Station> getRoute(final String startLine, final String startStation,
                                 final String destLine, final String destStation) {
        Station start = metroLines.get(startLine).getStation(startStation);
        Station end   = metroLines.get(destLine).getStation(destStation);

        if (start != null && end != null) {
            LinkedList<LinkedList<Station>> queue   = new LinkedList<>();
            HashSet<Station>                visited = new HashSet<>();

            queue.addLast(new LinkedList<>(List.of(start)));

            while (!queue.isEmpty()) {
                var path = queue.removeFirst(); // get the first path in the queue
                var node = path.peekLast(); // get the last node in the path
                if (node == end) {  // if the node matches the end, we're done
                    return path;
                }

                if (!visited.contains(node)) {  // check if we've already visited this node
                    for (var neighbor : node.getNeighbors()) {  // add the neighbors of the node to the path
                        var newPath = new LinkedList<>(path);
                        newPath.addLast(neighbor);
                        queue.addLast(newPath); // add the new path with a neighbor to the queue
                    }
                    visited.add(node);  // mark the node as visited
                }
            }
        }

        return null;
    }

    /**
     * Print out a route between two stations.
     * <p>
     * @param route
     *         a linked list of stations forming the route between stations
     */
    void printRoute(final LinkedList<Station> route) {
        Station previous = new Station("", route.getFirst().getLine());
        for (Station station : route) {
            if (!station.getLine().equals(previous.getLine())) {
                System.out.println("Transition to line " + station.getLine());
            }
            System.out.println(station.getName());
            previous = station;
        }
    }

}
