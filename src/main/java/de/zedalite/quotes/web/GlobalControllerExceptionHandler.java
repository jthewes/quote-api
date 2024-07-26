package de.zedalite.quotes.web;

import de.zedalite.quotes.data.model.ErrorResponse;
import de.zedalite.quotes.data.model.ValidationErrorDetails;
import de.zedalite.quotes.data.model.Violation;
import de.zedalite.quotes.exception.ResourceAccessException;
import de.zedalite.quotes.exception.ResourceAlreadyExitsException;
import de.zedalite.quotes.exception.ResourceNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * This class is a global controller exception handler for handling various exceptions thrown by controller methods.
 * <p>
 * It provides methods to handle different types of exceptions and return appropriate error details.
 */
@RestControllerAdvice
public class GlobalControllerExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFoundException(final ResourceNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
  }

  @ExceptionHandler({ ResourceAlreadyExitsException.class, ResourceAccessException.class })
  public ResponseEntity<ErrorResponse> handleForbiddenException(final RuntimeException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationErrorDetails> handleNotValidException(final MethodArgumentNotValidException ex) {
    final List<Violation> violations = new ArrayList<>();
    final ValidationErrorDetails details = new ValidationErrorDetails(violations);
    for (final FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      details
        .violations()
        .add(
          new Violation(
            fieldError.getField(),
            Objects.requireNonNullElse(fieldError.getDefaultMessage(), "Validation failed")
          )
        );
    }
    return ResponseEntity.badRequest().body(details);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ValidationErrorDetails> handleConstraintViolationException(
    final ConstraintViolationException ex
  ) {
    final List<Violation> violations = new ArrayList<>();
    for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
      violations.add(new Violation(violation.getPropertyPath().toString(), violation.getMessage()));
    }
    final ValidationErrorDetails details = new ValidationErrorDetails(violations);
    return ResponseEntity.badRequest().body(details);
  }

  @ExceptionHandler(AuthorizationDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException() {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("Access Denied"));
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponse> handleInternalServerException(final RuntimeException ex) {
    LOGGER.error("Internal Server Error", ex);
    return ResponseEntity.internalServerError().body(new ErrorResponse("Internal Server Error"));
  }
}
