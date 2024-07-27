package de.zedalite.quotes.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.zedalite.quotes.data.model.User;
import de.zedalite.quotes.data.model.UserPrincipal;
import de.zedalite.quotes.data.model.UserRequest;
import de.zedalite.quotes.exception.UserNotFoundException;
import de.zedalite.quotes.repository.AuthenticationServerRepository;
import de.zedalite.quotes.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private AuthenticationServerRepository authRepository;

  @InjectMocks
  private AuthenticationService instance;

  @Test
  @DisplayName("Registered user should be member")
  void registeredUserShouldBeMember() {
    final User user = mock(User.class);
    given(user.authId()).willReturn("usr-132141");
    given(userRepository.findByAuthId(anyString())).willReturn(user);

    final UserPrincipal result = instance.getUser(user.authId());

    assertThat(result.getUsername()).isEqualTo(user.authId());
    assertThat(result.getAuthorities().stream().map(GrantedAuthority::getAuthority)).contains("ROLE_MEMBER");
  }

  @Test
  @DisplayName("Non-registered user should be created")
  void nonRegisteredUserShouldBeCreated() {
    final User user = mock(User.class);

    given(user.authId()).willReturn("usr-132141");
    given(userRepository.findByAuthId(anyString())).willThrow(UserNotFoundException.class);
    given(authRepository.getUsername(anyString())).willReturn("THE USER");
    given(userRepository.save(anyString(), any(UserRequest.class))).willReturn(user);

    final UserPrincipal result = instance.getUser(user.authId());

    assertThat(result.getUsername()).isEqualTo(user.authId());
    assertThat(result.getAuthorities().stream().map(GrantedAuthority::getAuthority)).contains("ROLE_MEMBER");
  }

  @Test
  @DisplayName("When auth server not reachable should throw exception")
  void whenAuthServerNotReachableShouldThrowException() {
    final User user = mock(User.class);

    given(user.authId()).willReturn("usr-132141");
    given(userRepository.findByAuthId(anyString())).willThrow(UserNotFoundException.class);
    given(authRepository.getUsername(anyString())).willThrow(UserNotFoundException.class);

    final String authId = user.authId();

    assertThatCode(() -> instance.getUser(authId)).isInstanceOf(AccessDeniedException.class);
  }

  @Test
  @DisplayName("When user could not be created should throw exception")
  void whenUserCouldNotBeCreatedShouldThrowException() {
    final User user = mock(User.class);

    given(user.authId()).willReturn("usr-132141");
    given(userRepository.findByAuthId(anyString())).willThrow(UserNotFoundException.class);
    given(authRepository.getUsername(anyString())).willReturn("THE USER");
    given(userRepository.save(anyString(), any(UserRequest.class))).willThrow(UserNotFoundException.class);

    final String authId = user.authId();

    assertThatCode(() -> instance.getUser(authId)).isInstanceOf(AccessDeniedException.class);
  }
}
