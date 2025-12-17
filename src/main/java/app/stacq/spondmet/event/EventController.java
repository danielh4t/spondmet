package app.stacq.spondmet.event;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final EventWeatherFacade eventWeatherFacade;

    public EventController(EventService eventService, EventWeatherFacade eventWeatherFacade) {
        this.eventService = eventService;
        this.eventWeatherFacade = eventWeatherFacade;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Event createEvent(@RequestBody Event event) {
        return eventService.createEvent(event);
    }

    @GetMapping
    public List<Event> getEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    @GetMapping("/{id}/weather")
    public EventWithWeather getEventWithWeather(@PathVariable Long id) {
        return eventWeatherFacade.getEventWithWeather(id);
    }
}