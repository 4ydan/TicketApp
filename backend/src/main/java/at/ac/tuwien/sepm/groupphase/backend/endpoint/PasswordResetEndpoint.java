package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResetPasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/password-resets")
public class PasswordResetEndpoint {

    private final PasswordResetService passwordResetService;

    @Autowired
    public PasswordResetEndpoint(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @ResponseStatus(HttpStatus.OK)
    @PermitAll
    @PostMapping
    @Operation(summary = "Send email to reset password")
    public String requestPasswordReset(@RequestBody String email) {
        log.info("POST /password-resets {}", email);
        return passwordResetService.requestPasswordReset(email);
    }

    @PermitAll
    @PutMapping(path = "/updatePassword")
    @ResponseStatus(HttpStatus.OK)
    public String updatePassword(@RequestBody ResetPasswordDto dto) throws ValidationException {
        log.info("GET /updatePassword ({})", dto);
        return passwordResetService.confirmAndUpdate(dto);
    }
}

