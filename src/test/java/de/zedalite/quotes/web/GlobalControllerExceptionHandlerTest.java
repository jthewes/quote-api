package de.zedalite.quotes.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.zedalite.quotes.data.model.ErrorResponse;
import de.zedalite.quotes.data.model.ValidationErrorDetails;
import de.zedalite.quotes.data.model.Violation;
import de.zedalite.quotes.exception.ResourceAccessException;
import de.zedalite.quotes.exception.ResourceAlreadyExitsException;
import de.zedalite.quotes.exception.ResourceNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalControllerExceptionHandlerTest {

  private final GlobalControllerExceptionHandler instance = new GlobalControllerExceptionHandler();

  @Test
  @DisplayName("Should handle ResourceNotFoundException")
  void shouldHandleResourceNotFoundException() {
    final ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

    final ErrorResponse errorResponse = instance.handleNotFoundException(exception).getBody();

    assertThat(errorResponse).isNotNull();
    assertThat(errorResponse.message()).isEqualTo("Resource not found");
  }

  @ParameterizedTest
  @ValueSource(classes = { ResourceAlreadyExitsException.class, ResourceAccessException.class })
  @DisplayName("Should handle ResourceAccessException")
  void shouldHandleResourceAccessException(final Class<? extends RuntimeException> clazz) throws Exception {
    final RuntimeException exception = clazz
      .getDeclaredConstructor(String.class)
      .newInstance("Resource can't be accessed");

    final ErrorResponse errorResponse = instance.handleForbiddenException(exception).getBody();

    assertThat(errorResponse).isNotNull();
    assertThat(errorResponse.message()).isEqualTo("Resource can't be accessed");
  }

  @Test
  @DisplayName("Should handle ValidationException")
  void shouldHandleValidationException() {
    final MethodParameter methodParameter = mock(MethodParameter.class);
    final BindingResult bindingResult = mock(BindingResult.class);
    final FieldError fieldError = mock(FieldError.class);

    given(fieldError.getField()).willReturn("name");
    given(fieldError.getDefaultMessage()).willReturn("size must be between 0 and 32");
    given(bindingResult.getFieldErrors()).willReturn(List.of(fieldError));

    final MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
      methodParameter,
      bindingResult
    );

    final ValidationErrorDetails errorDetails = instance.handleNotValidException(exception).getBody();

    assertThat(errorDetails).isNotNull();
    assertThat(errorDetails.violations()).containsOnly(new Violation("name", "size must be between 0 and 32"));
  }

  @Test
  @DisplayName("Should handle ConstraintViolationException")
  void shouldHandleConstraintViolationException() {
    final ConstraintViolation<?> constraintViolation = mock(ConstraintViolation.class);
    final Path path = mock(Path.class);

    given(constraintViolation.getPropertyPath()).willReturn(path);
    given(constraintViolation.getPropertyPath().toString()).willReturn("joinGroup.code");
    given(constraintViolation.getMessage()).willReturn("size must be between 1 and 8");

    final ConstraintViolationException exception = new ConstraintViolationException(Set.of(constraintViolation));

    final ValidationErrorDetails errorDetails = instance.handleConstraintViolationException(exception).getBody();

    assertThat(errorDetails).isNotNull();
    assertThat(errorDetails.violations()).containsOnly(new Violation("joinGroup.code", "size must be between 1 and 8"));
  }

  @Test
  @DisplayName("Should handle authorization exception")
  void shouldHandleAuthorizationException() {
    final ErrorResponse errorResponse = instance.handleAuthorizationDeniedException().getBody();

    assertThat(errorResponse).isNotNull();
    assertThat(errorResponse.message()).isEqualTo("Access Denied");
  }

  @Test
  @DisplayName("Should handle unexpected RuntimeException")
  void shouldHandleUnexpectedRuntimeException() {
    final RuntimeException exception = new RuntimeException("Unexpected exception");

    final ErrorResponse errorResponse = instance.handleInternalServerException(exception).getBody();

    assertThat(errorResponse).isNotNull();
    assertThat(errorResponse.message()).isEqualTo("Internal Server Error");
  }
}
