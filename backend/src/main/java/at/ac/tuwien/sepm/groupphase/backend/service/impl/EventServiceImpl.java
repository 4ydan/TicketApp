package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventCreateDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.PerformanceMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Picture;
import at.ac.tuwien.sepm.groupphase.backend.exception.FileManagerException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final PerformanceRepository performanceRepository;
    private final EventMapper eventMapper;
    private final PerformanceMapper performanceMapper;
    private final PictureFileManager pictureFileManager;
    private final ArtistServiceImpl artistService;
    private final EventValidator eventValidator;

    @PersistenceContext
    EntityManager entityManager;

    public EventServiceImpl(EventRepository eventRepository,
                            EventMapper eventMapper,
                            EventValidator eventValidator,
                            PerformanceRepository performanceRepository,
                            PerformanceMapper performanceMapper,
                            PictureFileManager pictureFileManager,
                            ArtistServiceImpl artistService) {
        this.eventRepository = eventRepository;
        this.performanceRepository = performanceRepository;
        this.eventMapper = eventMapper;
        this.performanceMapper = performanceMapper;
        this.pictureFileManager = pictureFileManager;
        this.artistService = artistService;
        this.eventValidator = eventValidator;
    }

    @Override
    public List<EventDto> findAllByTerm(EventSearchDto searchDto) {
        log.trace("Find ALL events by term {}", searchDto.getTerm());
        if (searchDto.getPerPage() != null && searchDto.getPerPage() > 0) {
            return this.eventMapper.entityToListDto(this.eventRepository.findByTitleContainingIgnoreCase(
                searchDto.getTerm(),
                PageRequest.of(0, searchDto.getPerPage())
            ));
        } else {
            return this.eventMapper.entityToListDto(this.eventRepository.findByTitleContainingIgnoreCase(
                searchDto.getTerm(),
                Pageable.unpaged()
            ));
        }
    }

    @Override
    public List<EventDto> findTopTen(EventSearchDto searchDto) {
        log.trace("Find TOPTEN events by category {} from year {} and month {}", searchDto.getCategory(), searchDto.getYear(), searchDto.getMonth());
        LocalDate from = LocalDate.of(searchDto.getYear(), searchDto.getMonth(), 1);
        LocalDate to = YearMonth.of(searchDto.getYear(), searchDto.getMonth()).atEndOfMonth();
        List<Object[]> myMap = this.eventRepository.findTopTenEventsByCategory(
            from,
            to,
            searchDto.getCategory(),
            searchDto.getDuration(),
            searchDto.getDescription(),
            PageRequest.of(0, 10)
        );
        EventDto eventDto;
        List<EventDto> listEventDto = new ArrayList<>();
        for (Object[] o : myMap
        ) {
            log.debug("MY OBJECT {}", o);
            Event e = (Event) o[0];
            eventDto = this.eventMapper.entityToDto(e);
            Long soldTickets = (Long) o[1];
            eventDto.setSoldTickets(soldTickets);
            listEventDto.add(eventDto);
        }
        return listEventDto;
    }

    public List<EventDto> findAllByDate(EventSearchDto searchDto) {
        log.trace("Find all events by date {}", searchDto);
        if (searchDto.getDuration() != null
            || searchDto.getStartTime() != null
            || searchDto.getEndTime() != null
            || searchDto.getDescription() != null
            || searchDto.getStartDate() != null
            || searchDto.getEndDate() != null
            || searchDto.getMinPrice() != null
            || searchDto.getMaxPrice() != null
            || searchDto.getStreet() != null
            || searchDto.getCity() != null
            || searchDto.getCountry() != null
        ) {
            return findAllByCriteriaStartDateDesc(searchDto);
        }
        return findAllByStartDateDesc(searchDto);
    }


    public List<EventDto> findAllByTitle(EventSearchDto searchDto) {
        log.trace("Find all events by title {}", searchDto);
        if (searchDto.getDuration() != null
            || searchDto.getStartTime() != null
            || searchDto.getEndTime() != null
            || searchDto.getDescription() != null
            || searchDto.getStartDate() != null
            || searchDto.getEndDate() != null
            || searchDto.getMinPrice() != null
            || searchDto.getMaxPrice() != null
            || searchDto.getStreet() != null
            || searchDto.getCity() != null
            || searchDto.getCountry() != null
        ) {
            return findAllByCriteriaTitleDesc(searchDto);
        }
        return findAllByTitleDesc(searchDto);
    }

    private List<EventDto> findAllByStartDateDesc(EventSearchDto searchDto) {
        log.trace("Find ALL events by category ordered by start date {}", searchDto);
        Pageable pageable = Pageable.unpaged();
        if (searchDto.getPage() != null && searchDto.getPerPage() != null) {
            pageable = PageRequest.of(searchDto.getPage(), searchDto.getPerPage());
        }
        List<Event> events = this.eventRepository.findAllByStartDateDesc(
            searchDto.getCategory(),
            pageable
        );
        log.debug("Found events: {}", events);
        return this.eventMapper.entityToListDto(events);
    }

    private List<EventDto> findAllByCriteriaStartDateDesc(EventSearchDto searchDto) {
        log.trace("Find ALL events by additional criteria {} ordered by start date", searchDto);
        Pageable pageable = Pageable.unpaged();
        if (searchDto.getPage() != null && searchDto.getPerPage() != null) {
            pageable = PageRequest.of(searchDto.getPage(), searchDto.getPerPage());
        }
        List<Event> events = this.eventRepository.findAllByCriteriaStartDateDesc(
            searchDto.getDuration(),
            searchDto.getStartTime(),
            searchDto.getEndTime(),
            searchDto.getDescription(),
            searchDto.getCategory(),
            searchDto.getStartDate(),
            searchDto.getEndDate(),
            searchDto.getStreet(),
            searchDto.getCity(),
            searchDto.getCountry(),
            searchDto.getPostalCode(),
            searchDto.getMinPrice(),
            searchDto.getMaxPrice(),
            pageable
        );
        log.debug("Found events: {}", events);
        return this.eventMapper.entityToListDto(events);
    }

    private List<EventDto> findAllByTitleDesc(EventSearchDto searchDto) {
        log.trace("Find ALL events by category ordered by their title {}", searchDto);
        Pageable pageable = Pageable.unpaged();
        if (searchDto.getPage() != null && searchDto.getPerPage() != null) {
            pageable = PageRequest.of(searchDto.getPage(), searchDto.getPerPage());
        }
        List<Event> events = this.eventRepository.findAllByTitleDesc(
            searchDto.getCategory(),
            pageable
        );
        log.debug("Found events: {}", events);
        return this.eventMapper.entityToListDto(events);
    }

    private List<EventDto> findAllByCriteriaTitleDesc(EventSearchDto searchDto) {
        log.trace("Find ALL events by additional criteria {} ordered by their title ", searchDto);
        Pageable pageable = Pageable.unpaged();
        if (searchDto.getPage() != null && searchDto.getPerPage() != null) {
            pageable = PageRequest.of(searchDto.getPage(), searchDto.getPerPage());
        }
        return this.eventMapper.entityToListDto(
            this.eventRepository.findAllByCriteriaTitleDesc(
                searchDto.getDuration(),
                searchDto.getStartTime(),
                searchDto.getEndTime(),
                searchDto.getDescription(),
                searchDto.getCategory(),
                searchDto.getStartDate(),
                searchDto.getEndDate(),
                searchDto.getStreet(),
                searchDto.getCity(),
                searchDto.getCountry(),
                searchDto.getPostalCode(),
                searchDto.getMinPrice(),
                searchDto.getMaxPrice(),
                pageable
            )
        );
    }

    @Override
    public EventDetailDto getById(long id) {
        log.trace("GET Event by id {}", id);
        Optional<Event> event = this.eventRepository.findById(id);
        if (event.isEmpty()) {
            throw new NotFoundException("Event not found");
        }
        EventDetailDto eventDetailDto = this.eventMapper.entityToDetailDto(event.get());
        eventDetailDto.setPerformances(this.performanceMapper.entityToListDto(this.performanceRepository.findByEvent_IdOrderByDateAsc(event.get().getId())));
        log.debug("{}", eventDetailDto);
        return eventDetailDto;
    }

    @Override
    @Transactional
    public EventCreateDetailDto create(EventCreateDto toCreate) throws ValidationException {
        log.trace("Create event {}", toCreate);

        eventValidator.validateForCreate(toCreate);

        Artist artist = this.artistService.saveByNameIfNotExists(toCreate.getArtistName());
        Picture picture = null;
        Event event = Event.builder()
            .title(toCreate.getTitle())
            .description(toCreate.getDescription())
            .startDate(LocalDate.parse(toCreate.getStartDate()))
            .endDate(LocalDate.parse(toCreate.getEndDate()))
            .durationInMinutes(toCreate.getDurationInMinutes())
            .eventCategory(toCreate.getEventCategory())
            .image(picture)
            .artist(artist)
            .build();

        log.debug("Saving event {}", event);
        entityManager.persist(event);
        entityManager.flush();

        String filename = "event" + event.getId() + ".jpg";
        picture = Picture.builder().name(filename).build();
        event.setImage(picture);
        try {
            pictureFileManager.savePictureByName(toCreate.getImage().getBytes(), filename);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new FileManagerException(e.getMessage(), e);
        }

        entityManager.merge(event);

        return eventMapper.eventToEventCreateEditDetailDto(event);
    }

}
