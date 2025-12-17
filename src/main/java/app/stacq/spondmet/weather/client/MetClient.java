package app.stacq.spondmet.weather.client;

import java.time.Instant;
import java.util.Optional;

public interface MetClient {

    Optional<MetWeatherData> fetchWeatherData(double latitude, double longitude, Instant eventTime);

    record MetWeatherData(
            double temperature,
            double windSpeed,
            Instant forecastTime
    ) { }
}
