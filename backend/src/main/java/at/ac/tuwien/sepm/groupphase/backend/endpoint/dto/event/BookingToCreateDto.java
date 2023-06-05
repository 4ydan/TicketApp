package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.NonNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class BookingToCreateDto {
    @NonNull
    private Boolean isPaid;
    @NonNull
    private TicketToCreateDto[] tickets;
    @NonNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;
    @NonNull
    private Long userId;
}
