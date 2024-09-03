package de.zedalite.quotes.data.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@JsonSerialize
@JsonDeserialize
public record Violation(
  @Schema(description = "Field name", example = "name") @NotNull String field,

  @Schema(description = "Explanation", example = "size must be between 0 and 32") @NotNull String message
) {}
