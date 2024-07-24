package de.zedalite.quotes.service;

import de.zedalite.quotes.data.mapper.QuoteMapper;
import de.zedalite.quotes.data.model.CountResponse;
import de.zedalite.quotes.data.model.FilterKey;
import de.zedalite.quotes.data.model.Quote;
import de.zedalite.quotes.data.model.QuoteRequest;
import de.zedalite.quotes.data.model.QuoteResponse;
import de.zedalite.quotes.data.model.SortField;
import de.zedalite.quotes.data.model.SortMode;
import de.zedalite.quotes.data.model.User;
import de.zedalite.quotes.exception.QuoteNotFoundException;
import de.zedalite.quotes.exception.ResourceNotFoundException;
import de.zedalite.quotes.repository.GroupQuoteRepository;
import de.zedalite.quotes.repository.UserRepository;
import de.zedalite.quotes.utils.NumberUtils;
import de.zedalite.quotes.utils.StringUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import org.springframework.stereotype.Service;

@Service
public class GroupQuoteService {

  private static final QuoteMapper QUOTE_MAPPER = QuoteMapper.INSTANCE;

  private final GroupQuoteRepository repository;

  private final UserRepository userRepository;

  public GroupQuoteService(final GroupQuoteRepository repository, final UserRepository userRepository) {
    this.repository = repository;
    this.userRepository = userRepository;
  }

  public QuoteResponse create(final Integer id, final QuoteRequest request, final Integer creatorId) {
    try {
      return getResponse(repository.save(id, request, creatorId));
    } catch (final QuoteNotFoundException ex) {
      throw new ResourceNotFoundException(ex.getMessage());
    }
  }

  public List<QuoteResponse> findAll(
    final Integer id,
    final FilterKey filterKey,
    final String filterValue,
    final SortField sortField,
    final SortMode sortMode
  ) {
    try {
      final List<Quote> quotes = repository.findAll(id);

      final List<QuoteResponse> quoteResponses = new ArrayList<>(
        quotes
          .stream()
          .filter(getQuoteFilter(filterKey, filterValue))
          .sorted(getQuoteComparator(sortField, sortMode))
          .map(this::getResponse)
          .toList()
      );

      if (quoteResponses.isEmpty()) {
        throw new ResourceNotFoundException("Group quote not found");
      }

      if (SortMode.RANDOM.equals(sortMode)) {
        Collections.shuffle(quoteResponses);
      }

      return quoteResponses;
    } catch (final QuoteNotFoundException ex) {
      throw new ResourceNotFoundException(ex.getMessage());
    }
  }

  public QuoteResponse find(final Integer id, final Integer quoteId) {
    try {
      final Quote quote = repository.findById(id, quoteId);
      return getResponse(quote);
    } catch (final QuoteNotFoundException ex) {
      throw new ResourceNotFoundException(ex.getMessage());
    }
  }

  public CountResponse count(final Integer id) {
    return new CountResponse(repository.count(id));
  }

  private static Predicate<Quote> getQuoteFilter(final FilterKey key, final String value) {
    if (value == null || value.isBlank()) return quote -> true;

    return quote ->
      switch (key) {
        case AUTHOR -> quote.author().equals(value);
        case CREATOR -> quote.creatorId().isPresent() &&
        NumberUtils.isParsableToInt(value) &&
        quote.creatorId().get().equals(Integer.parseInt(value));
        case TEXT -> quote.text().toLowerCase().contains(value.toLowerCase());
        case CONTEXT -> quote.context() != null && quote.context().toLowerCase().contains(value.toLowerCase());
        case null, default -> true;
      };
  }

  private static Comparator<Quote> getQuoteComparator(final SortField field, final SortMode order) {
    return (quote1, quote2) -> {
      Comparator<Quote> byField =
        switch (field) {
          case CREATION_DATE -> Comparator.comparing(Quote::creationDate);
          case null, default -> (o1, o2) -> 0;
        };

      return switch (order) {
        case ASCENDING -> byField.compare(quote1, quote2);
        case DESCENDING -> byField.reversed().compare(quote1, quote2);
        case null, default -> 0;
      };
    };
  }

  private QuoteResponse getResponse(final Quote quote) {
    final List<User> mentions = getMentions(StringUtils.extractUserIds(quote.text()));

    return QUOTE_MAPPER.mapToResponse(quote, mentions);
  }

  private List<User> getMentions(final List<Integer> userIds) {
    return userIds.isEmpty() ? Collections.emptyList() : userIds.stream().map(userRepository::findById).toList();
  }
}
