package de.zedalite.quotes.data.mapper;

import de.zedalite.quotes.data.jooq.tables.records.QuotesOfTheDayRecord;
import de.zedalite.quotes.data.model.QuoteOfTheDay;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface QuoteOfTheDayMapper {
  QuoteOfTheDayMapper INSTANCE = Mappers.getMapper(QuoteOfTheDayMapper.class);

  QuoteOfTheDay toQuoteOfTheDay(final QuotesOfTheDayRecord quotesOfTheDayRecord);
}