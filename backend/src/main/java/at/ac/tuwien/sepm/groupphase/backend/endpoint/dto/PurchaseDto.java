package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.TicketToCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.merchandise.MerchandisePurchaseDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseDto {
    private List<MerchandisePurchaseDto> merchandises;
    private List<TicketToCreateDto> tickets;
}
