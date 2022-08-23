package com.github.ksouthwood.hypermetro;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        var reader = new BufferedReader(new InputStreamReader(System.in));
        if (args.length == 1) {
            readFile(args[0], reader);
        }
    }

    static void readFile(final String filename, final BufferedReader reader) {
        var lines  = new FileOperations().readJSONFile(filename);
        var controller = new Controller(lines, new CommandParser(reader));
        controller.start();
    }
}
