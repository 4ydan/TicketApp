package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.ConfirmationToken;
import at.ac.tuwien.sepm.groupphase.backend.exception.TokenConfirmationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class TokenValidator {

    private final ConfirmationTokenServiceImpl confirmationTokenService;

    public TokenValidator(ConfirmationTokenServiceImpl confirmationTokenService) {
        this.confirmationTokenService = confirmationTokenService;
    }

    public ConfirmationToken validateTokenAndReturn(String token) {

        log.trace("Validating token ({})", token);
        ConfirmationToken confirmationToken = confirmationTokenService
            .getToken(token)
            .orElseThrow(() ->
                new TokenConfirmationException("invalid link"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new TokenConfirmationException("link already used for password change");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new TokenConfirmationException("link expired");
        }
        return confirmationToken;
    }
}
