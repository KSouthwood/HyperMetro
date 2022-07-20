package com.github.ksouthwood.hypermetro;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class MetroLine {
    String lineName;

    LinkedList<String> stations;

    MetroLine(final String name, final LinkedList<String> stations) {
        this.lineName = name;
        this.stations = stations;
    }

/*
    private void parseStations(final LinkedTreeMap stations) {
        TreeMap stationMap;
        Gson    gson = new Gson();
        gson.fromJson(stations, TreeMap.class);
    }
*/

    /**
     * Output the stations of the line
     * <p>
     * Print the stations of the line in order, with depot's at the beginning
     * and end. Each line has three stops separated by a hyphen.
     *
     */
    void printStations() {
        if (stations != null) {
            var listToPrint = new LinkedList<>(stations);
            listToPrint.addFirst("depot");
            listToPrint.addLast("depot");

            for (int index = 0; index < listToPrint.size() - 2; index++) {
                System.out.printf("%s - %s - %s%n", listToPrint.get(index), listToPrint.get(index + 1),
                                  listToPrint.get(index + 2));
            }
        }

    }

    void addHead(final String stationName) {
        if (stationName != null && !stationName.isEmpty()) {
            stations.addFirst(stationName);
        }
    }

    void append(final String stationName) {
        if (stationName != null && !stationName.isEmpty()) {
            stations.addLast(stationName);
        }
    }

    void remove(final String stationName) {
        if (stationName != null && !stationName.isEmpty()) {
            stations.remove(stationName);
        }
    }
}
