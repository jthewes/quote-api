package de.zedalite.quotes.web;

import static de.zedalite.quotes.data.model.FilterKey.*;
import static de.zedalite.quotes.data.model.SortField.CREATION_DATE;
import static de.zedalite.quotes.data.model.SortMode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;

import de.zedalite.quotes.data.model.*;
import de.zedalite.quotes.fixtures.QuoteGenerator;
import de.zedalite.quotes.fixtures.UserGenerator;
import de.zedalite.quotes.service.GroupQuoteService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class GroupQuoteControllerTest {

  @InjectMocks
  private GroupQuoteController instance;

  @Mock
  private GroupQuoteService service;

  @Test
  @DisplayName("Should get group quotes")
  void shouldGetGroupQuotes() {
    final List<QuoteResponse> expectedQuotes = QuoteGenerator.getQuoteResponses();
    willReturn(expectedQuotes)
      .given(service)
      .findAll(anyInt(), any(FilterKey.class), anyString(), any(SortField.class), any(SortMode.class));

    final ResponseEntity<List<QuoteResponse>> response = instance.getAll(1, CREATOR, "1", CREATION_DATE, ASCENDING);

    then(service).should().findAll(1, CREATOR, "1", CREATION_DATE, ASCENDING);
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  @DisplayName("Should get group quote")
  void shouldGetGroupQuote() {
    final QuoteResponse expectedQuote = QuoteGenerator.getQuoteResponse();
    willReturn(expectedQuote).given(service).find(anyInt(), anyInt());

    final ResponseEntity<QuoteResponse> response = instance.get(1, 8);

    then(service).should().find(1, 8);
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  @DisplayName("Should get quote count")
  void shouldGetQuoteCount() {
    final CountResponse count = new CountResponse(5);
    willReturn(count).given(service).count(anyInt());

    final ResponseEntity<CountResponse> response = instance.getQuotesCount(1);

    then(service).should().count(1);
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  @DisplayName("Should post group quote")
  void shouldPostGroupQuote() {
    final QuoteRequest quoteRequest = QuoteGenerator.getQuoteRequest();
    final UserPrincipal principal = UserGenerator.getUserPrincipal();
    final QuoteResponse expectedQuote = QuoteGenerator.getQuoteResponse();
    willReturn(expectedQuote).given(service).create(anyInt(), any(QuoteRequest.class), anyInt());

    final ResponseEntity<QuoteResponse> response = instance.createQuote(1, quoteRequest, principal);

    then(service).should().create(1, quoteRequest, 1);
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
