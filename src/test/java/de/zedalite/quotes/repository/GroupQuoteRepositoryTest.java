package de.zedalite.quotes.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import de.zedalite.quotes.TestEnvironmentProvider;
import de.zedalite.quotes.data.jooq.quotes.tables.GroupQuotes;
import de.zedalite.quotes.data.model.GroupRequest;
import de.zedalite.quotes.data.model.Quote;
import de.zedalite.quotes.data.model.QuoteRequest;
import de.zedalite.quotes.data.model.UserRequest;
import de.zedalite.quotes.exception.QuoteNotFoundException;
import de.zedalite.quotes.fixtures.QuoteGenerator;
import java.util.List;
import java.util.Optional;
import org.jooq.DSLContext;
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
class GroupQuoteRepositoryTest extends TestEnvironmentProvider {

  private static final GroupQuotes GROUP_QUOTES = GroupQuotes.GROUP_QUOTES.as("group_quotes");

  @Autowired
  private GroupQuoteRepository instance;

  @Autowired
  private GroupRepository groupRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private DSLContext dsl;

  private Integer groupId;

  private Integer userId;

  @BeforeAll
  void setup() {
    userId = userRepository.save("usr-479832", new UserRequest("Quote Group")).id();
    groupId = groupRepository.save(new GroupRequest("quoter-group", "QuoterG"), userId).id();
    instance.save(groupId, QuoteGenerator.getQuoteRequest(), userId);
  }

  @Test
  @DisplayName("Should save group quote")
  void shouldSaveGroupQuote() {
    final QuoteRequest quoteRequest = QuoteGenerator.getQuoteRequest();

    final Quote newQuote = instance.save(groupId, quoteRequest, userId);

    assertThat(newQuote).isNotNull();
    assertThat(newQuote.id()).isNotNegative();
    assertThat(newQuote.author()).isEqualTo(quoteRequest.author());
    assertThat(newQuote.text()).isEqualTo(quoteRequest.text());
    assertThat(newQuote.context()).isEqualTo(quoteRequest.context());
    assertThat(newQuote.creatorId()).isEqualTo(Optional.of(userId));

    final boolean isInserted = dsl.fetchExists(
      dsl.selectFrom(GROUP_QUOTES).where(GROUP_QUOTES.GROUP_ID.eq(groupId).and(GROUP_QUOTES.QUOTE_ID.eq(newQuote.id())))
    );

    assertThat(isInserted).isTrue();
  }

  @Test
  @DisplayName("Should find group quote by id")
  void shouldFindGroupQuoteById() {
    final QuoteRequest quoteRequest = QuoteGenerator.getQuoteRequest();
    final Quote savedQuote = instance.save(groupId, quoteRequest, userId);

    final Quote newQuote = instance.findById(groupId, savedQuote.id());

    assertThat(newQuote).isNotNull();
    assertThat(newQuote.id()).isNotNegative();
    assertThat(newQuote.author()).isEqualTo(quoteRequest.author());
    assertThat(newQuote.text()).isEqualTo(quoteRequest.text());
    assertThat(newQuote.context()).isEqualTo(quoteRequest.context());
    assertThat(newQuote.creatorId()).isEqualTo(Optional.of(userId));
  }

  @Test
  @DisplayName("Should throw exception when group quote not found")
  void shouldThrowExceptionWhenGroupQuoteNotFound() {
    assertThatCode(() -> instance.findById(groupId, 987654321)).isInstanceOf(QuoteNotFoundException.class);
  }

  @Test
  @DisplayName("Should find all group quotes")
  void shouldFindAllGroupQuotes() {
    final List<Quote> quotes = instance.findAll(groupId);

    assertThat(quotes).hasSizeGreaterThanOrEqualTo(1);
  }

  @Test
  @DisplayName("Should throw exception when no group quotes found")
  void shouldThrowExceptionWhenNoGroupQuotesFound() {
    assertThatCode(() -> instance.findAll(987654321)).isInstanceOf(QuoteNotFoundException.class);
  }

  @Test
  @DisplayName("Should find group quote count")
  void shouldFindGroupQuoteCount() {
    final Integer count = instance.count(groupId);

    assertThat(count).isGreaterThan(1);
  }
}
