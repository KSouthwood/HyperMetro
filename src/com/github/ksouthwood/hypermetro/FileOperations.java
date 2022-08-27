package com.github.ksouthwood.hypermetro;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FileOperations {
    private static final HashMap<String, ArrayList<String>> TRANSFERS = new HashMap<>();

    private static String lineName;

    private FileOperations() {
    }

    /**
     * Read a JSON file.
     * <p>
     * Attempts to read the specified JSON file and return the contents in a map with each line found in a MetroLine
     * object. Outputs an error and returns null if the file doesn't exist.
     *
     * @param filename
     *         String for the filename to read.
     *
     * @return Map of line name and corresponding MetroLine object or null if the file didn't exist.
     */
    static HashMap<String, MetroLine> readJSONFile(final String filename) {
        HashMap<String, MetroLine> stationsList = new HashMap<>();
        try (BufferedReader file = new BufferedReader(new FileReader(filename))) {
            stationsList = parseJSONFile(file);
        } catch (FileNotFoundException e) {
            System.out.println("Error! Such a file doesn't exist!");
            return null;
        } catch (JsonSyntaxException e) {
            System.out.println("File to be read is malformed JSON. Please specify a valid JSON file.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stationsList;
    }

    /**
     * Parse a JSON file and create necessary objects.
     * <p>
     * Parse the JSON file creating MetroLine and Station objects as needed in the specified order.
     *
     * @param file
     *         Object to read JSON from.
     *
     * @return Map of MetroLine objects with their name as keys. Map is empty if there is a problem encountered with the
     *         file to be read.
     */
    private static HashMap<String, MetroLine> parseJSONFile(final BufferedReader file) throws JsonSyntaxException {
        // map to hold each metro line keyed by line name
        HashMap<String, MetroLine> metroLines = new HashMap<>();

        JsonElement fileElement = JsonParser.parseReader(file);

        if (fileElement.isJsonNull()) {     // if we have a JsonNull value, the file read is empty
            return metroLines;
        }

        metroLines = createLines(fileElement.getAsJsonObject());

        // add any transfer points between the lines. Needs to be done after the lines
        // are created to ensure we have all the necessary station objects created.
        for (var transferStation : TRANSFERS.keySet()) {
            String  lineA = TRANSFERS.get(transferStation).get(0);
            String  lineB = TRANSFERS.get(transferStation).get(1);
            Station statA = metroLines.get(lineA).getStation(transferStation);
            Station statB = metroLines.get(lineB).getStation(transferStation);
            statA.setTransfers(statB);
            statB.setTransfers(statA);
        }

        return metroLines;
    }

    /**
     * Creates the map of lines and stations.
     * <p>
     * Goes through the parse tree of the file processing each line object and returning a map of each line with
     * their respective stations in the correct order.
     *
     * @param fileObject
     *         the JSON object holding the parse tree of the read file
     *
     * @return the map of the lines with the stations
     */
    private static HashMap<String, MetroLine> createLines(final JsonObject fileObject) {
        HashMap<String, MetroLine> metroLines = new HashMap<>();

        // iterate over each metro line in the file
        for (var metroLine : fileObject.entrySet()) {
            lineName = metroLine.getKey();

            // map to hold the stations, sorts by ascending key value
            TreeMap<Integer, Station> stationTreeMap = new TreeMap<>();

            // iterate through each station, reading its specifications (name, transfer status, etc.)
            for (var stationEntry : fileObject.getAsJsonObject(lineName).entrySet()) {
                // get details for the station
                int     stationNumber = Integer.parseInt(stationEntry.getKey());
                Station station       = createStation(stationEntry.getValue());
                stationTreeMap.put(stationNumber, station);
            }

            // update the stations with their previous and next stops
            stationTreeMap.forEach((key, val) -> {
                var prevStation = stationTreeMap.get(key - 1);
                var nextStation = stationTreeMap.get(key + 1);
                val.setPrev(prevStation);
                val.setNext(nextStation);
            });

            // get the first and last stations and create the line
            var head = stationTreeMap.firstEntry().getValue();
            var tail = stationTreeMap.lastEntry().getValue();
            metroLines.put(lineName, new MetroLine(lineName, head, tail));
        }

        return metroLines;
    }

    /**
     * Create the Station object from a station entry.
     * <p>
     * Takes an entry from a line JSON object and creates the Station object from it. We take into account if the entry
     * is as simple as the station number with a name only, or a whole JSON object itself with name, transfers, and time
     * between stations.
     *
     * @param station
     *         JSON element of station to create
     *
     * @return Station object
     */
    private static Station createStation(final JsonElement station) {
        // the element is only a station number and station name; i.e. ("1": "Hammersmith")
        if (!station.isJsonObject()) {
            return new Station(station.getAsString(), lineName);
        }

        JsonObject stationDetails = station.getAsJsonObject();
        String     stationName    = stationDetails.get("name").getAsString();
        addTransferStations(stationDetails.get("transfer"), stationName);

        return new Station(stationName, lineName);
    }

    /**
     * Add transfer stations (if any) to our map
     * <p>
     *
     * @param transferElement
     *         JSON element holding the transfers
     * @param stationName
     *         name of the station we're processing transfers for
     */
    private static void addTransferStations(final JsonElement transferElement, final String stationName) {
        // the element is null, nothing to process
        if (transferElement.isJsonNull()) {
            return;
        }

        // the element is a single JSON element so add it to the transfers map
        if (!transferElement.isJsonArray()) {
            TRANSFERS.put(stationName,
                          new ArrayList<>(List.of(lineName,
                                                  transferElement.getAsJsonObject().get("line").getAsString())));
            return;
        }

        JsonArray transferArray = transferElement.getAsJsonArray();

        // the array is empty, nothing to process
        if (transferArray.isEmpty()) {
            return;
        }

        // process all the elements in the array adding them to the transfers map
        ArrayList<String> transferLines = new ArrayList<>();
        transferLines.add(lineName);
        for (var transfer : transferArray) {
            transferLines.add(transfer.getAsJsonObject().get("line").getAsString());
        }
        TRANSFERS.put(stationName, transferLines);
    }
}
