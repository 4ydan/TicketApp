package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedUserDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import javax.mail.MessagingException;

public interface RegistrationService {

    /**
     * Signs in a user.
     *
     * @param user new user
     */
    String createUser(DetailedUserDto user) throws ValidationException, ConflictException;

    /**
     * Confirm registration.
     *
     * @param token confirmation token
     */
    String confirmRegistration(String token);

    /**
     * Signs up new user.
     *
     * @param request user to be registered
     */
    String register(DetailedUserDto request) throws ValidationException, ConflictException, MessagingException;

    /**
     * Admin creates new user.
     *
     * @param user new user to be created
     */
    void createUserByAdmin(DetailedUserDto user);
}
