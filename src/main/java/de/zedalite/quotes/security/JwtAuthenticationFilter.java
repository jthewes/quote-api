package de.zedalite.quotes.security;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.auth0.jwt.exceptions.JWTVerificationException;
import de.zedalite.quotes.data.mapper.RequestMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
  private static final RequestMapper REQUEST_MAPPER = RequestMapper.INSTANCE;

  private final JwtTokenService tokenService;
  private final UserDetailsService userDetailsService;

  public JwtAuthenticationFilter(final JwtTokenService tokenService, final UserDetailsService userDetailsService) {
    this.tokenService = tokenService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(
    @NotNull final HttpServletRequest request,
    @NotNull final HttpServletResponse response,
    @NotNull final FilterChain filterChain
  ) throws ServletException, IOException {
    final String header = request.getHeader(AUTHORIZATION);

    if (header != null && header.startsWith("Bearer ")) {
      final String token = header.substring(7); // cut "Bearer "

      try {
        final String username = tokenService.validateToken(token);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          userDetails,
          null,
          userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String authMessage = REQUEST_MAPPER.map(request, "SUCCESS", username).toString();
        LOGGER.debug(authMessage);
      } catch (final JWTVerificationException ex) {
        final String authMessage = REQUEST_MAPPER.map(request, "FAIL", "anon").toString();
        LOGGER.info(authMessage);
        filterChain.doFilter(request, response);
        return;
      }
    }
    filterChain.doFilter(request, response);
  }
}
