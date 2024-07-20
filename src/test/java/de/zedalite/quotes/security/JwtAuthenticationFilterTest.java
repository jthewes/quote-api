package de.zedalite.quotes.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.auth0.jwt.exceptions.TokenExpiredException;
import de.zedalite.quotes.fixtures.UserGenerator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

  @InjectMocks
  private JwtAuthenticationFilter instance;

  @Mock
  private JwtTokenService jwtTokenService;

  @Mock
  private UserDetailsService userDetailsService;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  @Test
  @DisplayName("Should allow valid token")
  void shouldAllowValidToken() throws Exception {
    given(request.getHeader(AUTHORIZATION)).willReturn("Bearer jwtToken");
    given(request.getRemoteAddr()).willReturn("127.0.0.1");
    given(jwtTokenService.validateToken(anyString())).willReturn("validTestUser");
    given(userDetailsService.loadUserByUsername(anyString())).willReturn(UserGenerator.getUserPrincipal());

    instance.doFilterInternal(request, response, filterChain);

    then(filterChain).should().doFilter(request, response);
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
  }

  @Test
  @DisplayName("Should block invalid token")
  void shouldBlockInvalidToken() throws Exception {
    given(request.getHeader(AUTHORIZATION)).willReturn("Bearer jwtToken");
    given(request.getRemoteAddr()).willReturn("127.0.0.1");
    given(jwtTokenService.validateToken(anyString())).willThrow(TokenExpiredException.class);

    instance.doFilterInternal(request, response, filterChain);

    then(userDetailsService).should(never()).loadUserByUsername(anyString());
    then(filterChain).should().doFilter(request, response);
  }

  @ParameterizedTest
  @ValueSource(strings = { "Basic gfqvh21nb" })
  @NullSource
  @DisplayName("Should block no token or wrong header")
  void shouldBlockNoToken(final String headerValue) throws Exception {
    given(request.getHeader(AUTHORIZATION)).willReturn(headerValue);

    instance.doFilterInternal(request, response, filterChain);

    then(jwtTokenService).should(never()).validateToken(anyString());
    then(userDetailsService).should(never()).loadUserByUsername(anyString());
    then(filterChain).should().doFilter(request, response);
  }
}
