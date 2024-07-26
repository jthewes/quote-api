package de.zedalite.quotes.security;

import static de.zedalite.quotes.data.model.UserAuthorityRole.MEMBER;

import de.zedalite.quotes.data.model.User;
import de.zedalite.quotes.data.model.UserPrincipal;
import de.zedalite.quotes.exception.UserNotFoundException;
import de.zedalite.quotes.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

  private static final String ROLE_PREFIX = "ROLE_";

  private final UserRepository userRepository;

  public AuthenticationService(final UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserPrincipal getUser(final String username) {
    try {
      final User user = userRepository.findByName(username);
      return new UserPrincipal(user, List.of(new SimpleGrantedAuthority(ROLE_PREFIX + MEMBER)));
    } catch (final UserNotFoundException e) {
      // TODO create new user

      final User user = new User(0, username, username.toUpperCase(), LocalDateTime.now());
      return new UserPrincipal(user, List.of(new SimpleGrantedAuthority(ROLE_PREFIX + MEMBER))); // guest account
    }
  }
}
