package de.zedalite.quotes.data.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.zedalite.quotes.data.jooq.users.tables.records.UsersRecord;
import de.zedalite.quotes.data.model.User;
import de.zedalite.quotes.data.model.UserResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

class UserMapperTest {

  private static final UserMapper instance = UserMapper.INSTANCE;

  @Test
  @DisplayName("Should map userRecord to user")
  void shouldMapUserRecordToUser() {
    final UsersRecord userRec = new UsersRecord(0, "user", "USER", LocalDateTime.MIN);

    final User user = instance.mapToUser(userRec);

    assertThat(user).isNotNull();
    assertThat(user.id()).isEqualTo(userRec.getId());
    assertThat(user.name()).isEqualTo(userRec.getName());
    assertThat(user.creationDate()).isEqualTo(userRec.getCreationDate());
    assertThat(user.displayName()).isEqualTo(userRec.getDisplayName());
  }

  @ParameterizedTest
  @DisplayName("Should map empty userRecord to null")
  @NullSource
  void shouldMapEmptyUserRecordToNull(final UsersRecord usersRecord) {
    final User user = instance.mapToUser(usersRecord);

    assertThat(user).isNull();
  }

  @ParameterizedTest
  @DisplayName("Should map empty user record list to null")
  @NullSource
  void shouldMapEmptyUserRecordListToNull(final List<UsersRecord> usersRecords) {
    final List<User> result = instance.mapToUserList(usersRecords);

    assertThat(result).isNull();
  }

  @ParameterizedTest
  @DisplayName("Should map empty user to null")
  @NullSource
  void shouldMapEmptyUserListToNull(final User user) {
    final UserResponse result = instance.mapToResponse(user);

    assertThat(result).isNull();
  }

  @ParameterizedTest
  @DisplayName("Should map empty user list to null")
  @NullSource
  void shouldMapEmptyUserListToNull(final List<User> users) {
    final List<UserResponse> result = instance.mapToResponses(users);

    assertThat(result).isNull();
  }
}
