package de.zedalite.quotes.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import de.zedalite.quotes.TestEnvironmentProvider;
import de.zedalite.quotes.data.model.*;
import de.zedalite.quotes.exception.QotdNotFoundException;
import java.time.LocalDate;
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
class GroupQuoteOfTheDayRepositoryTest extends TestEnvironmentProvider {

  @Autowired
  private GroupQuoteOfTheDayRepository instance;

  @Autowired
  private GroupQuoteRepository groupQuoteRepository;

  @Autowired
  private GroupRepository groupRepository;

  @Autowired
  private UserRepository userRepository;

  private Integer exampleQuoteId;

  private Integer exampleGroupId;

  @BeforeAll
  void setup() {
    Integer exampleUserId = userRepository.save(new UserRequest("qotd_user", "QOTD_USER")).id();
    exampleGroupId = groupRepository.save(new GroupRequest("qotd_group", "QOTD"), exampleUserId).id();
    exampleQuoteId = groupQuoteRepository
      .save(exampleGroupId, new QuoteRequest("qotd", "I'm the best", LocalDateTime.now(), null), exampleUserId)
      .id();
    instance.save(exampleGroupId, new QuoteOfTheDayRequest(exampleQuoteId, LocalDate.now()));
  }

  @Test
  @DisplayName("Should save quote of the day")
  void shouldSaveQuoteOfTheDay() {
    final QuoteOfTheDayRequest qotd = new QuoteOfTheDayRequest(exampleQuoteId, LocalDate.now());

    final QuoteOfTheDay savedQotd = instance.save(exampleGroupId, qotd);

    assertThat(savedQotd).isNotNull();
    assertThat(savedQotd.quoteId()).isEqualTo(exampleQuoteId);
  }

  @Test
  @DisplayName("Should find quote of the day according to current date")
  void shouldFindQuoteOfTheDayAccordingToCurrentDate() {
    final Quote qotd = instance.findByDate(exampleGroupId, LocalDate.now());

    assertThat(qotd).isNotNull();
    assertThat(qotd.id()).isEqualTo(exampleQuoteId);
  }

  @Test
  @DisplayName("Should throw exception if quote of the day not found")
  void shouldThrowExceptionIfQuoteOfTheDayNotFound() {
    final LocalDate future = LocalDate.now().plusDays(1);

    assertThatCode(() -> instance.findByDate(exampleGroupId, future)).isInstanceOf(QotdNotFoundException.class);
  }
}
