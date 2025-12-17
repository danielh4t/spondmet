package app.stacq.spondmet.event;

import app.stacq.spondmet.weather.WeatherResponse;
import app.stacq.spondmet.weather.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class EventWeatherFacade {

    private static final Logger logger = LoggerFactory.getLogger(EventWeatherFacade.class);
    private static final Duration FORECAST_WINDOW = Duration.ofDays(7);

    private final EventService eventService;
    private final WeatherService weatherService;

    public EventWeatherFacade(EventService eventService, WeatherService weatherService) {
        this.eventService = eventService;
        this.weatherService = weatherService;
    }

    public EventWithWeather getEventWithWeather(Long eventId) {
        Event event = eventService.getEventById(eventId);

        if (!isEligibleForWeather(event)) {
            return EventWithWeather.from(event);
        }

        try {
            WeatherResponse weather = weatherService.getWeather(
                    event.getLatitude(),
                    event.getLongitude(),
                    event.getStartAt()
            );
            return EventWithWeather.from(event, weather);
        } catch (Exception e) {
            logger.warn("Failed to fetch weather for event {}: {}", eventId, e.getMessage());
            return EventWithWeather.from(event);
        }
    }

    private boolean isEligibleForWeather(Event event) {
        if (event.getLatitude() == null || event.getLongitude() == null) {
            return false;
        }
        if (event.getStartAt() == null) {
            return false;
        }
        Instant now = Instant.now();
        Instant maxForecastTime = now.plus(FORECAST_WINDOW);
        return event.getStartAt().isAfter(now) && event.getStartAt().isBefore(maxForecastTime);
    }
}
