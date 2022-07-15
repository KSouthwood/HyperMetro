package com.github.ksouthwood.hypermetro;

import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        var stations = args.length == 1 ? new FileOperations().readFile(args[0]) : null;
        printStations(stations);
    }

    static void printStations(final LinkedList<String> stations) {
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
}
