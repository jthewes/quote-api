package de.zedalite.quotes.service;

import de.zedalite.quotes.data.mapper.QuoteMapper;
import de.zedalite.quotes.data.model.Quote;
import de.zedalite.quotes.data.model.QuoteOfTheDayRequest;
import de.zedalite.quotes.data.model.QuoteResponse;
import de.zedalite.quotes.data.model.User;
import de.zedalite.quotes.exception.QotdNotFoundException;
import de.zedalite.quotes.exception.ResourceNotFoundException;
import de.zedalite.quotes.repository.GroupQuoteOfTheDayRepository;
import de.zedalite.quotes.repository.GroupQuoteRepository;
import de.zedalite.quotes.repository.UserRepository;
import de.zedalite.quotes.utils.StringUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GroupQuoteOfTheDayService {

  private static final QuoteMapper QUOTE_MAPPER = QuoteMapper.INSTANCE;

  private static final String MIN_QUOTES_COUNT = "Minimum count of quotes not reached (10)";

  private final GroupQuoteOfTheDayRepository repository;

  private final GroupQuoteRepository groupQuoteRepository;

  private final UserRepository userRepository;

  public GroupQuoteOfTheDayService(
    final GroupQuoteOfTheDayRepository repository,
    final GroupQuoteRepository groupQuoteRepository,
    final UserRepository userRepository
  ) {
    this.repository = repository;
    this.groupQuoteRepository = groupQuoteRepository;
    this.userRepository = userRepository;
  }

  public QuoteResponse findQuoteOfTheDay(final Integer id) throws ResourceNotFoundException {
    if (groupQuoteRepository.count(id) < 10) throw new ResourceNotFoundException(MIN_QUOTES_COUNT);

    Quote qotd;
    try {
      qotd = repository.findByDate(id, LocalDate.now());
    } catch (final QotdNotFoundException ex) {
      final ArrayList<Quote> allQuotes = new ArrayList<>(groupQuoteRepository.findAll(id));
      Collections.shuffle(allQuotes);
      qotd = allQuotes.getFirst();
      repository.save(id, new QuoteOfTheDayRequest(qotd.id(), LocalDate.now()));
    }

    return getResponse(qotd);
  }

  private QuoteResponse getResponse(final Quote quote) {
    final List<User> mentions = getMentions(StringUtils.extractUserIds(quote.text()));

    return QUOTE_MAPPER.mapToResponse(quote, mentions);
  }

  private List<User> getMentions(final List<Integer> userIds) {
    return userIds.isEmpty() ? Collections.emptyList() : userIds.stream().map(userRepository::findById).toList();
  }
}
