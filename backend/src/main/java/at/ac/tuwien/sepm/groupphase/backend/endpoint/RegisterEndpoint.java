package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedUserDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import javax.mail.MessagingException;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/registrations")
public class RegisterEndpoint {

    private final RegistrationService registrationService;

    @Autowired
    public RegisterEndpoint(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PermitAll
    @PostMapping
    @Operation(summary = "Sign up a User")
    public String register(@RequestBody DetailedUserDto userDto) throws ValidationException, ConflictException, MessagingException {
        log.info("POST /registrations {}", userDto);
        return registrationService.register(userDto);
    }

    @PermitAll
    @GetMapping(path = "confirmation")
    @ResponseStatus(HttpStatus.OK)
    public String confirm(String token) {
        log.info("GET " + "/confirmation?token=({})", token);
        return registrationService.confirmRegistration(token);
    }
}
