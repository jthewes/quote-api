package de.zedalite.quotes.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;

import de.zedalite.quotes.data.model.QuoteResponse;
import de.zedalite.quotes.fixtures.QuoteGenerator;
import de.zedalite.quotes.service.GroupQuoteOfTheDayService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class GroupQuoteOfTheDayControllerTest {

  @InjectMocks
  private GroupQuoteOfTheDayController instance;

  @Mock
  private GroupQuoteOfTheDayService service;

  @Test
  @DisplayName("Should get quote of the day")
  void shouldGetQuoteOfTheDay() {
    final QuoteResponse expectedQuote = QuoteGenerator.getQuoteResponse();
    willReturn(expectedQuote).given(service).findQuoteOfTheDay(anyInt());

    final ResponseEntity<QuoteResponse> response = instance.getQuoteOfTheDay(1);

    then(service).should().findQuoteOfTheDay(1);
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
