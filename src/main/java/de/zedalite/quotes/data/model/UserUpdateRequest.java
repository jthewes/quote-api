package de.zedalite.quotes.data.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@JsonSerialize
@JsonDeserialize
public record UserUpdateRequest(
  @Schema(description = "Name", example = "scott") @Size(max = 32) String name,

  @Schema(description = "Email address", example = "me@scott.com") @Size(max = 64) String email,

  @Schema(description = "Visual presentation of the name", example = "The real Scott")
  @Size(max = 32)
  String displayName
) {}
