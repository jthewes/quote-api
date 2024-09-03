package de.zedalite.quotes.exception;

public class QotdNotFoundException extends RuntimeException {

  public QotdNotFoundException(final String message) {
    super(message);
  }
}
