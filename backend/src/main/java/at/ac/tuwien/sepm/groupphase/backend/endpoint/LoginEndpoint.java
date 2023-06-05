package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import javax.annotation.security.PermitAll;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/authentications")
public class LoginEndpoint {

    private final UserService userService;

    public LoginEndpoint(UserService userService) {
        this.userService = userService;
    }

    @ResponseStatus(HttpStatus.OK)
    @PermitAll
    @PostMapping
    public String login(@RequestBody UserLoginDto userLoginDto) {
        log.info("Logging user with email : " + userLoginDto.getEmail());
        return userService.login(userLoginDto);
    }
}
