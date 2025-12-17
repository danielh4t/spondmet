package app.stacq.spondmet.weather;

import app.stacq.spondmet.weather.client.MetClient;
import app.stacq.spondmet.weather.exception.WeatherNotFoundException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class WeatherService {

    private final MetClient metClient;

    public WeatherService(MetClient metClient) {
        this.metClient = metClient;
    }

    @Cacheable(
            value = "weatherCache",
            key = "T(app.stacq.spondmet.weather.cache.WeatherCacheKey).create(#latitude, #longitude, #eventTime).key()"
    )
    public WeatherResponse getWeather(double latitude, double longitude, Instant eventTime) {
        // check event starts in next 7 days
        Duration sevenDays = Duration.ofDays(7);
        if (eventTime.isAfter(Instant.now().plus(sevenDays))) {
            throw new WeatherNotFoundException("Event time is beyond the supported forecast range");
        }

        return metClient.fetchWeatherData(latitude, longitude, eventTime)
                .map(data -> new WeatherResponse(
                        data.temperature(),
                        data.windSpeed(),
                        Instant.now()
                ))
                .orElseThrow(() -> new WeatherNotFoundException("Weather data not found for the given parameters"));
    }
}
