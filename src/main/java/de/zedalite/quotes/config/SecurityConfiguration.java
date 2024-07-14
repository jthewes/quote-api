package de.zedalite.quotes.config;

import static de.zedalite.quotes.data.model.UserAuthorityRole.GUEST;
import static de.zedalite.quotes.data.model.UserAuthorityRole.MEMBER;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import de.zedalite.quotes.security.JwtAuthenticationFilter;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
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

  private final AuthenticationProvider authenticationProvider;

  private final List<String> corsAllowedOrigins;

  public SecurityConfiguration(
    final JwtAuthenticationFilter jwtAuthFilter,
    final AuthenticationProvider authenticationProvider,
    final @Value("${app.security.cors.allowedOrigins}") List<String> corsAllowedOrigins
  ) {
    this.jwtAuthFilter = jwtAuthFilter;
    this.authenticationProvider = authenticationProvider;
    this.corsAllowedOrigins = corsAllowedOrigins;
  }

  @Bean
  @SuppressWarnings("java:S4502") //The web application does not use cookies to authenticate users.
  public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
    http
      .cors(c -> c.configurationSource(getCorsConfiguration()))
      .authorizeHttpRequests(authz ->
        authz
          .requestMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui/**")
          .permitAll()
          .requestMatchers("/users")
          .hasRole(GUEST.toString())
          .anyRequest()
          .hasRole(MEMBER.toString())
      )
      .csrf(AbstractHttpConfigurer::disable) // https://security.stackexchange.com/questions/170388/do-i-need-csrf-token-if-im-using-bearer-jwt
      .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
      .authenticationProvider(authenticationProvider)
      .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
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
