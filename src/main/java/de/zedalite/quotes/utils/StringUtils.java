package de.zedalite.quotes.utils;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public final class StringUtils {

  StringUtils() {
    throw new IllegalStateException("Utility class");
  }

  private static final Pattern USER_ID_PATTERN = Pattern.compile("<@(\\d+)>");

  public static List<Integer> extractUserIds(final String text) {
    return USER_ID_PATTERN.matcher(text)
      .results()
      .map(result -> parseToInt(result.group(1)))
      .filter(Objects::nonNull)
      .toList();
  }

  public static String truncate(final String text, final int length) {
    return text.length() > length ? text.substring(0, length) : text;
  }

  private static Integer parseToInt(final String number) {
    try {
      return Integer.parseInt(number);
    } catch (final NumberFormatException e) {
      return null;
    }
  }
}
