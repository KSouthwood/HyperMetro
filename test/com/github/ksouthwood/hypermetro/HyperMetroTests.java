package com.github.ksouthwood.hypermetro;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;


class HyperMetroTests {
    @Test
    public void testEmptyFile() {
        var result = new FileOperations().readFile("test/test_files/empty.txt");
        assertTrue(result.isEmpty());
    }

    @Test
    public void testBaltimoreFile() {
        var result = new FileOperations().readFile("test/test_files/baltimore.txt");
        assertEquals(result,
                     List.of("Owings Mills", "Old Court", "Milford Mill", "Reiserstown Plaza", "Rogers Avenue",
                             "West Cold Spring", "Mondawmin", "Penn North", "Uptown", "State Center",
                             "Lexington Market", "Charles Center", "Shot Tower/Market Place",
                             "Johns Hopkins Hospital"));
    }

    @Test
    public void testInvalidFile() {
        var result = new FileOperations().readFile("test/test_files/invalid.txt");
        assertNull(result);
    }

    @Test
    public void testOutputBaltimore() throws Exception {
        var stations = new FileOperations().readFile("test/test_files/baltimore.txt");
        var result = tapSystemOut(() -> Main.printStations(stations));
        assertEquals("""
                     depot - Owings Mills - Old Court
                     Owings Mills - Old Court - Milford Mill
                     Old Court - Milford Mill - Reiserstown Plaza
                     Milford Mill - Reiserstown Plaza - Rogers Avenue
                     Reiserstown Plaza - Rogers Avenue - West Cold Spring
                     Rogers Avenue - West Cold Spring - Mondawmin
                     West Cold Spring - Mondawmin - Penn North
                     Mondawmin - Penn North - Uptown
                     Penn North - Uptown - State Center
                     Uptown - State Center - Lexington Market
                     State Center - Lexington Market - Charles Center
                     Lexington Market - Charles Center - Shot Tower/Market Place
                     Charles Center - Shot Tower/Market Place - Johns Hopkins Hospital
                     Shot Tower/Market Place - Johns Hopkins Hospital - depot
                     """, result);
    }
}
