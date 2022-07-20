package com.github.ksouthwood.hypermetro;

public class Main {
    public static void main(String[] args) {
        if (args.length == 1) {
            readFile(args[0]);
        }
    }

    static void readFile(final String filename) {
        var fileOps = new FileOperations();
        var stations = fileOps.readFile(filename);
        var line = new MetroLine("Metro", stations);
        line.printStations();
    }
}
