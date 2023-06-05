package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.merchandise;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class MerchandiseRedeemDto {

    @NonNull
    private Long userId;

    @NonNull
    private Long itemId;

    @NonNull
    private Integer amount;
}