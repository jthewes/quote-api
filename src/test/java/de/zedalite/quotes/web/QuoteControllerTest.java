package de.zedalite.quotes.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;

import de.zedalite.quotes.data.model.CountResponse;
import de.zedalite.quotes.service.QuoteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class QuoteControllerTest {

  @InjectMocks
  private QuoteController instance;

  @Mock
  private QuoteService service;

  @Test
  @DisplayName("Should get quote count")
  void shouldGetQuoteCount() {
    final CountResponse count = new CountResponse(5);
    willReturn(count).given(service).count();

    final ResponseEntity<CountResponse> response = instance.getCount();

    then(service).should().count();
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
