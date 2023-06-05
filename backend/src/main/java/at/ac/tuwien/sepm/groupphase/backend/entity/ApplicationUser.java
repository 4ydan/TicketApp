package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.type.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import javax.persistence.Column;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
public class ApplicationUser {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, length = 30)
    private String firstName;

    @Column(nullable = false, length = 30)
    private String lastName;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 60)
    @Size(min = 8)
    private String password;

    @Column(nullable = false)
    private short failedLogins;

    @Column(nullable = false)
    private Boolean isActivated;

    @Column(nullable = false)
    private Boolean isNotLocked;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(nullable = false)
    private UserRole role;

    @Column
    private Long createFrom;

    @Column
    private int bonusPoints;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "has_read",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "message_id"))
    List<Message> hasRead;
}
