package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.merchandise;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class MerchandisePurchaseDto {

    private Long id;

    private Integer amount;
}
