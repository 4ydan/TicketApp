package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChangePasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserSessionDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.PermitAll;
import java.util.List;


@Slf4j
@RestController
@RequestMapping(value = "/api/v1/users")
public class UserEndpoint {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserEndpoint(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PermitAll
    @GetMapping(value = "/activated/{email}")
    @Operation(summary = "Get the activated user with the specified email ")
    public DetailedUserDto getActivatedUserByEmail(@PathVariable String email) {
        log.info("GET /activated/{}", email);
        return userMapper.applicationUserToDetailedUserDto(userService.findActivatedApplicationUserByEmail(email));
    }

    @PermitAll
    @GetMapping(value = "/activated/customers/{email}")
    @Operation(summary = "Get the activated customer user with the specified email ")
    public DetailedUserDto getActivatedCustomerByEmail(@PathVariable String email) {
        log.info("GET /activated/customers/{}", email);
        return userMapper.applicationUserToDetailedUserDto(userService.findActivatedCustomerByEmail(email));
    }

    @PermitAll
    @GetMapping(value = "/{email}")
    @Operation(summary = "Get the user with the specified email ")
    public UserSessionDto getUserByEmail(@PathVariable String email) {
        UserSessionDto dto = userMapper.applicationUserToUserSessionDto(userService.findApplicationUserByEmail(email));
        log.info(String.valueOf(dto));
        return dto;
    }

    @PermitAll
    @GetMapping
    @Operation(summary = "Get all users", security = @SecurityRequirement(name = "apiKey"))
    public List<DetailedUserDto> getAllUsers() {
        log.info("Get all users");
        return userService.getAllUsers();
    }


    @PermitAll
    @DeleteMapping(value = "/{email}")
    @Operation(summary = "Delete a user", security = @SecurityRequirement(name = "apiKey"))
    public void deleteUser(@PathVariable String email) {
        log.info("Delete a user with email {}", email);
        try {
            userService.deleteUser(email);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found with that email.");
        }
    }

    @PermitAll
    @PutMapping(value = "")
    @Operation(summary = "Update a user", security = @SecurityRequirement(name = "apiKey"))
    public DetailedUserDto updateUser(@RequestBody DetailedUserDto detailedUserDto) {
        log.info("Update a user {}", detailedUserDto);
        return userMapper.applicationUserToDetailedUserDto(userService.editUser(detailedUserDto));
    }

    @PermitAll
    @PutMapping("/changePassword")
    @Operation(summary = "Change a password", security = @SecurityRequirement(name = "apiKey"))
    public void changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        log.info("Change Password {}", changePasswordDto);
        userService.changePassword(changePasswordDto);
    }

    @PermitAll
    @PutMapping(value = "/{email}")
    @Operation(summary = "Block or unblock user", security = @SecurityRequirement(name = "apiKey"))
    public void blockOrUnblockUser(@PathVariable String email, @Parameter(required = true) Boolean block) {
        log.info(" Block or unblock user with email {}", email);
        if (block) {
            log.info("Block user {}", email);
            userService.blockUser(email);
        } else {
            log.info("Unblock user {}", email);
            userService.unblockUser(email);
        }
    }
}
