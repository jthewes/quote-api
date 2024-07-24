package de.zedalite.quotes.repository;

import de.zedalite.quotes.data.jooq.quotes.tables.GroupQuotes;
import de.zedalite.quotes.data.jooq.quotes.tables.Quotes;
import de.zedalite.quotes.data.jooq.quotes.tables.records.QuotesRecord;
import de.zedalite.quotes.data.mapper.QuoteMapper;
import de.zedalite.quotes.data.model.Quote;
import de.zedalite.quotes.data.model.QuoteRequest;
import de.zedalite.quotes.exception.QuoteNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;

@Repository
public class GroupQuoteRepository {

  private static final QuoteMapper QUOTE_MAPPER = QuoteMapper.INSTANCE;

  private static final Quotes QUOTES = Quotes.QUOTES_.as("quotes");

  private static final GroupQuotes GROUP_QUOTES = GroupQuotes.GROUP_QUOTES.as("group_quotes");

  private static final String GROUP_QUOTE_NOT_FOUND = "Group quote not found";

  private final DSLContext dsl;

  public GroupQuoteRepository(final DSLContext dsl) {
    this.dsl = dsl;
  }

  @Caching(
    put = @CachePut(value = "groupquotes", key = "{#id,#result.id}", unless = "#result == null"),
    evict = @CacheEvict(value = "groupquotes_quotes", key = "{#id}")
  )
  public Quote save(final Integer id, final QuoteRequest quote, final Integer creatorId) {
    final Optional<QuotesRecord> savedQuoteRec = dsl
      .insertInto(QUOTES)
      .set(QUOTES.AUTHOR, quote.author())
      .set(QUOTES.CREATION_DATE, Objects.requireNonNullElse(quote.creationDate(), LocalDateTime.now()))
      .set(QUOTES.TEXT, quote.text())
      .set(QUOTES.CONTEXT, quote.context())
      .set(QUOTES.CREATOR_ID, creatorId)
      .returning()
      .fetchOptionalInto(QuotesRecord.class);
    if (savedQuoteRec.isEmpty()) throw new QuoteNotFoundException(GROUP_QUOTE_NOT_FOUND);
    final Quote savedQuote = QUOTE_MAPPER.mapToQuote(savedQuoteRec.get());

    dsl.insertInto(GROUP_QUOTES).set(GROUP_QUOTES.GROUP_ID, id).set(GROUP_QUOTES.QUOTE_ID, savedQuote.id()).execute();

    return savedQuote;
  }

  @Cacheable(value = "groupquotes", key = "{#id,#quoteId}", unless = "#result = null")
  public Quote findById(final Integer id, final Integer quoteId) {
    final Optional<Quote> quote = dsl
      .select(QUOTES)
      .from(GROUP_QUOTES.join(QUOTES).on(GROUP_QUOTES.QUOTE_ID.eq(QUOTES.ID)))
      .where(GROUP_QUOTES.GROUP_ID.eq(id).and(GROUP_QUOTES.QUOTE_ID.eq(quoteId)))
      .fetchOptionalInto(Quote.class);
    if (quote.isEmpty()) throw new QuoteNotFoundException(GROUP_QUOTE_NOT_FOUND);
    return quote.get();
  }

  // TODO implement caching, optimise with single caching or learn how to manipulate the cache to insert multiple values
  @Cacheable(value = "groupquotes_quotes", key = "{#id}", unless = "#result = null")
  public List<Quote> findAll(final Integer id) {
    final List<Quote> quotes = dsl
      .select(QUOTES)
      .from(GROUP_QUOTES.join(QUOTES).on(GROUP_QUOTES.QUOTE_ID.eq(QUOTES.ID)))
      .where(GROUP_QUOTES.GROUP_ID.eq(id))
      .fetchInto(Quote.class);
    if (quotes.isEmpty()) throw new QuoteNotFoundException(GROUP_QUOTE_NOT_FOUND);
    return quotes;
  }

  public Integer count(final Integer id) {
    return dsl.fetchCount(GROUP_QUOTES, GROUP_QUOTES.GROUP_ID.eq(id));
  }
}
