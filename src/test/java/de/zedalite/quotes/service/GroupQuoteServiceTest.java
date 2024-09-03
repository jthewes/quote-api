package de.zedalite.quotes.service;

import static de.zedalite.quotes.data.model.SortField.CREATION_DATE;
import static de.zedalite.quotes.data.model.SortMode.DESCENDING;
import static de.zedalite.quotes.data.model.SortMode.NONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.anyInt;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;

import de.zedalite.quotes.data.model.CountResponse;
import de.zedalite.quotes.data.model.FilterKey;
import de.zedalite.quotes.data.model.Quote;
import de.zedalite.quotes.data.model.QuoteRequest;
import de.zedalite.quotes.data.model.QuoteResponse;
import de.zedalite.quotes.data.model.SortField;
import de.zedalite.quotes.data.model.SortMode;
import de.zedalite.quotes.data.model.User;
import de.zedalite.quotes.exception.QuoteNotFoundException;
import de.zedalite.quotes.exception.ResourceNotFoundException;
import de.zedalite.quotes.fixtures.QuoteGenerator;
import de.zedalite.quotes.fixtures.UserGenerator;
import de.zedalite.quotes.repository.GroupQuoteRepository;
import de.zedalite.quotes.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroupQuoteServiceTest {

  @InjectMocks
  private GroupQuoteService instance;

  @Mock
  private GroupQuoteRepository repository;

  @Mock
  private UserRepository userRepository;

  @Test
  @DisplayName("Should create group quote")
  void shouldCreateGroupQuote() {
    final QuoteRequest quoteRequest = QuoteGenerator.getQuoteRequest();
    final Quote expectedQuote = QuoteGenerator.getQuoteWithMentions();
    final User expectedUser = UserGenerator.getUser();

    willReturn(expectedQuote).given(repository).save(1, quoteRequest, 5);
    willReturn(expectedUser).given(userRepository).findById(anyInt());

    final QuoteResponse result = instance.create(1, quoteRequest, 5);

    then(repository).should().save(1, quoteRequest, 5);
    assertThat(result).isNotNull();
    assertThat(result.mentions()).hasSizeGreaterThanOrEqualTo(1);
  }

  @Test
  @DisplayName("Should throw exception when group quote not created")
  void shouldThrowExceptionWhenGroupQuoteNotCreated() {
    final QuoteRequest quoteRequest = QuoteGenerator.getQuoteRequest();
    willThrow(QuoteNotFoundException.class).given(repository).save(1, quoteRequest, 5);

    assertThatCode(() -> instance.create(1, quoteRequest, 5)).isInstanceOf(ResourceNotFoundException.class);
  }

  @ParameterizedTest
  @CsvSource(
    {
      "CREATOR, 4, CREATION_DATE, DESCENDING, 1",
      "CREATOR, 4, CREATION_DATE, ASCENDING, 1",
      "CREATOR, 4, CREATION_DATE, RANDOM, 1",
      "CREATOR, 4, CREATION_DATE, NONE, 1",
      "CREATOR, 4, NONE, DESCENDING, 1",
      "CREATOR, 4, NONE, ASCENDING, 1",
      "CREATOR, 4, NONE, RANDOM, 1",
      "CREATOR, 4, NONE, NONE, 1",
      "AUTHOR, qa, CREATION_DATE, DESCENDING, 1",
      "AUTHOR, qa, CREATION_DATE, ASCENDING, 1",
      "AUTHOR, qa, CREATION_DATE, RANDOM, 1",
      "AUTHOR, qa, CREATION_DATE, NONE, 1",
      "AUTHOR, qa, NONE, DESCENDING, 1",
      "AUTHOR, qa, NONE, ASCENDING, 1",
      "AUTHOR, qa, NONE, RANDOM, 1",
      "AUTHOR, qa, NONE, NONE, 1",
      "TEXT, are, CREATION_DATE, DESCENDING, 2",
      "TEXT, are, CREATION_DATE, ASCENDING, 2",
      "TEXT, are, CREATION_DATE, RANDOM, 2",
      "TEXT, are, CREATION_DATE, NONE, 2",
      "TEXT, are, NONE, DESCENDING, 2",
      "TEXT, are, NONE, ASCENDING, 2",
      "TEXT, are, NONE, RANDOM, 2",
      "TEXT, are, NONE, NONE, 2",
      "CONTEXT, home, CREATION_DATE, DESCENDING, 1",
      "CONTEXT, Home, CREATION_DATE, ASCENDING, 1",
      "CONTEXT, home, CREATION_DATE, RANDOM, 1",
      "CONTEXT, home, CREATION_DATE, NONE, 1",
      "CONTEXT, home, NONE, DESCENDING, 1",
      "CONTEXT, Home, NONE, ASCENDING, 1",
      "CONTEXT, home, NONE, RANDOM, 1",
      "CONTEXT, home, NONE, NONE, 1",
      "NONE, , CREATION_DATE, DESCENDING, 3",
      "NONE, , CREATION_DATE, ASCENDING, 3",
      "NONE, , CREATION_DATE, RANDOM, 3",
      "NONE, , CREATION_DATE, NONE, 3",
      "NONE, , NONE, DESCENDING, 3",
      "NONE, , NONE, ASCENDING, 3",
      "NONE, , NONE, RANDOM, 3",
      "NONE, , NONE, NONE, 3",
      "NONE, a, CREATION_DATE, DESCENDING, 3",
    }
  )
  @DisplayName("Should find all group quotes")
  void shouldFindAllGroupQuotes(
    final FilterKey filterKey,
    final String filterValue,
    final SortField sortField,
    final SortMode sortMode,
    final Integer expectedSize
  ) {
    final List<Quote> expectedQuotes = QuoteGenerator.getQuotes();
    willReturn(expectedQuotes).given(repository).findAll(anyInt());

    final List<QuoteResponse> quotes = instance.findAll(1, filterKey, filterValue, sortField, sortMode);

    assertThat(quotes).hasSize(expectedSize);
  }

  @ParameterizedTest
  @EnumSource(FilterKey.class)
  @DisplayName("Should find all group quotes without filter and sort")
  void shouldFindAllGroupQuotesWithoutFilterAndSort(final FilterKey filterKey) {
    final List<Quote> expectedQuotes = QuoteGenerator.getQuotes();
    willReturn(expectedQuotes).given(repository).findAll(anyInt());

    final List<QuoteResponse> quotes = instance.findAll(1, filterKey, null, null, null);

    assertThat(quotes).hasSize(expectedQuotes.size());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("Should return all quotest when filterValue is empty")
  void shouldReturnAllQuotestWhenFilterValueIsEmpty(final String filterValue) {
    final List<Quote> expectedQuotes = QuoteGenerator.getQuotes();
    willReturn(expectedQuotes).given(repository).findAll(anyInt());

    final List<QuoteResponse> quotes = instance.findAll(1, FilterKey.TEXT, filterValue, null, null);

    assertThat(quotes).hasSize(expectedQuotes.size());
  }

  @Test
  @DisplayName("Should throw exception when group quotes list is empty")
  void shouldThrowExceptionWhenGroupQuotesListIsEmpty() {
    final List<Quote> expectedQuotes = QuoteGenerator.getQuotes();
    willReturn(expectedQuotes).given(repository).findAll(anyInt());

    assertThatCode(() -> instance.findAll(1, FilterKey.AUTHOR, "ABCDEF", CREATION_DATE, NONE)).isInstanceOf(
      ResourceNotFoundException.class
    );
  }

  @Test
  @DisplayName("Should throw exception when group quotes not found")
  void shouldThrowExceptionWhenGroupQuotesNotFound() {
    willThrow(QuoteNotFoundException.class).given(repository).findAll(anyInt());

    assertThatCode(() -> instance.findAll(1, null, null, CREATION_DATE, DESCENDING)).isInstanceOf(
      ResourceNotFoundException.class
    );
  }

  @Test
  @DisplayName("Should find group quote")
  void shouldFindGroupQuote() {
    final Quote expectedQuote = QuoteGenerator.getQuote();
    willReturn(expectedQuote).given(repository).findById(1, 2);

    final QuoteResponse result = instance.find(1, 2);

    then(repository).should().findById(1, 2);
    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("Should throw exception when group quote not found")
  void shouldThrowExceptionWhenGroupQuoteNotFound() {
    willThrow(QuoteNotFoundException.class).given(repository).findById(1, 2);

    assertThatCode(() -> instance.find(1, 2)).isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  @DisplayName("Should count quotes")
  void shouldCountQuotes() {
    willReturn(5).given(repository).count(1);

    final CountResponse count = instance.count(1);

    assertThat(count.count()).isEqualTo(5);
    then(repository).should().count(1);
  }
}
