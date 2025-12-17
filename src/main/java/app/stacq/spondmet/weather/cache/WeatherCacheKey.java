package app.stacq.spondmet.weather.cache;

import app.stacq.spondmet.weather.util.CoordinateUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public record WeatherCacheKey(double roundedLatitude, double roundedLongitude, long hourBucket) {
    public static WeatherCacheKey create(double latitude, double longitude, Instant eventTime) {
        double roundedLatitude = CoordinateUtils.truncate(latitude);
        double roundedLongitude = CoordinateUtils.truncate(longitude);
        long hourBucket = eventTime.truncatedTo(ChronoUnit.HOURS).toEpochMilli() / (60 * 60 * 1000);
        return new WeatherCacheKey(roundedLatitude, roundedLongitude, hourBucket);
    }

    // 1 decimal place
    public String key() {
        return String.format("weather:%.1f:%.1f:%d", roundedLatitude, roundedLongitude, hourBucket);
    }
}
