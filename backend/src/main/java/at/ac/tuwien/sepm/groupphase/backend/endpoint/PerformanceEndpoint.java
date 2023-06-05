package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.PerformanceDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.PerformanceService;
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
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.PermitAll;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/performances")
public class PerformanceEndpoint {
    private final PerformanceService performanceService;

    @Autowired
    public PerformanceEndpoint(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @ResponseStatus(HttpStatus.OK)
    @PermitAll
    @GetMapping("{id}")
    @Operation(summary = "Get one performance with detailed info", security = @SecurityRequirement(name = "apiKey"))
    public PerformanceDto getById(@PathVariable long id) {
        log.info("GET /api/v1/performances/" + id);
        try {
            return performanceService.getById(id);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @Operation(summary = "Create a new performance", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<PerformanceDto> create(
        @RequestParam("eventId") Long eventId,
        @RequestParam("name") String name,
        @RequestParam("country") String country,
        @RequestParam("city") String city,
        @RequestParam("postalCode") Long postalCode,
        @RequestParam("street") String street,
        @RequestParam("streetNr") Long streetNr,
        @RequestParam("date") String date,
        @RequestParam("price") Double price,
        @RequestParam("startTime") String startTime,
        @RequestParam("hallPlanId") Long hallPlanId) {
        PerformanceDto toCreate = PerformanceDto.builder()
            .name(name.trim())
            .country(country.trim())
            .city(city.trim())
            .postalCode(postalCode)
            .street(street.trim())
            .streetNr(streetNr)
            .date(date.trim())
            .price(price)
            .startTime(startTime)
            .build();
        log.info("POST /api/v1/performances/ " + name);
        try {
            return new ResponseEntity<>(performanceService.create(toCreate, eventId, hallPlanId), HttpStatus.CREATED);
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        }
    }
}