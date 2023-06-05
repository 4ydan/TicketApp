package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResetPasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Component
public class UserValidator {

    private UserRepository userRepository;

    /**
     * Validation of user objects.
     *
     * @param userRepository the userRepository for user.
     */
    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Validation for users that need to be saved in the database.
     *
     * @param detailedUserDto that is valided
     * @throws ValidationException if a Validation error occured
     * @throws ConflictException   if conflict in relation to the user occured
     */
    public void validateForAdd(DetailedUserDto detailedUserDto) throws ValidationException, ConflictException {
        log.trace("Validating user to add : ({})", detailedUserDto);
        List<String> validationErrors = new ArrayList<>();
        List<String> conflictErrors = new ArrayList<>();

        if (detailedUserDto.getFirstName() == null) {
            validationErrors.add("User first name is null");
        } else {
            if (detailedUserDto.getFirstName().isBlank()) {
                validationErrors.add("User first name is given but blank");
            }
            if (detailedUserDto.getFirstName().length() > 30) {
                validationErrors.add("User first name too long: longer than 30 characters");
            }
        }

        if (detailedUserDto.getLastName() == null) {
            validationErrors.add("User last name is null");
        } else {
            if (detailedUserDto.getLastName().isBlank()) {
                validationErrors.add("User last name is given but blank");
            }
            if (detailedUserDto.getLastName().length() > 30) {
                validationErrors.add("User last name too long: longer than 30 characters");
            }
        }

        if (detailedUserDto.getEmail() != null) {
            if (userRepository.findUserByEmailAndIsActivatedTrue(detailedUserDto.getEmail()) != null) {
                conflictErrors.add("User with email " + detailedUserDto.getEmail() + " already exists");
            }

            if (detailedUserDto.getEmail().isBlank()) {
                validationErrors.add("User email is given but blank");
            }
            if (detailedUserDto.getEmail().length() > 50) {
                validationErrors.add("User email too long: longer than 30 characters");
            }
            if (!Pattern.matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$", detailedUserDto.getEmail())) {
                validationErrors.add("Email has wrong format");
            }
        }

        if (detailedUserDto.getPassword() == null) {
            validationErrors.add("User password is null");
        } else {
            if (detailedUserDto.getPassword().isBlank()) {
                validationErrors.add("User password is given but blank");
            }
            if (detailedUserDto.getPassword().length() < 8) {
                validationErrors.add("User password is too short");
            }
            if (detailedUserDto.getPassword().length() > 30) {
                validationErrors.add("User last name too long: longer than 30 characters");
            }
        }

        if (detailedUserDto.getAddress().getCity() != null) {
            if (detailedUserDto.getAddress().getCity().length() > 30) {
                validationErrors.add("City of User too long: longer than 30 characters");
            }
        }

        if (detailedUserDto.getAddress().getStreet() != null) {
            if (detailedUserDto.getAddress().getStreet().length() > 50) {
                validationErrors.add("Street of User too long: longer than 50 characters");
            }
        }
        if (detailedUserDto.getAddress().getCountry() != null) {
            if (detailedUserDto.getAddress().getCountry().length() > 30) {
                validationErrors.add("Country of User too long: longer than 30 characters");
            }
        }
        if (detailedUserDto.getAddress().getPostalCode() != null) {
            if (detailedUserDto.getAddress().getPostalCode() > 9999999999L) {
                validationErrors.add("Postal Code of City for User too large: cant surpass 9999999999");
            }
        }
        if (detailedUserDto.getAddress().getStreetNr() != null) {
            if (detailedUserDto.getAddress().getStreetNr() > 9999999999L) {
                validationErrors.add("Street Number of Street for User too large: cant surpass 9999999999");
            }
        }
        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of user to add failed", validationErrors);
        }

        if (!conflictErrors.isEmpty()) {
            throw new ConflictException("User to add is conflicted", validationErrors);
        }
    }

    public void validateForPasswordReset(ResetPasswordDto dto) throws ValidationException {
        log.trace("Validating password reset");
        List<String> validationErrors = new ArrayList<>();

        if (dto.getPassword() == null || dto.getConfirmPassword() == null) {
            validationErrors.add("At least one password entry is null");
        } else {
            if (dto.getPassword().isBlank()) {
                validationErrors.add("Password is given but blank");
            }
            if (dto.getPassword().length() < 8) {
                validationErrors.add("Password is too short");
            }
            if (dto.getPassword().length() > 50) {
                validationErrors.add("Password too long: longer than 30 characters");
            }

            if (dto.getConfirmPassword().isBlank()) {
                validationErrors.add("Confirm password is given but blank");
            }
            if (dto.getConfirmPassword().length() < 8) {
                validationErrors.add("Confirm password is too short");
            }
            if (dto.getConfirmPassword().length() > 50) {
                validationErrors.add("Confirm password too long: longer than 30 characters");
            }

            if (!dto.getPassword().equals(dto.getConfirmPassword())) {
                validationErrors.add("Password entries differ");
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of user for password reset failed", validationErrors);
        }
    }
}
