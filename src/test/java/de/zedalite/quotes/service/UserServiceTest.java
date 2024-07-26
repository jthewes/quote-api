package de.zedalite.quotes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

import de.zedalite.quotes.data.model.User;
import de.zedalite.quotes.data.model.UserRequest;
import de.zedalite.quotes.data.model.UserResponse;
import de.zedalite.quotes.data.model.UserUpdateRequest;
import de.zedalite.quotes.exception.ResourceAlreadyExitsException;
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
  @DisplayName("Should create user when it not exist")
  void shouldCreateUserWhenItNotExist() {
    final UserRequest userRequest = UserGenerator.getUserRequest();
    final User user = UserGenerator.getUser();
    willReturn(false).given(repository).isUsernameTaken(anyString());
    willReturn(user).given(repository).save(any(UserRequest.class));

    final UserResponse result = instance.create(userRequest);

    then(repository).should().save(userRequest);
    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("Should not create user when it already exists")
  void shouldNotCreateUserWhenItAlreadyExists() {
    final UserRequest userRequest = UserGenerator.getUserRequest();
    willReturn(true).given(repository).isUsernameTaken(anyString());

    assertThatCode(() -> instance.create(userRequest)).isInstanceOf(ResourceAlreadyExitsException.class);

    then(repository).should(never()).save(userRequest);
  }

  @Test
  @DisplayName("Should not create user when saving failed ")
  void shouldNotCreateUserWhenSavingFailed() {
    final UserRequest userRequest = UserGenerator.getUserRequest();
    willThrow(UserNotFoundException.class).given(repository).save(userRequest);

    assertThatCode(() -> instance.create(userRequest)).isInstanceOf(ResourceNotFoundException.class);
  }

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
  @DisplayName("Should throw exception when username already taken")
  void shouldThrowExceptionWhenUsernameAlreadyTaken() {
    final UserUpdateRequest request = mock(UserUpdateRequest.class);
    given(repository.isUsernameTaken(anyString())).willReturn(true);
    given(request.name()).willReturn("taken");

    assertThatCode(() -> instance.update(0, request)).isInstanceOf(ResourceAlreadyExitsException.class);
  }

  @Test
  @DisplayName("Should update user")
  void shouldUpdateUser() {
    final User user = mock(User.class);
    final User updatedUser = mock(User.class);
    final UserUpdateRequest request = mock(UserUpdateRequest.class);

    given(repository.isUsernameTaken(anyString())).willReturn(false);
    given(repository.findById(anyInt())).willReturn(user);
    given(repository.update(anyInt(), any(UserUpdateRequest.class))).willReturn(updatedUser);
    given(user.name()).willReturn("name");
    given(user.displayName()).willReturn("displayName");
    given(updatedUser.name()).willReturn("newName");
    given(updatedUser.displayName()).willReturn("newDisplayName");
    given(request.name()).willReturn("newName");
    given(request.displayName()).willReturn("newDisplayName");

    final UserResponse result = instance.update(user.id(), request);

    then(repository).should().isUsernameTaken(request.name());
    then(repository).should().findById(user.id());
    then(repository).should().update(anyInt(), any(UserUpdateRequest.class));
    assertThat(result).isNotNull();
    assertThat(result.name()).isEqualTo("newName");
    assertThat(result.displayName()).isEqualTo("newDisplayName");
  }

  @Test
  @DisplayName("Should not update user when user not found")
  void shouldNotUpdateUserWhenUserNotFound() {
    final User user = mock(User.class);
    final UserUpdateRequest request = mock(UserUpdateRequest.class);

    given(repository.isUsernameTaken(anyString())).willReturn(false);
    given(repository.findById(anyInt())).willReturn(user);
    given(user.name()).willReturn("name");
    given(user.displayName()).willReturn("displayName");
    given(request.name()).willReturn("newName");
    given(request.displayName()).willReturn("newDisplayName");
    given(repository.update(anyInt(), any(UserUpdateRequest.class))).willThrow(UserNotFoundException.class);

    assertThatCode(() -> instance.update(0, request)).isInstanceOf(ResourceNotFoundException.class);
  }
}
