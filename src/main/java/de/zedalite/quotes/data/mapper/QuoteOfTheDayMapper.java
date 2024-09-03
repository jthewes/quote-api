package de.zedalite.quotes.data.mapper;

import de.zedalite.quotes.data.jooq.quotes.tables.records.QuotesOfTheDayRecord;
import de.zedalite.quotes.data.model.QuoteOfTheDay;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Defines the mapping between the QuotesOfTheDayRecord entity and the QuoteOfTheDay object.
 */
@Mapper
public interface QuoteOfTheDayMapper {
  QuoteOfTheDayMapper INSTANCE = Mappers.getMapper(QuoteOfTheDayMapper.class);

  QuoteOfTheDay mapToQuoteOfTheDay(final QuotesOfTheDayRecord quotesOfTheDayRecord);
}
