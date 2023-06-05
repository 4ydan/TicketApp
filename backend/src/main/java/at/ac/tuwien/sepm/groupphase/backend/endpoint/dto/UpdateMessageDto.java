package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Getter
@Setter
public class UpdateMessageDto {

    private Long id;
    private String title;
    private String shortDescription;
    private String description;
    private MultipartFile picture;

    public UpdateMessageDto(Long id, String title, String shortDescription, String description, MultipartFile picture) {
        this.id = id;
        this.title = title;
        this.shortDescription = shortDescription;
        this.description = description;
        this.picture = picture;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UpdateMessageDto that)) {
            return false;
        }
        return Objects.equals(id, that.id)
            && Objects.equals(title, that.title)
            && Objects.equals(shortDescription, that.shortDescription)
            && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, shortDescription, description);
    }

    @Override
    public String toString() {
        return "CreateMessageDto{"
            + ", id='" + id + '\''
            + ", title='" + title + '\''
            + ", shortDescription='" + shortDescription + '\''
            + ", description='" + description + '\''
            + '}';
    }
}
