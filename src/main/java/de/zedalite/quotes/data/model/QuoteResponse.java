package de.zedalite.quotes.data.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@JsonSerialize
@JsonDeserialize
public record QuoteResponse(
  @Schema(description = "quote") @NotNull Quote quote,

  @Schema(description = "mentioned user") List<UserResponse> mentions
) {}
