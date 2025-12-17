package app.stacq.spondmet.event;

import app.stacq.spondmet.weather.WeatherResponse;

import java.time.Instant;

public record EventWithWeather(
        Long id,
        String name,
        Double latitude,
        Double longitude,
        Instant startAt,
        Instant endAt,
        WeatherResponse weather
) {
    public static EventWithWeather from(Event event, WeatherResponse weather) {
        return new EventWithWeather(
                event.getId(),
                event.getName(),
                event.getLatitude(),
                event.getLongitude(),
                event.getStartAt(),
                event.getEndAt(),
                weather
        );
    }

    public static EventWithWeather from(Event event) {
        return from(event, null);
    }
}