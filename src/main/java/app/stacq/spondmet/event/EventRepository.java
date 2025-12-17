package app.stacq.spondmet.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE e.startAt BETWEEN :now AND :sevenDaysLater " +
            "AND e.latitude IS NOT NULL AND e.longitude IS NOT NULL")
    List<Event> findUpcomingEventsWithLocation(
            @Param("now") Instant now,
            @Param("sevenDaysLater") Instant sevenDaysLater
    );
}
