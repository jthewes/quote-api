package de.zedalite.quotes.data.mapper;

import de.zedalite.quotes.data.jooq.quotes.tables.records.QuotesRecord;
import de.zedalite.quotes.data.model.Quote;
import de.zedalite.quotes.data.model.QuoteResponse;
import de.zedalite.quotes.data.model.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * This class is responsible for mapping the QuotesRecord entity to Quote entity.
 */
@Mapper(uses = { OptionalMapper.class, UserMapper.class })
public interface QuoteMapper {
  QuoteMapper INSTANCE = Mappers.getMapper(QuoteMapper.class);

  Quote mapToQuote(final QuotesRecord quotesRecord);

  List<Quote> mapToQuoteList(final List<QuotesRecord> quotesRecords);

  QuoteResponse mapToResponse(final Quote quote, List<User> mentions);
}
