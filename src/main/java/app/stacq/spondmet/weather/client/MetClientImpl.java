package app.stacq.spondmet.weather.client;

import app.stacq.spondmet.weather.exception.WeatherApiUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.databind.JsonNode;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Component
public class MetClientImpl implements MetClient {

    private static final Logger logger = LoggerFactory.getLogger(MetClientImpl.class);

    private final RestClient restClient;

    public MetClientImpl(
            @Value("${met.api.base-url:https://api.met.no/weatherapi/locationforecast/2.0}") String baseUrl,
            @Value("${met.api.user-agent:spond-weather-service/1.0}") String userAgent
    ) {
        restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("User-Agent", userAgent)
                .build();
    }

    @Override
    public Optional<MetWeatherData> fetchWeatherData(double latitude, double longitude, Instant eventTime) {
        try {
            JsonNode response = restClient.get()
                    .uri("/compact?lat={lat}&lon={lon}", latitude, longitude)
                    .retrieve()
                    .body(JsonNode.class);

            if (response == null) {
                logger.warn("Received null response from MET API for coordinates ({}, {})", latitude, longitude);
                return Optional.empty();
            }

            return findClosestForecast(response, eventTime);
        } catch (RestClientException e) {
            logger.error("Error fetching weather data from MET API {}", e.getMessage());
            throw new WeatherApiUnavailableException("MET API is unavailable", e);
        }
    }

    private Optional<MetWeatherData> findClosestForecast(JsonNode response, Instant eventTime) {
        JsonNode timeseries = response.path("properties").path("timeseries");

        if (timeseries.isEmpty()) {
            return Optional.empty();
        }

        Instant firstForecast = Instant.parse(timeseries.get(0).path("time").asString());

        if (eventTime.isBefore(firstForecast)) {
            return extractForecast(timeseries.get(0));
        }

        // calculate hours difference between eventTime and firstForecast
        long minutesFromFirstForecast = Duration.between(firstForecast, eventTime).toMinutes();
        int index = (int) Math.round(minutesFromFirstForecast / 60.0);

        // prevent index out of bounds
        index = Math.max(0, Math.min(index, timeseries.size() - 1));

        return extractForecast(timeseries.get(index));
    }

    private Optional<MetWeatherData> extractForecast(JsonNode entry) {
        JsonNode details = entry.path("data").path("instant").path("details");

        return Optional.of(new MetWeatherData(
                details.path("air_temperature").asDouble(),
                details.path("wind_speed").asDouble(),
                Instant.parse(entry.path("time").asString())
        ));
    }
}
