package at.ac.tuwien.sepm.groupphase.backend.exception;

public class AccountLockedException extends RuntimeException {

    public AccountLockedException(String message) {
        super(message);
    }
}
