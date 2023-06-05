package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event;

import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class BookingDto {

    @NonNull
    private Long id;

    @NonNull
    private Boolean isPaid;

    @NonNull
    private Boolean isCanceled;

    @NonNull
    private List<Ticket> tickets;

    @NonNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    @NonNull
    private Long userId;
}