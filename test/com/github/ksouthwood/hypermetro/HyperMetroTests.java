package com.github.ksouthwood.hypermetro;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Stream;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOutNormalized;
import static org.junit.jupiter.api.Assertions.*;


class HyperMetroTests {
    @Test
    public void testReadEmptyFile() {
        var result = new FileOperations().readFile("test/test_files/empty.txt");
        assertTrue(result.isEmpty());
    }

    @Test
    public void testBaltimoreFile() {
        var result = new FileOperations().readFile("test/test_files/baltimore.txt");
        //noinspection SpellCheckingInspection
        assertEquals(result,
                     List.of("Owings Mills", "Old Court", "Milford Mill", "Reiserstown Plaza", "Rogers Avenue",
                             "West Cold Spring", "Mondawmin", "Penn North", "Uptown", "State Center",
                             "Lexington Market", "Charles Center", "Shot Tower/Market Place",
                             "Johns Hopkins Hospital"));
    }

    @Test
    public void testInvalidFile() throws Exception {
        var output = tapSystemOutNormalized(() -> assertNull(new FileOperations()
                                                                     .readFile("test/test_files/invalid.txt")));
        assertEquals("Error! Such a file doesn't exist!\n", output);
    }


    @ParameterizedTest
    @MethodSource("commandParseTestStrings")
    public void testParseString(final String command, final List<String> expectedResult) {
        var lines = new FileOperations().readJSONFile("test/test_files/json_test.json");
        var commandParser = new CommandParser(lines, new BufferedReader(new StringReader(command)));
        assertEquals(expectedResult, commandParser.getCommand());
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
                                      /add-head Hammersmith-and-City "Test station"
                                      """,
                                      List.of("/add-head", "Hammersmith-and-City", "Test station")),
                         Arguments.of("""
                                      /remove "Jake's Line" 'My "Jumping" Place'
                                      """,
                                      List.of("/remove", "Jake's Line", "My \"Jumping\" Place")),
                         Arguments.of("""
                                      /add Baltimore "Maryland"
                                      
                                      output
                                      /append Baltimore Maryland
                                      """,
                                      List.of("/append", "Baltimore", "Maryland")));
    }

    @ParameterizedTest
    @MethodSource("stage2Example")
    public void testStage2Example(final String commands, final String expected) {
        var reader = new BufferedReader(new StringReader(commands));
        String result;
        try {
            var lines = new FileOperations().readJSONFile("test/test_files/stage_2_example.json");
            var parser = new CommandParser(lines, reader);
            result = tapSystemOutNormalized(parser::start);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(expected, result);
    }

    private static Stream<Arguments> stage2Example() {
        //noinspection SpellCheckingInspection
        return Stream.of(Arguments.of("""
                                      /output Hammersmith-and-City
                                      /exit
                                      """,
                                      """
                                      depot - Hammersmith - Westbourne-park
                                      Hammersmith - Westbourne-park - depot
                                      """),
                         Arguments.of("""
                                      /append Hammersmith-and-City "Test station"
                                      /output Hammersmith-and-City
                                      /exit
                                      """,
                                      """
                                      depot - Hammersmith - Westbourne-park
                                      Hammersmith - Westbourne-park - Test station
                                      Westbourne-park - Test station - depot
                                      """),
                         Arguments.of("""
                                      /remove Hammersmith-and-City Hammersmith
                                      /output Hammersmith-and-City
                                      /exit
                                      """,
                                      """
                                      depot - Westbourne-park - depot
                                      """));
    }
}
