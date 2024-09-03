package de.zedalite.quotes.data.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@JsonSerialize
@JsonDeserialize
public record GroupInviteRequest(
  @Schema(description = "Unique invite code to join the group", example = "bestcode")
  @NotNull
  @Size(min = 1, max = 8)
  String inviteCode,

  @Schema(description = "Visual presentation of the username in group", example = "KING OF THE WORLD")
  @Size(max = 32)
  String userDisplayName
) {}
