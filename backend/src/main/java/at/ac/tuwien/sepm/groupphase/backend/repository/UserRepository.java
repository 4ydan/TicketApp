package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {

    /**
     * Find user by email.
     *
     * @param email of a user
     * @return an application user
     */
    ApplicationUser findUserByEmail(String email);

    /**
     * Activate user.
     *
     * @param email of an user
     * @return id of an activated user
     */
    @Transactional
    @Modifying
    @Query("UPDATE users a " + "SET a.isActivated = TRUE WHERE a.email = ?1")
    int activate(String email);

    /**
     * Find an activated application user based on the email address.
     *
     * @param email the email address
     * @return a found application user
     */
    ApplicationUser findUserByEmailAndIsActivatedTrue(String email);

    /**
     * Find an activated customer based on the email address.
     *
     * @param email the email address
     * @return a found customer
     */
    @Query(
        "SELECT u FROM users u"
            + " WHERE u.email=:email AND u.isActivated=TRUE AND u.role=0"
    )
    ApplicationUser findUserByEmailAndIsActivatedTrueAndRoleIs0(@Param("email") String email);

    /**
     * Check if unactivated user based on the email address exists.
     *
     * @param email the email address
     * @return true if such user exists otherwise false
     */
    boolean existsByEmailAndIsActivatedIsFalse(String email);

    /**
     * Check if user based on the email address exists.
     *
     * @param email the email address
     * @return true if such user exists otherwise false
     */
    boolean existsByEmail(String email);

    /**
     * Receive a user by his identifier.
     *
     * @param id of the user
     */
    ApplicationUser getById(Long id);

    /**
     * Update user password.
     *
     * @param password to be updated
     * @param id of a user
     */
    @Transactional
    @Modifying
    @Query("UPDATE users a " + "SET a.password = ?1 WHERE a.id = ?2")
    void updatePassword(String password, long id);


}
