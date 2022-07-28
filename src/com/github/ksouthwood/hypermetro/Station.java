package com.github.ksouthwood.hypermetro;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Station {
    private final String              name;

    private final Map<String, String> transfer;

    Station(String name) {
        this.name = name;
        this.transfer = new HashMap<>();
    }

    String getName() {
        return name;
    }

    void setTransfer(final String line, final String station) {
        transfer.put(line, station);
    }

    boolean hasTransfer() {
        return !transfer.isEmpty();
    }

    Set<Map.Entry<String, String>> getTransfer() {
        return transfer.entrySet();
    }
}
