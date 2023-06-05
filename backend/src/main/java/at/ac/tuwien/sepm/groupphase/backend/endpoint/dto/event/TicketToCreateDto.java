package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event;

import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class TicketToCreateDto {
    @NonNull
    private Long performanceId;
    @NonNull
    private Double price;
    private SeatDto seat;
    @NonNull
    private Boolean isStandingTicket;
}
