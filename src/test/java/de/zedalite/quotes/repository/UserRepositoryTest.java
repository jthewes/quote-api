package de.zedalite.quotes.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import de.zedalite.quotes.TestEnvironmentProvider;
import de.zedalite.quotes.data.model.User;
import de.zedalite.quotes.data.model.UserRequest;
import de.zedalite.quotes.data.model.UserUpdateRequest;
import de.zedalite.quotes.exception.UserNotFoundException;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(value = "classpath:test-no-cache.properties")
class UserRepositoryTest extends TestEnvironmentProvider {

  @Autowired
  private UserRepository instance;

  @BeforeAll
  void setup() {
    instance.save(new UserRequest("repoTester", "email@test.com", "Repo Tester"));
  }

  @Test
  @DisplayName("Should save user")
  void shouldSaveUser() {
    final UserRequest user = new UserRequest("newuser", "email@test.com", "New User");

    final User savedUser = instance.save(user);

    assertThat(savedUser).isNotNull();
    assertThat(savedUser.id()).isNotNull();
    assertThat(savedUser.name()).isEqualTo("newuser");
    assertThat(savedUser.email()).isEqualTo("email@test.com");
  }

  @Test
  @DisplayName("Should find all users")
  void shouldFindAllUsers() {
    final List<User> users = instance.findAll();

    assertThat(users).hasSizeGreaterThanOrEqualTo(1);
  }

  @Test
  @DisplayName("Should find all by ids")
  void shouldFindAllByIds() {
    final Integer id = instance.findByName("repoTester").id();
    final List<User> users = instance.findAllByIds(List.of(id));

    assertThat(users).hasSize(1);
    assertThat(users.getFirst().name()).isEqualTo("repoTester");
  }

  @Test
  @DisplayName("Should find user by name")
  void shouldFindUserByName() {
    final User user = instance.findByName("repoTester");

    assertThat(user).isNotNull();
    assertThat(user.name()).isEqualTo("repoTester");
  }

  @Test
  @DisplayName("Should throw exception when user not found")
  void shouldThrowExceptionWhenUserNotFound() {
    assertThatCode(() -> instance.findByName("invalidName")).isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("Should find user by id")
  void shouldFindUserById() {
    final Integer id = instance.findByName("repoTester").id();
    final User user = instance.findById(id);

    assertThat(user).isNotNull();
    assertThat(user.name()).isEqualTo("repoTester");
  }

  @Test
  @DisplayName("Should update user")
  void shouldUpdateUser() {
    final Integer userId = instance.save(new UserRequest("super", "email@test.com", "Super")).id();
    final UserUpdateRequest request = new UserUpdateRequest("mega", "email@test.com", "MEGA");

    final User updatedUser = instance.update(userId, request);

    assertThat(updatedUser.id()).isEqualTo(userId);
    assertThat(updatedUser.email()).isEqualTo("email@test.com");
    assertThat(updatedUser.displayName()).isEqualTo("MEGA");
  }

  @Test
  @DisplayName("Should return true when username is taken")
  void shouldReturnTrueWhenUsernameIsTaken() {
    instance.save(new UserRequest("definitelyTaken", "taken", "Taken User"));

    final boolean isTaken = instance.isUsernameTaken("definitelyTaken");

    assertThat(isTaken).isTrue();
  }

  @Test
  @DisplayName("Should return false when username is free")
  void shouldReturnFalseWhenUsernameIsFree() {
    final boolean isTaken = instance.isUsernameTaken("freeUsername");

    assertThat(isTaken).isFalse();
  }

  @Test
  @DisplayName("Should return false when username is already taken")
  void shouldReturnFalseWhenUsernameIsAlreadyTaken() {
    instance.save(new UserRequest("takenName", "taken", "Taken Name"));

    final boolean isAvailable = instance.isUsernameAvailable("takenName");

    assertThat(isAvailable).isFalse();
  }

  @Test
  @DisplayName("Should return true when username is free")
  void shouldReturnTrueWhenUsernameIsFree() {
    final boolean isAvailable = instance.isUsernameAvailable("abcdefghijklmn");

    assertThat(isAvailable).isTrue();
  }

  @Test
  @DisplayName("Should return true when user non exist")
  void shouldReturnTrueWhenUserNonExist() {
    final boolean isNonExist = instance.doesUserNonExist(9876);

    assertThat(isNonExist).isTrue();
  }

  @Test
  @DisplayName("Should return false when user exist")
  void shouldReturnFalseWhenUserExist() {
    final boolean isNonExist = instance.doesUserNonExist(1);

    assertThat(isNonExist).isFalse();
  }
}
