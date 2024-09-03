package de.zedalite.quotes.data.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import org.springframework.lang.NonNull;

@JsonSerialize
@JsonDeserialize
public record QuoteOfTheDayRequest(
  @NonNull @PositiveOrZero Integer quoteId,

  @NonNull @PastOrPresent LocalDate creationDate
) {}
