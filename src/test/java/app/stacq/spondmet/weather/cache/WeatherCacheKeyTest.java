package app.stacq.spondmet.weather.cache;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class WeatherCacheKeyTest {

    @Test
    void createKey_sameInput_returnsEqualKeys() {

        WeatherCacheKey cacheKey1 = new WeatherCacheKey(59.9, 10.7, 1765897588L);
        WeatherCacheKey cacheKey2 = new WeatherCacheKey(59.9, 10.7, 1765897588L);

        String key1 = cacheKey1.key();
        String key2 = cacheKey2.key();

        assertEquals(key1, key2);
    }

    @Test
    void createKey_differentInput_returnsDifferentKey() {
        Instant time1 = Instant.parse("2026-01-01T10:00:00Z");
        Instant time2 = Instant.parse("2026-01-01T11:00:00Z");

        String key1 = WeatherCacheKey.create(59.9, 10.7, time1).key();
        String key2 = WeatherCacheKey.create(59.9, 10.7, time2).key();

        assertNotEquals(key1, key2);
    }

    @Test
    void createKey_formatIsCorrect() {
        Instant time = Instant.parse("2025-12-16T10:00:00Z");

        String key = WeatherCacheKey.create(59.94, 10.78, time).key();

        assertTrue(key.startsWith("weather:59.9:10.7:"));
    }
}
