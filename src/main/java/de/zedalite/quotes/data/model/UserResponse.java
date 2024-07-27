package de.zedalite.quotes.data.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@JsonSerialize
@JsonDeserialize
public record UserResponse(
  @Schema(description = "Unique identifier", example = "5") @NotNull @PositiveOrZero Integer id,

  @Schema(description = "Visual presentation of the name", example = "The real Scott")
  @NotBlank
  @Size(max = 32)
  String displayName,

  @Schema(description = "Creation date", example = "2024-03-20T14:13:50.213Z")
  @NotNull
  @PastOrPresent
  LocalDateTime creationDate
) {}
