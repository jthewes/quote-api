package de.zedalite.quotes.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import de.zedalite.quotes.TestEnvironmentProvider;
import de.zedalite.quotes.data.model.User;
import de.zedalite.quotes.data.model.UserRequest;
import de.zedalite.quotes.data.model.UserUpdateRequest;
import de.zedalite.quotes.exception.UserNotFoundException;
import java.util.List;
import java.util.stream.Stream;
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
    instance.save("usr-12421", new UserRequest("Repo Tester"));
  }

  @Test
  @DisplayName("Should save user")
  void shouldSaveUser() {
    final UserRequest user = new UserRequest("New User");

    final User savedUser = instance.save("usr-323421", user);

    assertThat(savedUser).isNotNull();
    assertThat(savedUser.id()).isNotNull();
    assertThat(savedUser.authId()).isEqualTo("usr-323421");
    assertThat(savedUser.displayName()).isEqualTo("New User");
  }

  @Test
  @DisplayName("Should find all by ids")
  void shouldFindAllByIds() {
    final Integer id = instance.findByAuthId("usr-12421").id();
    final List<User> users = Stream.of(id).map(instance::findById).toList();

    assertThat(users).hasSize(1);
    assertThat(users.getFirst().authId()).isEqualTo("usr-12421");
  }

  @Test
  @DisplayName("Should find user by auth id")
  void shouldFindUserByAuthId() {
    final User user = instance.findByAuthId("usr-12421");

    assertThat(user).isNotNull();
    assertThat(user.authId()).isEqualTo("usr-12421");
  }

  @Test
  @DisplayName("Should throw exception when user not found")
  void shouldThrowExceptionWhenUserNotFound() {
    assertThatCode(() -> instance.findByAuthId("invalidName")).isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("Should find user by id")
  void shouldFindUserById() {
    final Integer id = instance.findByAuthId("usr-12421").id();
    final User user = instance.findById(id);

    assertThat(user).isNotNull();
    assertThat(user.authId()).isEqualTo("usr-12421");
  }

  @Test
  @DisplayName("Should update user")
  void shouldUpdateUser() {
    final Integer userId = instance.save("usr-42235", new UserRequest("Super")).id();
    final UserUpdateRequest request = new UserUpdateRequest("MEGA");

    final User updatedUser = instance.update(userId, request);

    assertThat(updatedUser.id()).isEqualTo(userId);
    assertThat(updatedUser.displayName()).isEqualTo("MEGA");
  }

  @Test
  @DisplayName("Should return true when auth id exists")
  void shouldReturnTrueWhenUsernameIsTaken() {
    instance.save("definitelyTaken", new UserRequest("Taken User"));

    final boolean isTaken = instance.doesAuthUserExist("definitelyTaken");

    assertThat(isTaken).isTrue();
  }

  @Test
  @DisplayName("Should return false when auth id is free")
  void shouldReturnFalseWhenUsernameIsFree() {
    final boolean isTaken = instance.doesAuthUserExist("freeUsername");

    assertThat(isTaken).isFalse();
  }

  @Test
  @DisplayName("Should return true when user non exist")
  void shouldReturnTrueWhenUserNonExist() {
    final boolean isNonExist = instance.doesUserExist(9876);

    assertThat(isNonExist).isTrue();
  }

  @Test
  @DisplayName("Should return false when user exist")
  void shouldReturnFalseWhenUserExist() {
    final boolean isNonExist = instance.doesUserExist(1);

    assertThat(isNonExist).isFalse();
  }
}
