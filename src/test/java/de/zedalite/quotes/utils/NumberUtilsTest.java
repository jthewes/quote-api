package de.zedalite.quotes.utils;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class NumberUtilsTest {

  @Test
  @DisplayName("Should throw when constructor called")
  void shouldThrowWhenConstructorCalled() {
    assertThatCode(NumberUtils::new).isInstanceOf(IllegalStateException.class);
  }

  @Test
  @DisplayName("Test with valid integer string")
  void testIsParsableToInt_withValidInteger() {
    assertTrue(NumberUtils.isParsableToInt("1234"));
  }

  @Test
  @DisplayName("Test with invalid integer string")
  void testIsParsableToInt_withInvalidInteger() {
    assertFalse(NumberUtils.isParsableToInt("1234abc"));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("Test with null string")
  void testIsParsableToInt_withNull(final String input) {
    assertFalse(NumberUtils.isParsableToInt(input));
  }
}
