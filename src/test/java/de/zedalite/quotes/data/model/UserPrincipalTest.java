package de.zedalite.quotes.data.model;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserPrincipalTest {

  @Test
  @DisplayName("Password is not supported")
  void passwordIsNotSupported() {
    final UserPrincipal userPrincipal = new UserPrincipal(null, null);

    assertThrows(UnsupportedOperationException.class, userPrincipal::getPassword);
  }
}
