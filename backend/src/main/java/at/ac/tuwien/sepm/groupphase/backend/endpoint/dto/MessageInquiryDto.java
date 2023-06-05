package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@Data
public class MessageInquiryDto {

    @NotNull(message = "Title must not be null")
    @Size(max = 100)
    private String title;

    @NotNull(message = "Summary must not be null")
    @Size(max = 500)
    private String summary;

    @NotNull(message = "Text must not be null")
    @Size(max = 10000)
    private String text;
}