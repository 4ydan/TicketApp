package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketDto {
    private @NonNull Long id;
    private @NonNull int price;
}
