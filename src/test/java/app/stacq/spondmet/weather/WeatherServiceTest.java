package app.stacq.spondmet.weather;

import app.stacq.spondmet.weather.client.MetClient;
import app.stacq.spondmet.weather.client.MetClient.MetWeatherData;
import app.stacq.spondmet.weather.exception.WeatherNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private MetClient metClient;

    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        weatherService = new WeatherService(metClient);
    }

    @Test
    void getWeather_validEventWithin7Days_returnsWeatherResponse() {
        Instant eventTime = Instant.now().plus(3, ChronoUnit.DAYS);
        MetWeatherData mockData = new MetWeatherData(15.5, 3.2, eventTime);
        when(metClient.fetchWeatherData(anyDouble(), anyDouble(), any(Instant.class)))
                .thenReturn(Optional.of(mockData));

        WeatherResponse response = weatherService.getWeather(59.9, 10.7, eventTime);

        assertNotNull(response);
        assertEquals(15.5, response.temperature());
        assertEquals(3.2, response.windSpeed());
        assertNotNull(response.fetchedAt());
    }

    @Test
    void getWeather_eventBeyond7Days_throwsWeatherNotFoundException() {
        Instant eventTime = Instant.now().plus(10, ChronoUnit.DAYS);

        WeatherNotFoundException exception = assertThrows(
                WeatherNotFoundException.class,
                () -> weatherService.getWeather(59.9, 10.7, eventTime)
        );
        assertTrue(exception.getMessage().contains("beyond"));
    }

    @Test
    void getWeather_metClientReturnsEmpty_throwsWeatherNotFoundException() {
        Instant eventTime = Instant.now().plus(2, ChronoUnit.DAYS);
        when(metClient.fetchWeatherData(anyDouble(), anyDouble(), any(Instant.class)))
                .thenReturn(Optional.empty());

        WeatherNotFoundException exception = assertThrows(
                WeatherNotFoundException.class,
                () -> weatherService.getWeather(59.9, 10.7, eventTime)
        );
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void getWeather_eventExactly7DaysAway_succeeds() {
        Instant eventTime = Instant.now().plus(7, ChronoUnit.DAYS).minus(1, ChronoUnit.MINUTES);
        MetWeatherData mockData = new MetWeatherData(10.0, 5.0, eventTime);
        when(metClient.fetchWeatherData(anyDouble(), anyDouble(), any(Instant.class)))
                .thenReturn(Optional.of(mockData));

        WeatherResponse response = weatherService.getWeather(59.9, 10.7, eventTime);

        assertNotNull(response);
    }
}
