package app.stacq.spondmet.weather.client;

import app.stacq.spondmet.weather.exception.WeatherApiUnavailableException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class MetClientImplTest {

    private MockWebServer server;
    private MetClientImpl metClient;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();

        // Point MetClient at our mock server
        String baseUrl = server.url("/").toString();
        metClient = new MetClientImpl(baseUrl, "test-agent");
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void fetchWeatherData_validResponse_returnsData() {
        String json = """
                {
                  "properties": {
                    "timeseries": [{
                      "time": "2025-12-16T10:00:00Z",
                      "data": {
                        "instant": {
                          "details": {
                            "air_temperature": 5.2,
                            "wind_speed": 3.1
                          }
                        }
                      }
                    }]
                  }
                }
                """;
        server.enqueue(new MockResponse()
                .setBody(json)
                .setHeader("Content-Type", "application/json"));


        var result = metClient.fetchWeatherData(59.9, 10.7, Instant.parse("2025-12-16T10:00:00Z"));

        assertTrue(result.isPresent());
        assertEquals(5.2, result.get().temperature());
        assertEquals(3.1, result.get().windSpeed());
    }

    @Test
    void fetchWeatherData_apiError_throwsException() {
        server.enqueue(new MockResponse().setResponseCode(500));

        assertThrows(
                WeatherApiUnavailableException.class,
                () -> metClient.fetchWeatherData(59.9, 10.7, Instant.now())
        );
    }
}
