package de.zedalite.quotes.data.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.zedalite.quotes.data.jooq.quotes.tables.records.QuotesRecord;
import de.zedalite.quotes.data.model.Quote;
import de.zedalite.quotes.data.model.QuoteResponse;
import de.zedalite.quotes.data.model.User;
import de.zedalite.quotes.fixtures.QuoteGenerator;
import de.zedalite.quotes.fixtures.UserGenerator;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

class QuoteMapperTest {

  private static final QuoteMapper instance = QuoteMapper.INSTANCE;

  @Test
  @DisplayName("Should map quoteRecord to quote")
  void shouldMapQuoteRecordToQuote() {
    final QuotesRecord quoteRec = new QuotesRecord(0, "test", LocalDateTime.MIN, "Successful test.", "sub", 2);

    final Quote quote = instance.mapToQuote(quoteRec);

    assertThat(quote).isNotNull();
    assertThat(quote.id()).isZero();
    assertThat(quote.author()).isEqualTo("test");
    assertThat(quote.creationDate()).isEqualTo(LocalDateTime.MIN);
    assertThat(quote.text()).isEqualTo("Successful test.");
    assertThat(quote.context()).isEqualTo("sub");
    assertThat(quote.creatorId()).isEqualTo(Optional.of(2));
  }

  @ParameterizedTest
  @DisplayName("Should map empty quoteRecord to null")
  @NullSource
  void shouldMapEmptyQuoteRecordToNull(final QuotesRecord quotesRecord) {
    final Quote quote = instance.mapToQuote(quotesRecord);

    assertThat(quote).isNull();
  }

  @Test
  @DisplayName("Should map quoteRecords to quotes")
  void shouldMapQuoteRecordsToQuotes() {
    final QuotesRecord quoteRecSonar = new QuotesRecord(
      0,
      "sonar",
      LocalDateTime.MIN,
      "I like code coverage.",
      "sub",
      3
    );
    final QuotesRecord quoteRecMapper = new QuotesRecord(
      1,
      "mapper",
      LocalDateTime.MAX,
      "Mappers facilitate the work.",
      null,
      2
    );
    final List<QuotesRecord> quotesRecords = List.of(quoteRecSonar, quoteRecMapper);

    final List<Quote> quotes = instance.mapToQuoteList(quotesRecords);

    final Quote quoteSonar = quotes.get(0);
    final Quote quoteMapper = quotes.get(1);

    final SoftAssertions softly = new SoftAssertions();

    softly.assertThat(quoteSonar).isNotNull();
    softly.assertThat(quoteSonar.id()).isZero();
    softly.assertThat(quoteSonar.author()).isEqualTo("sonar");
    softly.assertThat(quoteSonar.creationDate()).isEqualTo(LocalDateTime.MIN);
    softly.assertThat(quoteSonar.text()).isEqualTo("I like code coverage.");
    softly.assertThat(quoteSonar.context()).isEqualTo("sub");
    softly.assertThat(quoteSonar.creatorId()).isEqualTo(Optional.of(3));

    softly.assertThat(quoteMapper).isNotNull();
    softly.assertThat(quoteMapper.id()).isEqualTo(1);
    softly.assertThat(quoteMapper.author()).isEqualTo("mapper");
    softly.assertThat(quoteMapper.creationDate()).isEqualTo(LocalDateTime.MAX);
    softly.assertThat(quoteMapper.text()).isEqualTo("Mappers facilitate the work.");
    softly.assertThat(quoteMapper.context()).isNull();
    softly.assertThat(quoteMapper.creatorId()).isEqualTo(Optional.of(2));

    softly.assertAll();
  }

  @ParameterizedTest
  @DisplayName("Should map empty quoteRecords to null")
  @NullSource
  void shouldMapEmptyQuoteRecordsToNull(final List<QuotesRecord> quotesRecords) {
    final List<Quote> quotes = instance.mapToQuoteList(quotesRecords);

    assertThat(quotes).isNull();
  }

  @Test
  @DisplayName("Should map quote and mentions to quote response")
  void shouldMapQuoteAndMentionsToQuoteResponse() {
    final Quote quote = QuoteGenerator.getQuote();
    final List<User> mentions = UserGenerator.getUsers();

    final QuoteResponse quoteResponse = instance.mapToResponse(quote, mentions);

    assertThat(quoteResponse).isNotNull();
    assertThat(quoteResponse.quote()).isNotNull();
    assertThat(quoteResponse.mentions()).isNotNull().hasSizeGreaterThanOrEqualTo(1);
  }

  @Test
  @DisplayName("Should map quote and empty mentions to quote response")
  void shouldMapQuoteAndEmptyMentionsToQuoteResponse() {
    final Quote quote = QuoteGenerator.getQuote();
    final List<User> mentions = Collections.emptyList();

    final QuoteResponse quoteResponse = instance.mapToResponse(quote, mentions);

    assertThat(quoteResponse).isNotNull();
    assertThat(quoteResponse.quote()).isNotNull();
    assertThat(quoteResponse.mentions()).isNotNull().isEmpty();
  }

  @Test
  @DisplayName("Should map empty quote and empty mentions to null")
  void shouldMapEmptyQuoteAndEmptyMentionsToNull() {
    final Quote quote = null;
    final List<User> mentions = null;

    final QuoteResponse result = instance.mapToResponse(quote, mentions);

    assertThat(result).isNull();
  }
}
