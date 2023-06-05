package at.ac.tuwien.sepm.groupphase.backend.service;

public interface EmailService {

    /**
     * Send a verification email to the user.
     *
     * @param to user
     * @param email verification email
     */
    void sendRegistrationConfirm(String to, String email);

    /**
     * Send password reset mail to the user.
     *
     * @param to user
     * @param email reset email
     */
    void sendPasswordReset(String to, String email);

    /**
     * Send a password to the user.
     *
     * @param to user
     * @param email from which the password is sent
     */
    void sendUserPassword(String to, String email);

    /**
     * Builds new email.
     */
    String buildEmail(String link);

    /**
     * Building user creation email.
     */
    String buildCreatedEmail(String password);

}
