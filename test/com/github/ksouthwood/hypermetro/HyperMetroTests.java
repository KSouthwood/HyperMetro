package com.github.ksouthwood.hypermetro;

import org.junit.jupiter.api.Disabled;
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
        var result = new FileOperations().readJSONFile("test/test_files/empty.txt");
        assertTrue(result.isEmpty());
    }

    @Test
    public void testBaltimoreFileStage2AndLater() throws Exception {
        var output = tapSystemOutNormalized(() -> {
            var lines = new FileOperations().readJSONFile("test/test_files/baltimore.txt");
            assertTrue(lines.isEmpty());
        });
        assertEquals("File to be read is malformed JSON. Please specify a valid JSON file.\n", output);
    }

    @Test
    public void testFileDoesNotExist() throws Exception {
        var output = tapSystemOutNormalized(() -> {
            var lines = new FileOperations().readJSONFile("test/test_files/invalid.txt");
            assertNull(lines);
        });
        assertEquals("Error! Such a file doesn't exist!\n", output);
    }


    @ParameterizedTest
    @MethodSource("parseStringValidCommands")
    public void testParseStringWithValidCommands(final String command, final List<String> expectedResult) {
        var commandParser = new CommandParser(new BufferedReader(new StringReader(command)));
        assertEquals(expectedResult, commandParser.getCommand());
    }

    private static Stream<Arguments> parseStringValidCommands() {
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
                                      /append Baltimore Maryland
                                      """,
                                      List.of("/append", "Baltimore", "Maryland")),
                         Arguments.of("""
                                      /connect "New York" 'North Houston' Baltimore 'Richard "Dick" Plaza'
                                      """,
                                      List.of("/connect", "New York", "North Houston", "Baltimore", "Richard \"Dick\"" +
                                                                                                    " Plaza"))
        );
    }

    @Disabled("Not testing Stage 2 anymore.")
    @ParameterizedTest
    @MethodSource("stage2Example")
    public void testStage2Example(final String commands, final String expected) {
        var reader = new BufferedReader(new StringReader(commands));
        String result;
        try {
            var lines = new FileOperations().readJSONFile("test/test_files/stage_2_example.json");
            var parser = new CommandParser(reader);
            var controller = new Controller(lines, parser);
            result = tapSystemOutNormalized(controller::start);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(expected, result);
    }

    // Updated during Stage 3 to reflect change in output requirements
    private static Stream<Arguments> stage2Example() {
        //noinspection SpellCheckingInspection
        return Stream.of(Arguments.of("""
                                      /output Hammersmith-and-City
                                      /exit
                                      """,
                                      """
                                      depot
                                      Hammersmith
                                      Westbourne-park
                                      depot
                                      """),
                         Arguments.of("""
                                      /append Hammersmith-and-City "Test station"
                                      /output Hammersmith-and-City
                                      /exit
                                      """,
                                      """
                                      depot
                                      Hammersmith
                                      Westbourne-park
                                      Test station
                                      depot
                                      """),
                         Arguments.of("""
                                      /remove Hammersmith-and-City Hammersmith
                                      /output Hammersmith-and-City
                                      /exit
                                      """,
                                      """
                                      depot
                                      Westbourne-park
                                      depot
                                      """));
    }

//    @Disabled("Disabled until JSON parser has been updated in FileOperations")
    @ParameterizedTest
    @MethodSource("stage3Example")
    public void testStage3Example(final String commands, final String expected) {
        var reader = new BufferedReader((new StringReader(commands)));
        String result;
        try {
            var lines  = new FileOperations().readJSONFile("test/test_files/stage_3_example.json");
            var parser = new CommandParser(reader);
            var controller = new Controller(lines, parser);
            result = tapSystemOutNormalized(controller::start);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(expected, result);
    }

    private static Stream<Arguments> stage3Example() {
        //noinspection SpellCheckingInspection
        return Stream.of(Arguments.of("""
                                      /output Hammersmith-and-City
                                      /connect Hammersmith-and-City Hammersmith Metro-Railway "Edgver road"
                                      /output Hammersmith-and-City
                                      /output Metro-Railway
                                      /exit
                                      """,
                                      """
                                      depot
                                      Hammersmith
                                      Westbourne-park
                                      Baker street - Baker street (Metro-Railway)
                                      depot
                                      depot
                                      Hammersmith - Edgver road (Metro-Railway)
                                      Westbourne-park
                                      Baker street - Baker street (Metro-Railway)
                                      depot
                                      depot
                                      Bishops-road
                                      Edgver road - Hammersmith (Hammersmith-and-City)
                                      Baker street - Baker street (Hammersmith-and-City)
                                      depot
                                      """));
    }

    @ParameterizedTest
    @MethodSource("stage4Example")
    public void testStage4Example(final String commands, final String expected) {
        var reader = new BufferedReader((new StringReader(commands)));
        String result;
        try {
            var lines  = new FileOperations().readJSONFile("test/test_files/stage_3_example.json");
            var parser = new CommandParser(reader);
            var controller = new Controller(lines, parser);
            result = tapSystemOutNormalized(controller::start);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(expected, result);
    }

    private static Stream<Arguments> stage4Example() {
        //noinspection SpellCheckingInspection
        return Stream.of(Arguments.of("""
                                      /route Metro-Railway "Edgver road" Hammersmith-and-City Westbourne-park
                                      /exit
                                      """,
                                      """
                                      Edgver road
                                      Baker street
                                      Transition to line Hammersmith-and-City
                                      Baker street
                                      Westbourne-park
                                      """
                                      ));
    }
}
