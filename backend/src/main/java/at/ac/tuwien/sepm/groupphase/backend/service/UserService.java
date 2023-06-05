package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChangePasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;

import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService extends UserDetailsService {

    /**
     * Find a user in the context of Spring Security based on the email address.
     *
     * @param email the email address
     * @return a Spring Security user
     * @throws UsernameNotFoundException is thrown if the specified user does not exists
     */
    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    /**
     * Find an activated application user based on the email address.
     *
     * @param email the email address
     * @return a found application user
     */
    ApplicationUser findActivatedApplicationUserByEmail(String email);

    /**
     * Find an application user based on the email address.
     *
     * @param email the email address
     * @return a found application user
     */
    ApplicationUser findApplicationUserByEmail(String email);

    /**
     * Find an activated customer based on the email address.
     *
     * @param email the email address
     * @return a found customer user
     */
    ApplicationUser findActivatedCustomerByEmail(String email);

    /**
     * Log in a user.
     *
     * @param userLoginDto login credentials
     * @return the JWT, if successful
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are bad
     */
    String login(UserLoginDto userLoginDto);

    /**
     * Activates a user with the given email.
     *
     * @param email the email address
     * @return id of the activated user
     */
    int activateUser(String email);

    /**
     * Signs in a user.
     *
     * @param user new user.
     */
    DetailedUserDto createUser(DetailedUserDto user) throws ValidationException, ConflictException;

    /**
     * Delete user.
     *
     * @param email of the user.
     */
    void deleteUser(String email);

    /**
     * Update user.
     *
     * @param detailedUserDto dto of the user
     * @return updated user
     */
    ApplicationUser editUser(DetailedUserDto detailedUserDto);

    /**
     * Change password.
     *
     * @param changePasswordDto dto of password
     */
    void changePassword(ChangePasswordDto changePasswordDto);

    /**
     * Get all users.
     *
     * @return list of all users
     */
    List<DetailedUserDto> getAllUsers();


    /**
     * Block user.
     *
     * @param email of the user that will be locked
     */
    void blockUser(String email);

    /**
     * Unblock user.
     *
     * @param email of the user that will be unblocked
     */
    void unblockUser(String email);

    /**
     * Lock user.
     *
     * @param user that is locked
     */

    void lockUser(ApplicationUser user);
}
