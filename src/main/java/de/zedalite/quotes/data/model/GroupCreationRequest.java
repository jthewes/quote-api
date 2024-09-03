package de.zedalite.quotes.data.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonSerialize
@JsonDeserialize
public record GroupCreationRequest(
  @Schema(description = "Visual presentation of the name", example = "The Car Lovers")
  @NotBlank
  @Size(max = 32)
  String displayName,

  @Schema(description = "Unique invite code to join the group", example = "bestcode") @Size(max = 8) String inviteCode,

  @Schema(description = "Visual presentation of the username in group", example = "KING OF THE WORLD")
  @Size(max = 32)
  String userDisplayName
) {}
