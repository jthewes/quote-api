package de.zedalite.quotes.data.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@JsonSerialize
@JsonDeserialize
public record ErrorResponse(
  @Schema(description = "Reason for failing", example = "Validation failed") @NotNull String message,

  @Schema(description = "Details for failing", oneOf = ValidationErrorDetails.class) ErrorDetails details
) {
  public ErrorResponse(final String message) {
    this(message, null);
  }
}
