package de.zedalite.quotes.exception;

public class ResourceAlreadyExitsException extends RuntimeException {

  public ResourceAlreadyExitsException(final String message) {
    super(message);
  }
}
