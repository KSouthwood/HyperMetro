package com.github.ksouthwood.hypermetro;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FileOperations {
    public FileOperations() {
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
     * @return Map of line name and corresponding MetroLine object.
     */
    HashMap<String, MetroLine> readJSONFile(String filename) {
        HashMap<String, MetroLine> stationsList;
        try (BufferedReader file = new BufferedReader(new FileReader(filename))) {
            stationsList = parseJSONFile(file);
        } catch (FileNotFoundException e) {
            System.out.println("Error! Such a file doesn't exist!");
            return null;
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
     * @return Map of MetroLine objects with their name as keys. Map is empty if there is a problem
     * encountered with the file to be read.
     */
    private HashMap<String, MetroLine> parseJSONFile(final BufferedReader file) {
        // map to hold each metro line keyed by line name
        HashMap<String, MetroLine>         metroLines = new HashMap<>();
        HashMap<String, ArrayList<String>> transfers  = new HashMap<>();

        JsonElement fileElement;
        try {
            fileElement = JsonParser.parseReader(file);
        } catch (JsonSyntaxException e) {
            System.out.println("File to be read is malformed JSON. Please specify a valid JSON file.");
            return metroLines;
        }

        if (fileElement.isJsonNull()) {     // if we have a JsonNull value, the file read is empty
            return metroLines;
        }

        JsonObject fileObject = fileElement.getAsJsonObject();

        // iterate over each metro line in the file
        for (var metroLine : fileObject.entrySet()) {
            var lineName = metroLine.getKey();

            // map to hold the stations, sorts by ascending key value
            TreeMap<Integer, Station> stationTreeMap = new TreeMap<>();

            // iterate through each station, reading its specifications (name, transfer status, etc.)
            for (var stationEntry : fileObject.getAsJsonObject(lineName).entrySet()) {
                // get details for the station
                int        stationNumber  = Integer.parseInt(stationEntry.getKey());
                JsonObject stationDetails = stationEntry.getValue().getAsJsonObject();
                String     stationName    = stationDetails.get("name").getAsString();
                JsonObject transferObject = getTransferStation(stationDetails.get("transfer"));

                // create station and add details
                Station station = new Station(stationName, lineName);
                if (!(transferObject == null)) {
                    transfers.put(stationName, new ArrayList<>(List.of(lineName,
                                                                       transferObject.get("line").getAsString())));
                }
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

        // add any transfer points between the lines. Needs to be done after the lines
        // are created to ensure we have all the necessary station objects created.
        for (var transferStation : transfers.keySet()) {
            String lineA = transfers.get(transferStation).get(0);
            String lineB = transfers.get(transferStation).get(1);
            Station statA = metroLines.get(lineA).getStation(transferStation);
            Station statB = metroLines.get(lineB).getStation(transferStation);
            statA.setTransfers(statB);
            statB.setTransfers(statA);
        }

        return metroLines;
    }

    private JsonObject getTransferStation(final JsonElement transferElement) {
        if (transferElement.isJsonNull()) {
            return null;
        }

        if (transferElement.isJsonArray()) {
            if (transferElement.getAsJsonArray().isEmpty()) {
                return null;
            }
            return transferElement.getAsJsonArray().get(0).getAsJsonObject();
        }

        return transferElement.getAsJsonObject();
    }
}
