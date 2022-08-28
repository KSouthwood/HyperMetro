package com.github.ksouthwood.hypermetro;

import java.util.LinkedHashMap;

public class MetroLine {
    private final String lineName;

    private Station head;
    private Station tail;

    LinkedHashMap<String, Station> stations = new LinkedHashMap<>();

    MetroLine(final String lineName, final Station firstStation, final Station lastStation) {
        this.lineName = lineName;
        this.head = firstStation;
        this.tail = lastStation;

        var current = head;
        while (current != null) {
            stations.put(current.getName(), current);
            current = current.getNext();
        }
    }

    /**
     * Output the stations of the line
     * <p>
     * Print the stations of the line in order, with depot's at the beginning and end. Each line lists the station name
     * followed by which line you can transfer to if applicable.
     */
    void printStations() {
        if (head == null) { // if head is null, there are no stations on the line
            return;
        }

        Station current = head;
        System.out.println("depot");
        while (current != null) {
            System.out.print(current.getName());
            if (current.hasTransfers()) {
                var transfer = current.getTransfers();
                for (var entry : transfer) {
                    System.out.printf(" - %s (%s)", entry.getName(), entry.getLine());
                }
            }
            System.out.println();
            current = current.getNext();
        }
        System.out.println("depot");
    }

    void addHead(final String stationName, final int time) {
        if (stationName != null && !stationName.isEmpty()) {
            Station newStation = new Station(stationName, lineName, time);
            newStation.setNext(head);
            head.setPrev(newStation);
            head = newStation;
            stations.put(stationName, newStation);
        }
    }

    void append(final String stationName, final int time) {
        if (stationName != null && !stationName.isEmpty()) {
            Station newStation = new Station(stationName, lineName, time);
            newStation.setPrev(tail);
            tail.setNext(newStation);
            tail = newStation;
            stations.put(stationName, newStation);
        }
    }

    void remove(final String stationName) {
        if (stationName != null && !stationName.isEmpty()) {
            Station toRemove = stations.get(stationName);
            Station previous = toRemove.getPrev();
            Station next     = toRemove.getNext();
            if (previous != null) {
                previous.setNext(toRemove.getNext());
            }
            if (next != null) {
                next.setPrev(toRemove.getPrev());
            }
            if (toRemove == head) {
                head = next;
            }
            if (toRemove == tail) {
                tail = previous;
            }
            stations.remove(stationName);
        }
    }

    Station getStation(final String station) {
        if (stations.containsKey(station)) {
            return stations.get(station);
        }

        System.out.printf("No station %s on the %s line.", station, lineName);
        return null;
    }

}
