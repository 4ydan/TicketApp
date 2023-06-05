package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

    /**
     * Get the token by the specified token string.
     *
     * @param token the specified token
     */
    Optional<ConfirmationToken> findByToken(String token);

    /**
     * Update the confirmation date of the token.
     *
     * @param token       the specified token
     * @param confirmedAt the confirmation date
     */
    @Transactional
    @Modifying
    @Query("UPDATE tokens t " + "SET t.confirmedAt = ?2 " + "WHERE t.token = ?1")
    int updateConfirmedAt(String token, LocalDateTime confirmedAt);

    /**
     * Delete all tokens by user.
     *
     * @param id of the user
     */
    @Modifying
    @Query("delete from tokens t where t.user.id = ?1")
    void deleteAllByUser_Id(Long id);


    /**
     * Get the confirmation token by the user id.
     *
     * @param user id of the user
     */
    ConfirmationToken findConfirmationTokenByUser_Id(Long user);
}