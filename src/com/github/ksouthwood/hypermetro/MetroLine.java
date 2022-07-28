package com.github.ksouthwood.hypermetro;

import java.util.LinkedHashMap;
import java.util.Map;

public class MetroLine {
    String lineName;

    LinkedHashMap<String, Station> stations;

    MetroLine(final String name, final LinkedHashMap<String, Station> stations) {
        this.lineName = name;
        this.stations = stations;
    }

    /**
     * Output the stations of the line
     * <p>
     * Print the stations of the line in order, with depot's at the beginning and end. Each line lists the station name
     * followed by which line you can transfer to if applicable.
     */
    void printStations() {
        if (stations != null) {
            var listToPrint = new LinkedHashMap<String, Station>();
            listToPrint.put("depotStart", new Station("depot"));
            listToPrint.putAll(stations);
            listToPrint.put("depotEnd", new Station("depot"));

            listToPrint.forEach((name, station) -> {
                                    System.out.print(station.getName());
                                    if (station.hasTransfer()) {
                                        var transfer = station.getTransfer();
                                        for (Map.Entry<String, String> entry : transfer) {
                                            System.out.printf(" - %s (%s)", entry.getValue(), entry.getKey());
                                        }
                                    }
                                    System.out.println();
                                }
            );
        }
    }

    void addHead(final String stationName) {
        if (stationName != null && !stationName.isEmpty()) {
            LinkedHashMap<String, Station> newMap = new LinkedHashMap<>();
            newMap.put(stationName, new Station(stationName));
            newMap.putAll(stations);
            stations = newMap;
        }
    }

    void append(final String stationName) {
        if (stationName != null && !stationName.isEmpty()) {
            stations.put(stationName, new Station(stationName));
        }
    }

    void remove(final String stationName) {
        if (stationName != null && !stationName.isEmpty()) {
            stations.remove(stationName);
        }
    }

    void connect(final String station, final String transferLine, final String transferStation) {
        stations.get(station).setTransfer(transferLine, transferStation);
    }
}
