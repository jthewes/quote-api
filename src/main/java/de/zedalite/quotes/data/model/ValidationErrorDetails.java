package de.zedalite.quotes.data.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@JsonSerialize
@JsonDeserialize
public record ValidationErrorDetails(
  @Schema(description = "List of validation violations") @NotEmpty List<Violation> violations
)
  implements ErrorDetails {}
