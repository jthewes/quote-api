package de.zedalite.quotes;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class TestEnvironmentProvider {

  static PostgreSQLContainer<?> dbContainer;

  static {
    // No try-with-resources is intentionally used here
    // "The singleton container is started only once when the base class is loaded. The container can then be used by all inheriting test classes.
    // At the end of the test suite the Ryuk container that is started by Testcontainers core will take care of stopping the singleton container."
    // See: https://java.testcontainers.org/test_framework_integration/manual_lifecycle_control/
    dbContainer = new PostgreSQLContainer<>("postgres:latest").withInitScript("database.sql");
    dbContainer.start();
  }

  @DynamicPropertySource
  static void registerMySQLProperties(final DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", dbContainer::getJdbcUrl);
    registry.add("spring.datasource.username", dbContainer::getUsername);
    registry.add("spring.datasource.password", dbContainer::getPassword);
  }
}
