package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.ConfirmationToken;
import java.util.Optional;

public interface ConfirmationTokenService {

    /**
     * Saves confirmation token.
     *
     * @param token to be saved
     */
    void saveConfirmationToken(ConfirmationToken token);

    /**
     * Get token.
     *
     * @param token to get
     */
    Optional<ConfirmationToken> getToken(String token);

    /**
     * Token confirmation.
     *
     * @param token to be confirmed
     */
    int setConfirmedAt(String token);

    /**
     * Delete confirmation token by user id.
     *
     * @param id user identifier
     */
    void deleteConfirmationTokensByUserId(Long id);

}
