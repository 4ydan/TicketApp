package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResetPasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.ConfirmationToken;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EmailService;

import at.ac.tuwien.sepm.groupphase.backend.service.PasswordResetService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final EmailService emailService;
    private final SpringTemplateEngine templateEngine;
    private final ConfirmationTokenServiceImpl confirmationTokenService;
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final TokenValidator tokenValidator;
    private final PasswordEncoder passwordEncoder;


    @Override
    public String requestPasswordReset(String email) {
        log.trace("Send password reset mail to {}", email);
        String token = UUID.randomUUID().toString();
        String link = "http://localhost:4200/#/password-reset?token=" + token;

        ApplicationUser appUser = userRepository.findUserByEmail(email);

        if (appUser != null) {
            ConfirmationToken confirmationToken = new ConfirmationToken(null, token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(30),
                null, appUser);
            confirmationTokenService.saveConfirmationToken(confirmationToken);
            emailService.sendPasswordReset(email, emailService.buildEmail(link));
        }

        return token;
    }

    @Transactional
    public String confirmAndUpdate(ResetPasswordDto dto) throws ValidationException {
        log.trace("confirm password update");
        ConfirmationToken confirmationToken =
            tokenValidator.validateTokenAndReturn(dto.getToken());

        userValidator.validateForPasswordReset(dto);

        confirmationTokenService.setConfirmedAt(confirmationToken.getToken());

        String encodedPw = passwordEncoder.encode(dto.getPassword());
        userRepository.updatePassword(encodedPw, confirmationToken.getUser().getId());

        return "Password update successful";
    }

}
