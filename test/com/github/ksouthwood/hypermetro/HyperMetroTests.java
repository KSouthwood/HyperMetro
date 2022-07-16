package com.github.ksouthwood.hypermetro;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.junit.jupiter.api.Assertions.*;


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

    @ParameterizedTest
    @MethodSource("commandParseTestStrings")
    public void testParseString(final String command, final List<String> expectedResult) {
        assertEquals(expectedResult, new CommandParser().parseString(command));
    }

    private static Stream<Arguments> commandParseTestStrings() {
        return Stream.of(Arguments.of("""
                                      /exit
                                      """,
                                      List.of("/exit")),
                         Arguments.of("""
                                      /output Hammersmith-and-City
                                      """,
                                      List.of("/output", "Hammersmith-and-City")),
                         Arguments.of("""
                                      /add Hammersmith-and-City "Test station"
                                      """,
                                      List.of("/add", "Hammersmith-and-City", "Test station")),
                         Arguments.of("""
                                      /remove "Jake's Line" 'My "Jumping" Place'
                                      """,
                                      List.of("/remove", "Jake's Line", "My \"Jumping\" Place")));
    }
}
