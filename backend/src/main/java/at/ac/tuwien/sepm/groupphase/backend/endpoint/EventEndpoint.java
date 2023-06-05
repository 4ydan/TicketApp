package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventCreateDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.EventService;
import at.ac.tuwien.sepm.groupphase.backend.type.EventCategory;
import at.ac.tuwien.sepm.groupphase.backend.type.EventSort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.PermitAll;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/events")
public class EventEndpoint {
    private final EventService eventService;

    @Autowired
    public EventEndpoint(EventService eventService) {
        this.eventService = eventService;
    }

    @ResponseStatus(HttpStatus.OK)
    @PermitAll
    @GetMapping
    @Operation(summary = "Get a list of all events matching the search criteria", security = @SecurityRequirement(name = "apiKey"))
    public List<EventDto> findAllBy(EventSearchDto searchDto) {
        log.info("GET /api/v1/events");
        if (searchDto.getTerm() != null) {
            return eventService.findAllByTerm(searchDto);
        } else if (searchDto.getSort() == EventSort.TOPTEN) {
            return eventService.findTopTen(searchDto);
        } else if (searchDto.getSort() == EventSort.ALPHA) {
            return eventService.findAllByTitle(searchDto);
        } else if (searchDto.getSort() == EventSort.DATE) {
            return eventService.findAllByDate(searchDto);
        }
        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Unknown Sort");

    }

    @ResponseStatus(HttpStatus.OK)
    @PermitAll
    @GetMapping("{id}")
    @Operation(summary = "Get one event with detailed info", security = @SecurityRequirement(name = "apiKey"))
    public EventDetailDto getById(@PathVariable long id) {
        log.info("GET /api/v1/events/{}", id);
        return eventService.getById(id);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @Operation(summary = "Create a new event", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<EventCreateDetailDto> create(
        @RequestParam("title") String title,
        @RequestParam("description") String description,
        @RequestParam("eventCategory") EventCategory eventCategory,
        @RequestParam("startDate") String startDate,
        @RequestParam("endDate") String endDate,
        @RequestParam("durationInMinutes") Integer durationInMinutes,
        @RequestParam("artist") String artist,
        @RequestParam("image") MultipartFile image) throws IOException {
        EventCreateDto toCreate = EventCreateDto.builder()
            .title(title.trim())
            .image(image)
            .description(description.trim())
            .eventCategory(eventCategory)
            .startDate(startDate)
            .endDate(endDate)
            .durationInMinutes(durationInMinutes)
            .artistName(artist.trim())
            .build();
        log.info("POST /api/v1/events/ " + title + description);
        try {
            return new ResponseEntity<>(eventService.create(toCreate), HttpStatus.CREATED);
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        }
    }
}
