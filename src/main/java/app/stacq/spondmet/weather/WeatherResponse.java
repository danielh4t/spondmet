package app.stacq.spondmet.weather;

import java.io.Serializable;
import java.time.Instant;

public record WeatherResponse(double temperature, double windSpeed, Instant fetchedAt) implements Serializable {}
