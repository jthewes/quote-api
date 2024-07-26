package de.zedalite.quotes.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import de.zedalite.quotes.data.model.User;
import de.zedalite.quotes.data.model.UserPrincipal;
import de.zedalite.quotes.fixtures.UserGenerator;
import de.zedalite.quotes.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private AuthenticationService instance;

  @Test
  @DisplayName("Registered user should be member")
  void registeredUserShouldBeMember() {
    final User user = UserGenerator.getUser();
    given(userRepository.findByName(anyString())).willReturn(user);

    final UserPrincipal result = instance.getUser(user.name());

    assertThat(result.getUsername()).isEqualTo(user.name());
    assertThat(result.getAuthorities().stream().map(GrantedAuthority::getAuthority)).contains("ROLE_MEMBER");
  }
}
