package de.zedalite.quotes.data.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;

@JsonSerialize
@JsonDeserialize
public record GroupResponse(
  @Schema(description = "Group") @NotNull Group group,

  @Schema(description = "Creator") Optional<UserResponse> creator
) {}
