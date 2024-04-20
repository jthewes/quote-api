package de.zedalite.quotes.data.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonSerialize
@JsonDeserialize
public record GroupRequest(
  @Schema(description = "Unique name", example = "carlovers") @NotBlank @Size(max = 32) String name,

  @Schema(description = "Visual presentation of the name", example = "The Car Lovers")
  @NotBlank
  @Size(max = 32)
  String displayName
) {}
