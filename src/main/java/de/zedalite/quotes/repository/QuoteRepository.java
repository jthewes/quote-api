package de.zedalite.quotes.repository;

import de.zedalite.quotes.data.jooq.quotes.tables.Quotes;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class QuoteRepository {

  private static final Quotes QUOTES = Quotes.QUOTES_.as("quotes");

  private final DSLContext dsl;

  public QuoteRepository(final DSLContext dsl) {
    this.dsl = dsl;
  }

  public Integer count() {
    return dsl.fetchCount(QUOTES);
  }
}
