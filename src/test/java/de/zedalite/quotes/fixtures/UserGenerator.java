package de.zedalite.quotes.fixtures;

import de.zedalite.quotes.data.model.*;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class UserGenerator {

  // TODO replace test fixures with mocks in test for better extensibility

  public static UserRequest getUserRequest() {
    return new UserRequest("tester", "apple", "The Tester");
  }

  public static User getUser() {
    return new User(1, "tester", "apple", "TESTER", LocalDateTime.now());
  }

  public static UserResponse getUserResponse() {
    return new UserResponse(1, "tester", "TESTER", LocalDateTime.now());
  }

  public static UserPrincipal getUserPrincipal() {
    return new UserPrincipal(
      new User(1, "tester", "test", "TESTER", LocalDateTime.now()),
      List.of(new SimpleGrantedAuthority("ROLE_MEMBER"))
    );
  }

  public static List<User> getUsers() {
    return List.of(
      new User(1, "tester", "apple", "TESTER", LocalDateTime.now()),
      new User(2, "manager", "best", "MANAGER", LocalDateTime.MIN)
    );
  }
}
