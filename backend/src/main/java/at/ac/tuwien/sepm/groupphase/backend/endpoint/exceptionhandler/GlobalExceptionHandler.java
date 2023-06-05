package at.ac.tuwien.sepm.groupphase.backend.endpoint.exceptionhandler;

import at.ac.tuwien.sepm.groupphase.backend.exception.AccountLockedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ErrorListException;
import at.ac.tuwien.sepm.groupphase.backend.exception.FileManagerException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.PasswordMatchException;
import at.ac.tuwien.sepm.groupphase.backend.exception.TokenConfirmationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Register all your Java exceptions here to map them into meaningful HTTP exceptions
 * If you have special cases which are only important for specific endpoints, use ResponseStatusExceptions
 * https://www.baeldung.com/exception-handling-for-rest-with-spring#responsestatusexception
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Use the @ExceptionHandler annotation to write handler for custom exceptions.
     */
    @ExceptionHandler(value = {NotFoundException.class})
    protected ResponseEntity<Object> handleNotFound(RuntimeException ex, WebRequest request) {
        log.warn(ex.getMessage(), ex);
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {FileManagerException.class})
    protected ResponseEntity<Object> handleFileManagerException(RuntimeException ex, WebRequest request) {
        log.warn("Terminating request processing with status 500 due to {}: {}\n{}",
            ex.getClass().getSimpleName(), ex.getMessage(), ex.getStackTrace());
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = {BadCredentialsException.class})
    protected ResponseEntity<Object> handleBadCredentialsException(RuntimeException ex, WebRequest request) {
        log.warn(ex.getMessage());
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(value = {PasswordMatchException.class})
    protected ResponseEntity<Object> handlePasswordMatchException(RuntimeException ex, WebRequest request) {
        log.warn("Wrong password {}: {}\n{}",
            ex.getClass().getSimpleName(), ex.getMessage(), ex.getStackTrace());
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(value = {MessagingException.class})
    protected ResponseEntity<Object> handleMessagingException(RuntimeException ex, WebRequest request) {
        log.warn("Error sending mail due to {}: {}\n{}",
            ex.getClass().getSimpleName(), ex.getMessage(), ex.getStackTrace());
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = {ValidationException.class})
    protected ResponseEntity<Object> handleValidationException(ErrorListException ex, WebRequest request) {
        log.warn("Validation failed due to {}: {}\n{}",
            ex.getClass().getSimpleName(), ex.getMessage(), ex.getStackTrace());
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler(value = {ConflictException.class})
    protected ResponseEntity<Object> handleConflictException(ErrorListException ex, WebRequest request) {
        log.warn("Validated data conflicted due to {}: {}\n{}",
            ex.getClass().getSimpleName(), ex.getMessage(), ex.getStackTrace());
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(value = {TokenConfirmationException.class})
    protected ResponseEntity<Object> handleTokenConfirmationException(RuntimeException ex, WebRequest request) {
        log.warn("Token no longer viable due to {}: {}\n{}",
            ex.getClass().getSimpleName(), ex.getMessage(), ex.getStackTrace());
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.GONE, request);
    }

    @ExceptionHandler(value = {AccountLockedException.class})
    protected ResponseEntity<Object> handleAccountLockedException(RuntimeException ex, WebRequest request) {
        log.warn("The action cannot be performed because the account is locked {}: {}\n{}",
            ex.getClass().getSimpleName(), ex.getMessage(), ex.getStackTrace());
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.LOCKED, request);
    }

    /**
     * Override methods from ResponseEntityExceptionHandler to send a customized HTTP response for a know exception
     * from e.g. Spring
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        //Get all errors
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(err -> err.getField() + " " + err.getDefaultMessage())
            .collect(Collectors.toList());
        body.put("Validation errors", errors);

        return new ResponseEntity<>(body.toString(), headers, status);
    }
}
