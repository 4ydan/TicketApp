package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResetPasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

public interface PasswordResetService {

    /**
     * Request a password reset.
     *
     * @param email an email on which the reset password link will be send
     */
    String requestPasswordReset(String email);

    /**
     * Update password.
     *
     * @param dto new password
     */
    String confirmAndUpdate(ResetPasswordDto dto) throws ValidationException;
}
