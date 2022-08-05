package com.github.ksouthwood.hypermetro;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class MetroLine {
    String lineName;

    LinkedHashMap<String, Station> stations;
    LinkedList<Station> stationLinkedList = new LinkedList<>();

    MetroLine(final String name, final LinkedHashMap<String, Station> stations) {
        this.lineName = name;
        this.stations = stations;
        stations.forEach((k, v) -> this.stationLinkedList.add(v));
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
            Station newStation = new Station(stationName);
            LinkedHashMap<String, Station> newMap = new LinkedHashMap<>();
            newMap.put(stationName, newStation);
            newMap.putAll(stations);
            stations = newMap;
            stationLinkedList.addFirst(newStation);
        }
    }

    void append(final String stationName) {
        if (stationName != null && !stationName.isEmpty()) {
            Station newStation = new Station(stationName);
            stations.put(stationName, newStation);
            stationLinkedList.addLast(newStation);
        }
    }

    void remove(final String stationName) {
        if (stationName != null && !stationName.isEmpty()) {
            Station toRemove = stations.get(stationName);
            stations.remove(stationName);
            stationLinkedList.remove(toRemove);
        }
    }

    void connect(final String station, final String transferLine, final String transferStation) {
        stations.get(station).setTransfer(transferLine, transferStation);
    }
}
