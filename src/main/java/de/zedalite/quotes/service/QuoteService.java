package de.zedalite.quotes.service;

import de.zedalite.quotes.data.model.CountResponse;
import de.zedalite.quotes.repository.QuoteRepository;
import org.springframework.stereotype.Service;

@Service
public class QuoteService {

  private final QuoteRepository repository;

  public QuoteService(final QuoteRepository repository) {
    this.repository = repository;
  }

  public CountResponse count() {
    return new CountResponse(repository.count());
  }
}
