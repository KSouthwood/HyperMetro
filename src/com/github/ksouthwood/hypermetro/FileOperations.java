package com.github.ksouthwood.hypermetro;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FileOperations {
    public FileOperations() {
    }

    /**
     * Read the station names from the specified file
     * <p>
     * Read from the specified file the station names. If the supplied filename cannot be found, outputs an error
     * message and returns a null list. Otherwise, add all the station names to the list and return it. Returns an empty
     * list if the file is empty.
     *
     * @param filename
     *         name of text file to read
     *
     * @return null if the file doesn't exist; otherwise a LinkedList of String
     */
    LinkedList<String> readFile(String filename) {
        LinkedList<String> input = new LinkedList<>();
        String             line;

        try (BufferedReader file = new BufferedReader(new FileReader(filename))) {
            while ((line = file.readLine()) != null) {
                input.add(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error! Such a file doesn't exist!");
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return input;
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
     * @return Map of MetroLine objects with their name as keys.
     */
    private HashMap<String, MetroLine> parseJSONFile(final BufferedReader file) {
        // map to hold each metro line keyed by line name
        HashMap<String, MetroLine> metroLines = new HashMap<>();

        JsonObject fileObject = JsonParser.parseReader(file).getAsJsonObject();

        // iterate over each metro line in the file
        for (var metroLine : fileObject.entrySet()) {
            var lineName = metroLine.getKey();

            // map to hold the stations, sorts by ascending key value
            TreeMap<Integer, Station> stationTreeMap = new TreeMap<>();

            // iterate through each station, reading its specifications (name, transfer status, etc.)
            for (var stationEntry : fileObject.getAsJsonObject(lineName).entrySet()) {
                int        stationNumber  = Integer.parseInt(stationEntry.getKey());
                JsonObject stationDetails = stationEntry.getValue().getAsJsonObject();
                String     stationName    = stationDetails.get("name").getAsString();
                Station    station        = new Station(stationName);
                JsonObject transferObject = getTransferStation(stationDetails.get("transfer"));
                if (!(transferObject == null)) {
                    station.setTransfer(transferObject.get("line").getAsString(),
                                        transferObject.get("station").getAsString());
                }
                stationTreeMap.put(stationNumber, station);
            }
            var stationLinkedHashMap = new LinkedHashMap<String, Station>();
            stationTreeMap.forEach((key, val) -> stationLinkedHashMap.put(val.getName(), val));
            metroLines.put(lineName, new MetroLine(lineName, stationLinkedHashMap));
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
