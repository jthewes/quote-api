package de.zedalite.quotes.data.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@JsonSerialize
@JsonDeserialize
public record GroupMember(
  @Schema(description = "Group's unique identifier", example = "5") @NotNull Integer groupId,

  @Schema(description = "User's unique identifier", example = "5") @NotNull Integer userId,

  @Schema(description = "User's group display name", example = "Steeeeeve") String displayName
) {}
