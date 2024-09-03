package de.zedalite.quotes.data.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Optional;

@JsonSerialize
@JsonDeserialize
public record Quote(
  @Schema(description = "Unique identifier", example = "3") @NotNull @PositiveOrZero Integer id,

  @Schema(description = "Person who said the quote", example = "Scott") @NotBlank @Size(max = 32) String author,

  @Schema(description = "Creation date", example = "2024-03-20T09:09:50.213Z")
  @NotNull
  @PastOrPresent
  LocalDateTime creationDate,

  @Schema(description = "What the person said", example = "The universe is so extraordinary")
  @NotBlank
  @Size(max = 256)
  String text,

  @Schema(description = "In what context it was said", example = "At the press conference")
  @Size(max = 64)
  String context,

  @Schema(description = "Creator's unique identifier", example = "5") @PositiveOrZero Optional<Integer> creatorId
) {}
