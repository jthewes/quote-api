package de.zedalite.quotes.utils;

public final class NumberUtils {

  NumberUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static boolean isParsableToInt(String str) {
    try {
      Integer.parseInt(str);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
