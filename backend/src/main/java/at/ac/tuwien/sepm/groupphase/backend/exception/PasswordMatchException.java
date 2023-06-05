package at.ac.tuwien.sepm.groupphase.backend.exception;

public class PasswordMatchException extends RuntimeException {
    public PasswordMatchException(String message) {
        super(message);
    }
}
