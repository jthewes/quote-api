package de.zedalite.quotes.exception;

public class QuoteNotFoundException extends RuntimeException {

  public QuoteNotFoundException(final String message) {
    super(message);
  }
}
