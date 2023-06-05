package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("message")
public class Message extends ContentItem {

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "has_read",
        joinColumns = @JoinColumn(name = "message_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    List<ApplicationUser> readBy;

    @Builder
    public Message(Long id, LocalDateTime publishedAt, String title, String shortDescription, String description, Picture picture) {
        super(id, publishedAt, title, shortDescription, description, picture);
    }
}