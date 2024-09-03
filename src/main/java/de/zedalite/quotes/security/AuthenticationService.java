package de.zedalite.quotes.security;

import static de.zedalite.quotes.data.model.UserAuthorityRole.MEMBER;

import de.zedalite.quotes.data.model.User;
import de.zedalite.quotes.data.model.UserPrincipal;
import de.zedalite.quotes.data.model.UserRequest;
import de.zedalite.quotes.exception.UserNotFoundException;
import de.zedalite.quotes.repository.AuthenticationServerRepository;
import de.zedalite.quotes.repository.UserRepository;
import de.zedalite.quotes.utils.StringUtils;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

  private static final String ROLE_PREFIX = "ROLE_";

  private final UserRepository userRepository;
  private final AuthenticationServerRepository authRepository;

  public AuthenticationService(
    final UserRepository userRepository,
    final AuthenticationServerRepository authRepository
  ) {
    this.userRepository = userRepository;
    this.authRepository = authRepository;
  }

  public UserPrincipal getUser(final String authId) {
    try {
      final User user = userRepository.findByAuthId(authId);
      return new UserPrincipal(user, List.of(new SimpleGrantedAuthority(ROLE_PREFIX + MEMBER)));
    } catch (final UserNotFoundException e) {
      try {
        final User user = createNewAccount(authId);
        return new UserPrincipal(user, List.of(new SimpleGrantedAuthority(ROLE_PREFIX + MEMBER)));
      } catch (final UserNotFoundException e2) {
        throw new AccessDeniedException("User not found in auth server or auth server not reachable");
      }
    }
  }

  private User createNewAccount(final String authId) {
    final String username = authRepository.getUsername(authId); // fetch user from auth server
    return userRepository.save(authId, new UserRequest(StringUtils.truncate(username, 32)));
  }
}
