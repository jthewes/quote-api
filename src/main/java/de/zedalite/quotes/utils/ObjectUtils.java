package de.zedalite.quotes.utils;

public final class ObjectUtils {

  ObjectUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static <T> T requireNonNullElse(T obj, T defaultObj) {
    return obj != null ? obj : defaultObj;
  }
}
