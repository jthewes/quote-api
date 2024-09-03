package de.zedalite.quotes.repository;

import de.zedalite.auth.client.ApiClient;
import de.zedalite.auth.client.api.UserApi;
import de.zedalite.quotes.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
public class AuthenticationServerRepository {

  private final UserApi userApi;

  public AuthenticationServerRepository(
    final @Value("${app.security.auth-server.user}") String user,
    final @Value("${app.security.auth-server.password}") String pw
  ) {
    final ApiClient apiClient = new ApiClient();
    apiClient.setUsername(user);
    apiClient.setPassword(pw);
    this.userApi = new UserApi(apiClient);
  }

  @Cacheable(value = "auth_usernames", key = "{#userId}", unless = "#result == null")
  public String getUsername(final String userId) {
    try {
      return userApi.userGet(userId, null, null).getData().getFullName();
    } catch (Exception e) {
      throw new UserNotFoundException("Error while fetching user data from authentication server");
    }
  }
}
