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
     * Attempts to read the specified JSON file and return the contents in a
     * map with each line found in a MetroLine object. Outputs an error and
     * returns null if the file doesn't exist.
     *
     * @param filename String for the filename to read.
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
     *
     * Parse the JSON file creating MetroLine and Station objects as needed in
     * the specified order.
     *
     * @param file Object to read JSON from.
     *
     * @return Map of MetroLine objects with their name as keys.
     */
    private HashMap<String, MetroLine> parseJSONFile(final BufferedReader file) {
        HashMap<String, MetroLine> metroLines = new HashMap<>();
        JsonElement fileElement = JsonParser.parseReader(file);
        JsonObject fileObject = fileElement.getAsJsonObject();
        for (var entry : fileObject.entrySet()) {
            var lineName = entry.getKey();
            TreeMap<Integer, String> stations = new TreeMap<>();
            for (var stationEntry : fileObject.getAsJsonObject(lineName).entrySet()) {
                stations.put(Integer.parseInt(stationEntry.getKey()), stationEntry.getValue().getAsString());
            }
            var linkedStations = new LinkedList<Station>();
            stations.forEach((key, val) -> linkedStations.addLast(new Station(val)));
            metroLines.put(lineName, new MetroLine(lineName, linkedStations));
        }
        return metroLines;
    }

}
