package app.stacq.spondmet.weather.util;


import java.math.BigDecimal;
import java.math.RoundingMode;

public class CoordinateUtils {

    private static final int PRECISION = 1;

    public static double truncate(double coordinate) {
        return BigDecimal.valueOf(coordinate)
                .setScale(PRECISION, RoundingMode.DOWN)
                .doubleValue();
    }
}
