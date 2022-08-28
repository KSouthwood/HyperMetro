package com.github.ksouthwood.hypermetro;

import java.util.*;

public class Station {
    private final String name;
    private final String line;
    private final int    time;

    private Station prev;
    private Station next;

    private final List<Station> transfers = new LinkedList<>();

    Station(final String name, final String line) {
        this(name, line, 0);
    }

    Station(final String name, final String line, final int time) {
        this.name = name;
        this.line = line;
        this.time = time;
        this.next = null;
        this.prev = null;
    }

    String getName() {
        return name;
    }

    void setTransfers(final Station station) {
        transfers.add(station);
    }

    boolean hasTransfers() {
        return !transfers.isEmpty();
    }

    List<Station> getTransfers() {
        return transfers;
    }

    void setPrev(final Station previous) {
        this.prev = previous;
    }

    Station getPrev() {
        return prev;
    }

    void setNext(final Station next) {
        this.next = next;
    }

    Station getNext() {
        return next;
    }

    LinkedList<Station> getNeighbors() {
        LinkedList<Station> neighbors = new LinkedList<>(transfers);
        neighbors.add(prev);
        neighbors.add(next);
        neighbors.removeIf(Objects::isNull);
        return neighbors;
    }

    String getLine() {
        return line;
    }

    int getTime() {
        return time;
    }
}
