package de.zedalite.quotes.data.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Optional;

@JsonSerialize
@JsonDeserialize
public record Group(
  @Schema(description = "Unique identifier", example = "1") @NotNull @PositiveOrZero Integer id,

  @Schema(description = "Unique invite code", example = "vjh345jg") @NotBlank @Size(max = 8) String inviteCode,

  @Schema(description = "Visual presentation of the name", example = "The Car Lovers")
  @NotBlank
  @Size(max = 32)
  String displayName,

  @Schema(description = "Creation date", example = "2024-03-20T09:09:50.213Z")
  @NotNull
  @PastOrPresent
  LocalDateTime creationDate,

  @Schema(description = "Creator's unique identifier", example = "5") @PositiveOrZero Optional<Integer> creatorId
) {}
