package de.zedalite.quotes.config;

import static de.zedalite.quotes.data.model.UserAuthorityRole.MEMBER;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import de.zedalite.quotes.security.JwtAuthenticationFilter;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfiguration {

  private final JwtAuthenticationFilter jwtAuthFilter;
  private final List<String> corsAllowedOrigins;

  public SecurityConfiguration(
    final JwtAuthenticationFilter jwtAuthFilter,
    final @Value("${app.security.cors.allowedOrigins}") List<String> corsAllowedOrigins
  ) {
    this.jwtAuthFilter = jwtAuthFilter;
    this.corsAllowedOrigins = corsAllowedOrigins;
  }

  @Bean
  @SuppressWarnings("java:S4502") //The web application does not use cookies to authenticate users.
  public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
    http
      .cors(c -> c.configurationSource(getCorsConfiguration()))
      .csrf(AbstractHttpConfigurer::disable) // https://security.stackexchange.com/questions/170388/do-i-need-csrf-token-if-im-using-bearer-jwt
      .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
      .authorizeHttpRequests(authz ->
        authz
          .requestMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui/**")
          .permitAll()
          .anyRequest()
          .hasRole(MEMBER.toString())
      )
      .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
      .exceptionHandling(exceptHandler ->
        exceptHandler.authenticationEntryPoint((request, response, authException) -> response.sendError(HttpStatus.UNAUTHORIZED.value()))
      );
    return http.build();
  }

  private CorsConfigurationSource getCorsConfiguration() {
    final CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(corsAllowedOrigins);

    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
