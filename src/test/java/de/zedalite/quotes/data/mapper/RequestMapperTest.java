package de.zedalite.quotes.data.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import de.zedalite.quotes.data.model.AuthLog;
import de.zedalite.quotes.data.model.RequestLog;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestMapperTest {

  private static final RequestMapper instance = RequestMapper.INSTANCE;

  @Mock
  private HttpServletRequest request;

  @Test
  @DisplayName("Should mapRequest request log")
  void shouldMapRequestRequestLog() {
    given(request.getRequestURI()).willReturn("/quotes");
    given(request.getQueryString()).willReturn("filter=ASC");
    given(request.getHeader("X-Forwarded-For")).willReturn("10.0.0.2");
    given(request.getRemoteAddr()).willReturn("10.0.0.9");

    final RequestLog result = instance.mapRequest(request, 200, "user", 1L);

    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("Should mapRequest empty request log")
  void shouldMapRequestEmptyRequestLog() {
    final RequestLog result = instance.mapRequest(null, null, null, null);

    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should mapRequest auth log")
  void shouldMapRequestAuthLog() {
    given(request.getRequestURI()).willReturn("/quotes");
    given(request.getQueryString()).willReturn("filter=ASC");
    given(request.getHeader("X-Forwarded-For")).willReturn("10.0.0.2");
    given(request.getRemoteAddr()).willReturn("10.0.0.9");

    final AuthLog result = instance.mapAuth(request, "SUCCESS", "user", "success");

    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("Should mapRequest empty auth log")
  void shouldMapRequestEmptyAuthLog() {
    final AuthLog result = instance.mapAuth(null, null, null, null);

    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should mapRequest client")
  void shouldMapRequestClient() {
    given(request.getHeader("X-Forwarded-For")).willReturn("10.0.0.2");
    given(request.getRemoteAddr()).willReturn("10.0.0.9");

    final String result = instance.mapClient(request);

    assertThat(result).isEqualTo("10.0.0.2");
  }

  @Test
  @DisplayName("Should mapRequest client when X-Forwarded-Header is empty")
  void shouldMapRequestClientWhenXForwardedHeaderIsEmpty() {
    given(request.getHeader("X-Forwarded-For")).willReturn(null);
    given(request.getRemoteAddr()).willReturn("10.0.0.9");

    final String result = instance.mapClient(request);

    assertThat(result).isEqualTo("10.0.0.9");
  }

  @Test
  @DisplayName("Should mapRequest query")
  void shouldMapRequestQuery() {
    final String query = "filter=ASC";

    final String result = instance.mapQuery(query);

    assertThat(result).isEqualTo("?filter=ASC");
  }

  @ParameterizedTest
  @DisplayName("Should mapRequest empty query")
  @NullSource
  @EmptySource
  void shouldMapRequestEmptyQuery(final String query) {
    final String result = instance.mapQuery(query);

    assertThat(result).isBlank();
  }
}
