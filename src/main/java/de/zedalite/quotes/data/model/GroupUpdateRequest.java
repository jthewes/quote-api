package de.zedalite.quotes.data.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@JsonSerialize
@JsonDeserialize
public record GroupUpdateRequest(
  @Schema(description = "Group's display name", example = "Awesome Name") @Size(max = 32) String displayName,

  @Schema(description = "Group's invite code", example = "bestcode") @Size(max = 8) String inviteCode
) {}
