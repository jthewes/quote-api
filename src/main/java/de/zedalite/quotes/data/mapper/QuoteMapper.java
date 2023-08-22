package de.zedalite.quotes.data.mapper;

import de.zedalite.quotes.data.jooq.tables.records.QuotesRecord;
import de.zedalite.quotes.data.model.Quote;
import de.zedalite.quotes.data.model.QuoteMessage;
import de.zedalite.quotes.data.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * This class is responsible for mapping the QuotesRecord entity to Quote entity.
 */
@Mapper
public interface QuoteMapper {
  QuoteMapper INSTANCE = Mappers.getMapper(QuoteMapper.class);

  Quote quoteRecToQuote(final QuotesRecord quotesRecord);

  List<Quote> quoteRecsToQuotes(final List<QuotesRecord> quotesRecords);

  QuoteMessage quoteToQuoteMsg(final Quote quote, List<User> mentions);
}
