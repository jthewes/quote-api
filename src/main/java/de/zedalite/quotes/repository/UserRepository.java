package de.zedalite.quotes.repository;

import de.zedalite.quotes.data.jooq.users.tables.Users;
import de.zedalite.quotes.data.jooq.users.tables.records.UsersRecord;
import de.zedalite.quotes.data.mapper.UserMapper;
import de.zedalite.quotes.data.model.User;
import de.zedalite.quotes.data.model.UserRequest;
import de.zedalite.quotes.data.model.UserUpdateRequest;
import de.zedalite.quotes.exception.UserNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

/**
 * The UserRepository class is responsible for interacting with the user data in the database.
 * It provides methods for saving and retrieving user information.
 */
@Repository
public class UserRepository {

  private static final UserMapper USER_MAPPER = UserMapper.INSTANCE;

  private static final String USER_NOT_FOUND = "User not found";

  private static final Users USERS = Users.USERS_.as("Users");

  private final DSLContext dsl;

  public UserRepository(final DSLContext dsl) {
    this.dsl = dsl;
  }

  @CachePut(value = "users", key = "#result.id()", unless = "#result == null")
  public User save(final String authId, final UserRequest user) throws UserNotFoundException {
    final Optional<UsersRecord> savedUser = dsl
      .insertInto(USERS)
      .set(USERS.AUTH_ID, authId)
      .set(USERS.CREATION_DATE, LocalDateTime.now())
      .set(USERS.DISPLAY_NAME, user.displayName())
      .returning()
      .fetchOptionalInto(UsersRecord.class);
    if (savedUser.isEmpty()) throw new UserNotFoundException(USER_NOT_FOUND);
    return USER_MAPPER.mapToUser(savedUser.get());
  }

  @Cacheable(value = "users_auth_ids", key = "#authId", unless = "#result == null")
  public User findByAuthId(final String authId) {
    final Optional<UsersRecord> user = dsl
      .selectFrom(USERS)
      .where(USERS.AUTH_ID.eq(authId))
      .fetchOptionalInto(UsersRecord.class);
    if (user.isEmpty()) throw new UserNotFoundException(USER_NOT_FOUND);
    return USER_MAPPER.mapToUser(user.get());
  }

  @Cacheable(value = "users", key = "#id", unless = "#result == null")
  public User findById(final Integer id) {
    final Optional<UsersRecord> user = dsl
      .selectFrom(USERS)
      .where(USERS.ID.eq(id))
      .fetchOptionalInto(UsersRecord.class);
    if (user.isEmpty()) throw new UserNotFoundException(USER_NOT_FOUND);
    return USER_MAPPER.mapToUser(user.get());
  }

  @CachePut(value = "users", key = "#id", unless = "#result == null")
  public User update(final Integer id, final UserUpdateRequest user) throws UserNotFoundException {
    final Optional<UsersRecord> updatedUser = dsl
      .update(USERS)
      .set(USERS.DISPLAY_NAME, user.displayName())
      .where(USERS.ID.eq(id))
      .returning()
      .fetchOptionalInto(UsersRecord.class);
    if (updatedUser.isEmpty()) throw new UserNotFoundException(USER_NOT_FOUND);
    return USER_MAPPER.mapToUser(updatedUser.get());
  }

  public boolean doesUserExist(final Integer id) {
    return !dsl.fetchExists(dsl.selectFrom(USERS).where(USERS.ID.eq(id)));
  }

  public boolean doesAuthUserExist(final String authId) {
    return dsl.fetchExists(dsl.selectFrom(USERS).where(USERS.AUTH_ID.eq(authId)));
  }
}
