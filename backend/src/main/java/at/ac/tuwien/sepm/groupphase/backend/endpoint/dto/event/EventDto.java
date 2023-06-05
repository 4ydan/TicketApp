package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event;

import at.ac.tuwien.sepm.groupphase.backend.type.EventCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Data
@Builder
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
    private Long id;
    private String title;
    private byte[] image;
    private String description;
    private EventCategory eventCategory;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer durationInMinutes;
    private Integer amountOfPerformances;
    private Long soldTickets;
    private Long availableTickets;
}
