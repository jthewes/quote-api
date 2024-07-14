package de.zedalite.quotes.data.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@JsonSerialize
@JsonDeserialize
public record GroupUserResponse(
  @Schema(description = "User") @NotNull UserResponse user,

  @Schema(description = "Visual presentation of the username in group", example = "KING OF THE WORLD")
  @Size(max = 32)
  @NotBlank
  String displayName
) {}
