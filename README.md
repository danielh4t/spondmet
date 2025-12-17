# Spond Weather API

Weather forecasts for Spond events. Shows temperature (°C) and wind speed (m/s) for events within 7 days.

## Quick Start

```bash
./gradlew bootRun        # Start server on :5000
./gradlew test           # Run tests
docker compose up        # Run with Redis
```

## API Endpoints

| Endpoint | Description |
|----------|-------------|
| `GET /api/weather?lat=59.9&lon=10.7&eventTime=2025-12-20T10:00:00Z` | Get weather forecast |
| `GET /api/events/{id}/weather` | Get event with embedded weather |
| `GET /api/events` | List all events |
| `POST /api/events` | Create event |


### Create Event
**Request:**
```bash
curl -X POST http://localhost:5000/api/events \
    -H "Content-Type: application/json" \
    -d '{
      "name": "Football Practice",
      "latitude": 59.9,
      "longitude": 10.7,
      "startAt": "2025-12-20T10:00:00Z",
      "endAt": "2025-12-20T12:00:00Z"
    }'
```

### Get Weather for Event
**Request:**
```bash
curl http://localhost:5000/api/events/1/weather
```

**Response:**
```json
{
  "id": 1,
  "name": "Swimming",
  "latitude": 59.9,
  "longitude": 10.7,
  "startAt": "2025-12-20T10:00:00Z",
  "endAt": "2025-12-20T12:00:00Z",
  "weather": {
    "temperature": 2.7,
    "windSpeed": 1.7,
    "fetchedAt": "2025-12-17T16:00:22.114659Z"
  }
}
```

## Architecture


**Key Components:**
- `WeatherService` - Fetches from met.no
- `EventWeatherFacade` - Combines event + weather
- `WeatherJob` - Pre-fetches weather every 30 min

## Deployment

```bash
docker build -t spond-weather .
docker compose up
```


## Tech Stack

- Java 21, Spring Boot 4.0
- H2 (dev) / PostgreSQL (prod)

## TODOs

- [ ] True L1/L2 tiered caching (Caffeine → Redis fallback)
- [ ] Weather condition descriptions
- [ ] Batch endpoint for multiple events
- [ ] Weather API fallback for met.no failures such as yr.no
