package de.zedalite.quotes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

import de.zedalite.quotes.data.model.User;
import de.zedalite.quotes.data.model.UserResponse;
import de.zedalite.quotes.data.model.UserUpdateRequest;
import de.zedalite.quotes.exception.ResourceNotFoundException;
import de.zedalite.quotes.exception.UserNotFoundException;
import de.zedalite.quotes.fixtures.UserGenerator;
import de.zedalite.quotes.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @InjectMocks
  private UserService instance;

  @Mock
  private UserRepository repository;

  @Test
  @DisplayName("Should find user")
  void shouldFindUser() {
    final User user = UserGenerator.getUser();
    willReturn(user).given(repository).findById(1);

    final UserResponse result = instance.find(1);

    then(repository).should().findById(1);
    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("Should not find user when user non exist")
  void shouldNotFindUserWhenUserNonExist() {
    willThrow(UserNotFoundException.class).given(repository).findById(1);

    assertThatCode(() -> instance.find(1)).isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  @DisplayName("Should update user")
  void shouldUpdateUser() {
    final User user = mock(User.class);
    final User updatedUser = mock(User.class);
    final UserUpdateRequest request = mock(UserUpdateRequest.class);

    given(repository.findById(anyInt())).willReturn(user);
    given(repository.update(anyInt(), any(UserUpdateRequest.class))).willReturn(updatedUser);
    given(user.displayName()).willReturn("displayName");
    given(updatedUser.displayName()).willReturn("newDisplayName");
    given(request.displayName()).willReturn("newDisplayName");

    final UserResponse result = instance.update(user.id(), request);

    then(repository).should().findById(user.id());
    then(repository).should().update(anyInt(), any(UserUpdateRequest.class));
    assertThat(result).isNotNull();
    assertThat(result.displayName()).isEqualTo("newDisplayName");
  }

  @Test
  @DisplayName("Should not update user when user not found")
  void shouldNotUpdateUserWhenUserNotFound() {
    final User user = mock(User.class);
    final UserUpdateRequest request = mock(UserUpdateRequest.class);

    given(repository.findById(anyInt())).willReturn(user);
    given(user.displayName()).willReturn("displayName");
    given(request.displayName()).willReturn("newDisplayName");
    given(repository.update(anyInt(), any(UserUpdateRequest.class))).willThrow(UserNotFoundException.class);

    assertThatCode(() -> instance.update(0, request)).isInstanceOf(ResourceNotFoundException.class);
  }
}
