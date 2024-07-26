package de.zedalite.quotes.config;

import de.zedalite.quotes.security.AuthenticationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class AuthenticationConfig {

  private final AuthenticationService authenticationService;

  public AuthenticationConfig(final AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return authenticationService::getUser;
  }
}
