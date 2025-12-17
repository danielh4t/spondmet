package app.stacq.spondmet.weather.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class CoordinateUtilsTest {

    @ParameterizedTest
    @CsvSource({
            // positive
            "59.91, 59.9",
            "59.99, 59.9",
            "60.01, 60.0",

            // negative
            "-10.91, -10.9",
            "-10.99, -10.9",

            // zero
            "0.0, 0.0",
            "0.09, 0.0",

            // boundaries
            "59.9, 59.9",
            "-10.9, -10.9"
    })
    void truncate_input_correctlyTruncates(double input, double expected) {
        double result = CoordinateUtils.truncate(input);
        assert result == expected : "Expected " + expected + " but got " + result;
    }
}
