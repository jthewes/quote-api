package de.zedalite.quotes.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;

import de.zedalite.quotes.data.model.*;
import de.zedalite.quotes.fixtures.UserGenerator;
import de.zedalite.quotes.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @InjectMocks
  private UserController instance;

  @Mock
  private UserService service;

  @Test
  @DisplayName("Should get user")
  void shouldGetUser() {
    final UserPrincipal userPrincipal = UserGenerator.getUserPrincipal();
    final UserResponse expectedUser = UserGenerator.getUserResponse();
    willReturn(expectedUser).given(service).find(anyInt());

    final ResponseEntity<UserResponse> response = instance.get(userPrincipal);

    then(service).should().find(userPrincipal.getId());
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  @DisplayName("Should update user")
  void shouldUpdateUser() {
    final UserPrincipal principal = mock(UserPrincipal.class);
    final UserUpdateRequest request = mock(UserUpdateRequest.class);
    final UserResponse updatedUser = mock(UserResponse.class);

    given(service.update(principal.getId(), request)).willReturn(updatedUser);

    final ResponseEntity<UserResponse> response = instance.update(principal, request);

    then(service).should().update(principal.getId(), request);
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
