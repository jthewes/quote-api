package de.zedalite.quotes.repository;

import static org.assertj.core.api.Assertions.assertThat;

import de.zedalite.quotes.TestEnvironmentProvider;
import de.zedalite.quotes.data.model.GroupRequest;
import de.zedalite.quotes.data.model.QuoteRequest;
import de.zedalite.quotes.data.model.UserRequest;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(value = "classpath:test-no-cache.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QuoteRepositoryTest extends TestEnvironmentProvider {

  @Autowired
  private QuoteRepository instance;

  @Autowired
  private GroupRepository groupRepository;

  @Autowired
  private GroupQuoteRepository groupQuoteRepository;

  @Autowired
  private UserRepository userRepository;

  @BeforeAll
  void setup() {
    final Integer userId = userRepository.save(new UserRequest("quotetester", "Quote Tester")).id();
    final Integer userId2 = userRepository.save(new UserRequest("quotetester2", "Quote Tester 2")).id();

    final Integer groupId = groupRepository.save(new GroupRequest("group1", "GROUP 1"), userId).id();

    groupQuoteRepository.save(
      groupId,
      new QuoteRequest("quoter", "quotes are cool", LocalDateTime.now(), "in quotversum"),
      null
    );
    groupQuoteRepository.save(
      groupId,
      new QuoteRequest("quoter", "One more quotes", LocalDateTime.now(), "#2"),
      userId
    );
    groupQuoteRepository.save(
      groupId,
      new QuoteRequest("quotexpert", "I'm an expert", LocalDateTime.now(), null),
      userId2
    );
  }

  @Test
  @DisplayName("Should find quote count")
  void shouldFindQuoteCount() {
    final Integer quoteCount = instance.count();

    assertThat(quoteCount).isGreaterThanOrEqualTo(3);
  }
}
