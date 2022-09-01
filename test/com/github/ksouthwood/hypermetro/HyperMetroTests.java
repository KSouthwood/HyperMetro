package com.github.ksouthwood.hypermetro;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.stream.SystemOut;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SystemStubsExtension.class)
class HyperMetroTests {
    @SystemStub
    private SystemOut systemOut;

    @BeforeAll
    static void setFileOperations() {
    }

    @Test
    public void testReadEmptyFile() {
        var result = FileOperations.readJSONFile("test/test_files/empty.txt");
        assertTrue(result != null && result.isEmpty());
    }

    @Test
    public void testMalformedJSONFile() {
        Main.readFile("test/test_files/baltimore.txt", new BufferedReader(new StringReader("")));
        assertEquals("File to be read is malformed JSON. Please specify a valid JSON file.\n",
                     systemOut.getLinesNormalized());
    }

    @Test
    public void testFileDoesNotExist() {
        Main.readFile("test/test_files/invalid.txt", new BufferedReader(new StringReader("")));
        assertEquals("Error! Such a file doesn't exist!\n", systemOut.getLinesNormalized());
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

    @ParameterizedTest
    @MethodSource("stage2Example")
    public void testStage2Example(final String commands, final String expected) {
        var reader = new BufferedReader(new StringReader(commands));
        Main.readFile("test/test_files/stage_2_example.json", reader);
        assertEquals(expected, systemOut.getLinesNormalized());
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

    @ParameterizedTest
    @MethodSource("stage3Example")
    public void testStage3Example(final String commands, final String expected) {
        var reader = new BufferedReader(new StringReader(commands));
        Main.readFile("test/test_files/stage_3_example.json", reader);
        assertEquals(expected, systemOut.getLinesNormalized());
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
        var reader = new BufferedReader(new StringReader(commands));
        Main.readFile("test/test_files/stage_3_example.json", reader);
        assertEquals(expected, systemOut.getLinesNormalized());
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
                                      Transfer to: Hammersmith-and-City
                                      Baker street
                                      Westbourne-park
                                      """
        ));
    }

    @ParameterizedTest
    @MethodSource("stage5Example")
    public void testStage5Example(final String commands, final String expected) {
        var reader = new BufferedReader(new StringReader(commands));
        Main.readFile("test/test_files/stage_5_example.json", reader);
        assertEquals(expected, systemOut.getLinesNormalized());
    }

    private static Stream<Arguments> stage5Example() {
        //noinspection SpellCheckingInspection
        return Stream.of(Arguments.of("""
                                      /fastest-route Hammersmith-and-City "Baker street" Hammersmith-and-City Hammersmith
                                      /exit
                                      """,
                                      """
                                      Baker street
                                      Westbourne-park
                                      Hammersmith
                                      Total trip time: 4 minutes.
                                      """),
                         Arguments.of("""
                                      /append Hammersmith-and-City New-Station 4
                                      /remove Hammersmith-and-City Hammersmith
                                      /output Hammersmith-and-City
                                      /exit
                                      """,
                                      """
                                      depot
                                      Westbourne-park
                                      Baker street - Baker street (Metro-Railway)
                                      New-Station
                                      depot
                                      """));
    }

    @ParameterizedTest
    @MethodSource("advancedRouteFind_Prague_NoTime")
    public void testAdvancedRouteFind_Prague_NoTime(final String commands, final String expected) {
        var reader = new BufferedReader(new StringReader(commands));
        Main.readFile("test/test_files/prague_subway.json", reader);
        assertEquals(expected, systemOut.getLinesNormalized());
    }

    private static Stream<Arguments> advancedRouteFind_Prague_NoTime() {
        //noinspection SpellCheckingInspection
        return Stream.of(Arguments.of("""
                                      /route "Linka C" "Vy\u0161ehrad" "Linka B" "N\u00e1m\u011bst\u00ed Republiky"
                                      /exit
                                      """,
                                      """
                                      Vy\u0161ehrad
                                      I.P.Pavlova
                                      Muzeum
                                      Transfer to: Linka A
                                      Muzeum
                                      M\u016fstek
                                      Transfer to: Linka B
                                      M\u016fstek
                                      N\u00e1m\u011bst\u00ed Republiky
                                      """));
    }

    @ParameterizedTest
    @MethodSource("advancedRouteFind_Prague_WithTime")
    public void testAdvancedRouteFind_Prague_WithTime(final String commands, final String expected) {
        var reader = new BufferedReader(new StringReader(commands));
        Main.readFile("test/test_files/prague_w_time.json", reader);
        assertEquals(expected, systemOut.getLinesNormalized());
    }

    private static Stream<Arguments> advancedRouteFind_Prague_WithTime() {
        //noinspection SpellCheckingInspection
        return Stream.of(Arguments.of("""
                                      /route "Linka C" Vysehrad "Linka B" "Namesti Republiky"
                                      /fastest-route "Linka C" Vysehrad "Linka B" "Namesti Republiky"
                                      /exit
                                      """,
                                      """
                                      Vysehrad
                                      I.P.Pavlova
                                      Muzeum
                                      Transfer to: Linka A
                                      Muzeum
                                      Mustek
                                      Transfer to: Linka B
                                      Mustek
                                      Namesti Republiky
                                      Vysehrad
                                      I.P.Pavlova
                                      Muzeum
                                      Hlavni nadrazi
                                      Florenc
                                      Transfer to: Linka B
                                      Florenc
                                      Namesti Republiky
                                      Total trip time: 29 minutes.
                                      """));
    }

    @ParameterizedTest
    @MethodSource("testStage6_LondonSubway")
    void testStage6_LondonSubway_FindRoute(final String command, final String expected) {
        var reader = new BufferedReader(new StringReader(command));
        Main.readFile("test/test_files/london.json", reader);
        assertEquals(expected, systemOut.getLinesNormalized());
    }

    private static Stream<Arguments> testStage6_LondonSubway() {
        //noinspection SpellCheckingInspection
        return Stream.of(Arguments.of("""
                                      /route 'Piccadilly line' Ickenham 'Central line' 'North Acton'
                                      /exit
                                      """,
                                      """
                                      Ickenham
                                      Ruislip
                                      Ruislip Manor
                                      Eastcote
                                      Rayners Lane
                                      South Harrow
                                      Sudbury Hill
                                      Sudbury Town
                                      Alperton
                                      Park Royal
                                      North Ealing
                                      Ealing Common
                                      Transfer to: District line
                                      Ealing Common
                                      Ealing Broadway
                                      Transfer to: Central line
                                      Ealing Broadway
                                      West Acton
                                      North Acton
                                      """),
                         Arguments.of("""
                                      /fastest-route "Victoria line" "Brixton" "Northern line" "Angel"
                                      /exit
                                      """,
                                      """
                                      Brixton
                                      Stockwell
                                      Transfer to: Northern line
                                      Stockwell
                                      Oval
                                      Kennington
                                      Waterloo
                                      Transfer to: Waterloo & City line
                                      Waterloo
                                      Bank
                                      Transfer to: Northern line
                                      Bank
                                      Moorgate
                                      Old Street
                                      Angel
                                      Total trip time: 47 minutes.
                                      """),
                         Arguments.of("""
                                      /route "Piccadilly line" "Heathrow Terminal 5" "Piccadilly line" "Hounslow West"
                                      /exit
                                      """,
                                      """
                                      Heathrow Terminal 5
                                      Heathrow Terminals 1-2-3
                                      Hatton Cross
                                      Hounslow West
                                      """),
                         Arguments.of("""
                                      /fastest-route "District line" "Richmond" "District line" "Gunnersbury"
                                      /exit
                                      """,
                                      """
                                      Richmond
                                      Kew Gardens
                                      Gunnersbury
                                      Total trip time: 12 minutes.
                                      """));
    }
}
