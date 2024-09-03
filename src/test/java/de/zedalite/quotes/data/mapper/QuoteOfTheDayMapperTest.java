package de.zedalite.quotes.data.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.zedalite.quotes.data.jooq.quotes.tables.records.QuotesOfTheDayRecord;
import de.zedalite.quotes.data.model.QuoteOfTheDay;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

class QuoteOfTheDayMapperTest {

  private static final QuoteOfTheDayMapper instance = QuoteOfTheDayMapper.INSTANCE;

  @Test
  @DisplayName("Should map quoteOfTheDayRecord to quoteOfTheDay")
  void shouldMapQuoteOfTheDayRecordToQuoteOfTheDay() {
    final QuotesOfTheDayRecord qotdRec = new QuotesOfTheDayRecord(0, 0, LocalDate.MIN, null);

    final QuoteOfTheDay qotd = instance.mapToQuoteOfTheDay(qotdRec);

    assertThat(qotd).isNotNull();
    assertThat(qotd.id()).isZero();
    assertThat(qotd.quoteId()).isZero();
    assertThat(qotd.creationDate()).isEqualTo(LocalDate.MIN);
  }

  @ParameterizedTest
  @DisplayName("Should map empty quoteOfTheDayRecord to null")
  @NullSource
  void shouldMapEmptyQuoteOfTheDayRecordToNull(final QuotesOfTheDayRecord qotdRecord) {
    final QuoteOfTheDay qotd = instance.mapToQuoteOfTheDay(qotdRecord);

    assertThat(qotd).isNull();
  }
}
