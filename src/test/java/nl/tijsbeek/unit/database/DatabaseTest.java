package nl.tijsbeek.unit.database;

import nl.tijsbeek.database.Database;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseTest {

    @Test
    @DisplayName("Arguments to CSV string")
    void argumentsToCsvString() {
        String[] arguments = {"test", "argument2"};
        @Language("csv") String argumentsAsCsv = "\"test\",\"argument2\"\n";

        assertEquals(argumentsAsCsv, Database.argumentsToCsvString(arguments));
    }

    @Test
    @DisplayName("CSV string to arguments")
    void csvStringToArguments() {
        List<String> arguments = List.of("test", "argument2");
        String argumentsAsCsv = "\"test\", \"argument2\"";

        assertEquals(arguments, Database.csvStringToArguments(argumentsAsCsv));
    }
}