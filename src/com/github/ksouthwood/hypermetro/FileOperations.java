package com.github.ksouthwood.hypermetro;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class FileOperations {
    public FileOperations() {
    }

    /**
     * Read the station names from the specified file
     * <p>
     * Read from the specified file the station names. If the supplied filename cannot be found, outputs an error
     * message and returns a null list. Otherwise, add all the station names to the list and return it. Returns an empty
     * list if the file is empty.
     *
     * @param filename
     *         name of text file to read
     *
     * @return null if the file doesn't exist; otherwise a LinkedList of String
     */
    LinkedList<String> readFile(String filename) {
        LinkedList<String> input = new LinkedList<>();
        String             line;

        try (BufferedReader file = new BufferedReader(new FileReader(filename))) {
            while ((line = file.readLine()) != null) {
                input.add(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error! Such a file doesn't exist!");
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return input;
    }
}
