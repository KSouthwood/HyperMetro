package com.github.ksouthwood.hypermetro;

import java.util.LinkedList;

public class MetroLine {
    String lineName;

    LinkedList<Station> stations;

    MetroLine(final String name, final LinkedList<Station> stations) {
        this.lineName = name;
        this.stations = stations;
    }

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
            listToPrint.addFirst(new Station("depot"));
            listToPrint.addLast(new Station("depot"));

            for (int index = 0; index < listToPrint.size() - 2; index++) {
                System.out.printf("%s - %s - %s%n",
                                  listToPrint.get(index).getName(),
                                  listToPrint.get(index + 1).getName(),
                                  listToPrint.get(index + 2).getName());
            }
        }
    }

    void addHead(final String stationName) {
        if (stationName != null && !stationName.isEmpty()) {
            stations.addFirst(new Station(stationName));
        }
    }

    void append(final String stationName) {
        if (stationName != null && !stationName.isEmpty()) {
            stations.addLast(new Station(stationName));
        }
    }

    void remove(final String stationName) {
        if (stationName != null && !stationName.isEmpty()) {
            var station = stations.stream()
                                  .filter(x -> x.getName().equals(stationName))
                                  .toList();
            if (!station.isEmpty()) {
                stations.remove(station.get(0));
            }
        }
    }
}
