package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tokens")
public class ConfirmationToken {

    @SequenceGenerator(
        name = "confirmation_token_sequence",
        sequenceName = "confirmation_token_sequence",
        allocationSize = 1
    )
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "confirmation_token_sequence"
    )
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime confirmedAt;

    @ManyToOne(targetEntity = ApplicationUser.class)
    @JoinColumn(
        nullable = false,
        name = "app_user_id"
    )
    private ApplicationUser user;

}