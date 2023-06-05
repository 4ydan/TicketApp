package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event;

import at.ac.tuwien.sepm.groupphase.backend.type.EventCategory;
import at.ac.tuwien.sepm.groupphase.backend.type.EventSort;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class EventSearchDto {
    private final String term;
    private final EventCategory category;
    private final EventSort sort;
    private final Integer year;
    private final Integer month;
    private final Integer duration;
    @JsonFormat(pattern = "HH:mm")
    private final LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    private final LocalTime endTime;
    private final String description;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private final LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private final LocalDate endDate;
    private final String street;
    private final String city;
    private final String label;
    private final String country;
    private final Long postalCode;
    private final Double minPrice;
    private final Double maxPrice;
    private final Integer page;
    private final Integer perPage;
}
