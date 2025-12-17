package app.stacq.spondmet.weather;

import app.stacq.spondmet.event.Event;
import app.stacq.spondmet.event.EventRepository;
import app.stacq.spondmet.weather.cache.WeatherCacheKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WeatherJob {

    private static final Logger logger = LoggerFactory.getLogger(WeatherJob.class);
    private static final Duration FORECAST_WINDOW = Duration.ofDays(7);

    private final EventRepository eventRepository;
    private final WeatherService weatherService;

    public WeatherJob(EventRepository eventRepository, WeatherService weatherService) {
        this.eventRepository = eventRepository;
        this.weatherService = weatherService;
    }

    @Scheduled(fixedRate = 30 * 60 * 1000)  // Every 30 minutes
    public void precomputeWeatherForUpcomingEvents() {
        logger.info("Starting weather precomputation job");

        Instant now = Instant.now();
        Instant maxTime = now.plus(FORECAST_WINDOW);

        List<Event> events = eventRepository.findUpcomingEventsWithLocation(now, maxTime);
        logger.info("Found {} events eligible for weather precomputation", events.size());

        // Group by rounded coordinates to minimize API calls
        Map<String, Event> uniqueLocations = new HashMap<>();
        for (Event event : events) {
            String cacheKey = WeatherCacheKey.create(
                    event.getLatitude(),
                    event.getLongitude(),
                    event.getStartAt()
            ).key();
            uniqueLocations.putIfAbsent(cacheKey, event);
        }

        logger.info("Unique location/time combinations: {}", uniqueLocations.size());

        int success = 0;
        int failed = 0;

        for (Event event : uniqueLocations.values()) {
            try {
                weatherService.getWeather(
                        event.getLatitude(),
                        event.getLongitude(),
                        event.getStartAt()
                );
                success++;
            } catch (Exception e) {
                logger.warn("Failed to precompute weather for event {} at ({}, {}): {}",
                        event.getId(), event.getLatitude(), event.getLongitude(), e.getMessage());
                failed++;
            }
        }

        logger.info("Weather precomputation completed: {} success, {} failed", success, failed);
    }
}
