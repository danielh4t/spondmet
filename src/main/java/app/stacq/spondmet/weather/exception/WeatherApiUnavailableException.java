package app.stacq.spondmet.weather.exception;

public class WeatherApiUnavailableException extends RuntimeException {
    public WeatherApiUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
