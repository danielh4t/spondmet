package app.stacq.spondmet.weather;

import app.stacq.spondmet.weather.exception.WeatherApiUnavailableException;
import app.stacq.spondmet.weather.exception.WeatherNotFoundException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Map;


@RestController
@RequestMapping("/api/weather")
@Validated
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public ResponseEntity<?> getWeather(
            @RequestParam @NotNull @Min(-90) @Max(90) Double lat,
            @RequestParam @NotNull @Min(-180) @Max(180) Double lon,
            @RequestParam @NotNull String eventTime
    ) {
        try {
            Instant parsedEventTime = Instant.parse(eventTime);
            WeatherResponse response = weatherService.getWeather(lat, lon, parsedEventTime);
            return ResponseEntity.ok(response);

        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid eventTime format."));
        }
    }

    @ExceptionHandler(WeatherNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(WeatherNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(WeatherApiUnavailableException.class)
    public ResponseEntity<Map<String, String>> handleApiUnavailable(WeatherApiUnavailableException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "Weather service temporarily unavailable"));
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleValidation(Exception e) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", "Invalid coordinates: lat must be -90 to 90, lon must be -180 to 180"));
    }
}
