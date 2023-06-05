package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.ConfirmationToken;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;

import at.ac.tuwien.sepm.groupphase.backend.service.EmailService;
import at.ac.tuwien.sepm.groupphase.backend.service.RegistrationService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;
    private final TokenValidator tokenValidator;
    private final UserMapper userMapper;
    private final ConfirmationTokenServiceImpl confirmationTokenService;
    private final UserService userService;
    private final EmailService emailService;
    private final SpringTemplateEngine templateEngine;

    @Override
    public String register(DetailedUserDto request) throws ValidationException, ConflictException {
        log.trace("Storing user in the database and send confirmation mail");
        String token = null;
        if (request.getCreateFrom() != 1) {
            token = createUser(request);
            String link = "http://localhost:4200/#/confirm?token=" + token;
            emailService.sendRegistrationConfirm(request.getEmail(), buildVerificationEmail(request, link));
        } else {
            createUserByAdmin(request);
        }
        return token;
    }

    @Override
    public String createUser(DetailedUserDto user) throws ValidationException, ConflictException {
        log.trace("Create User {}", user);
        String encodedPw;
        userValidator.validateForAdd(user);
        encodedPw = passwordEncoder.encode(user.getPassword());

        user.setPassword(encodedPw);
        ApplicationUser appUser = userMapper.detailedUserDtoToApplicationUser(user);
        appUser.setIsNotLocked(true);
        if (userRepository.existsByEmailAndIsActivatedIsFalse(appUser.getEmail())) {
            ApplicationUser old = userRepository.findUserByEmail(appUser.getEmail());
            appUser.setId(old.getId());
            confirmationTokenService.deleteConfirmationTokensByUserId(appUser.getId());
        }
        userRepository.save(appUser);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(null, token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), null, appUser);
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        return token;
    }

    @Override
    public void createUserByAdmin(DetailedUserDto user) {
        log.trace("Create User by Admin{}", user);
        String newPassword = UUID.randomUUID().toString().substring(0, 12);
        String encodedPw = passwordEncoder.encode(newPassword);
        emailService.sendUserPassword(user.getEmail(), emailService.buildCreatedEmail(newPassword));

        user.setPassword(encodedPw);
        ApplicationUser appUser = userMapper.detailedUserDtoToApplicationUser(user);
        appUser.setIsNotLocked(true);
        appUser.setIsActivated(true);
        userRepository.save(appUser);
    }

    @Transactional
    public String confirmRegistration(String token) {
        log.trace("Confirm token ({})", token);
        ConfirmationToken confirmationToken = tokenValidator.validateTokenAndReturn(token);

        confirmationTokenService.setConfirmedAt(token);
        userService.activateUser(confirmationToken.getUser().getEmail());

        return "Confirmation successful, please log in";
    }

    private String buildVerificationEmail(DetailedUserDto dto, String link) {
        log.trace("Building confirmation mail");
        final Context ctx = new Context();
        ctx.setVariable("name", dto.getFirstName());
        ctx.setVariable("link", link);

        return this.templateEngine.process("verificationEmail.html", ctx);
    }

}
