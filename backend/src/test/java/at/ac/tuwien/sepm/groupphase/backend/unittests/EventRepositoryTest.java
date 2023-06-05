package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Picture;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.type.EventCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class EventRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    EventRepository repository;

    private final Picture picture = Picture.builder().name("test1.jpg").build();

    Event cinemaEvent = Event.builder()
        .title("CinemaEvent")
        .image(picture)
        .description("test-description")
        .startDate(LocalDate.of(2020, Month.JANUARY, 8))
        .endDate(LocalDate.of(2020, Month.JANUARY, 8))
        .durationInMinutes(60)
        .eventCategory(EventCategory.CINEMA)
        .build();

    Event operaEvent = Event.builder()
        .title("OperaEvent")
        .image(picture)
        .description("test-description")
        .startDate(LocalDate.of(2020, Month.JANUARY, 8))
        .endDate(LocalDate.of(2020, Month.JANUARY, 8))
        .durationInMinutes(60)
        .eventCategory(EventCategory.OPERA)
        .build();

    Event concertEvent = Event.builder()
        .title("ConcertEvent")
        .image(picture)
        .description("test-description")
        .startDate(LocalDate.of(2020, Month.JANUARY, 8))
        .endDate(LocalDate.of(2020, Month.JANUARY, 8))
        .durationInMinutes(60)
        .eventCategory(EventCategory.CONCERT)
        .build();

    Event theatreEvent = Event.builder()
        .title("TheatreEvent")
        .image(picture)
        .description("test-description")
        .startDate(LocalDate.of(2020, Month.JANUARY, 8))
        .endDate(LocalDate.of(2020, Month.JANUARY, 8))
        .durationInMinutes(60)
        .eventCategory(EventCategory.THEATRE)
        .build();

    @Test
    public void should_find_no_events_when_empty() {
        Iterable<Event> events = repository.findAll();
        assertThat(events).isEmpty();
    }

    @Test
    public void should_store_an_event() {
        Event event = repository.save(this.cinemaEvent);

        assertThat(event).hasFieldOrPropertyWithValue("title", "CinemaEvent");
        assertThat(event).hasFieldOrPropertyWithValue("description", "test-description");
        assertThat(event).hasFieldOrProperty("startDate");
        assertThat(event).hasFieldOrProperty("endDate");
        assertThat(event).hasFieldOrProperty("eventCategory");
    }

    @Test
    public void should_find_4_events() {
        entityManager.persist(cinemaEvent);
        entityManager.persist(concertEvent);
        entityManager.persist(operaEvent);
        entityManager.persist(theatreEvent);

        Iterable<Event> events = repository.findAll();

        assertThat(events).hasSize(4).contains(cinemaEvent, concertEvent, operaEvent, theatreEvent);
    }

    @Test
    public void should_find_event_by_id() {
        entityManager.persist(cinemaEvent);
        entityManager.persist(concertEvent);
        entityManager.persist(operaEvent);
        entityManager.persist(theatreEvent);

        Optional<Event> foundEvent = repository.findById(concertEvent.getId());
        foundEvent.ifPresent(event -> assertThat(event).isEqualTo(concertEvent));
    }

    @Test
    public void should_findByEventCategory() {
        entityManager.persist(cinemaEvent);
        entityManager.persist(concertEvent);
        entityManager.persist(operaEvent);
        entityManager.persist(theatreEvent);
        Iterable<Event> events1 = repository.findAllByStartDateDesc(EventCategory.CINEMA, Pageable.unpaged());
        Iterable<Event> events2 = repository.findAllByStartDateDesc(EventCategory.CONCERT, Pageable.unpaged());
        Iterable<Event> events3 = repository.findAllByStartDateDesc(EventCategory.OPERA, Pageable.unpaged());
        Iterable<Event> events4 = repository.findAllByStartDateDesc(EventCategory.THEATRE, Pageable.unpaged());

        assertThat(events1).hasSize(1).contains(cinemaEvent);
        assertThat(events2).hasSize(1).contains(concertEvent);
        assertThat(events3).hasSize(1).contains(operaEvent);
        assertThat(events4).hasSize(1).contains(theatreEvent);
    }

    @Test
    public void should_findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase() {
        entityManager.persist(cinemaEvent);
        entityManager.persist(concertEvent);
        entityManager.persist(operaEvent);
        entityManager.persist(theatreEvent);
        Iterable<Event> events1 = repository.findByTitleContainingIgnoreCase(
            "ConcertEvent", PageRequest.of(0, 10));

        assertThat(events1).hasSize(1).contains(concertEvent);
    }
}

