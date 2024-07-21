package de.zedalite.quotes.utils;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ObjectUtilsTest {

  @Test
  @DisplayName("Should throw when constructor called")
  void shouldThrowWhenConstructorCalled() {
    assertThatCode(ObjectUtils::new).isInstanceOf(IllegalStateException.class);
  }

  @Test
  @DisplayName("Test with non-null object")
  void testRequireNonNullElse_withNonNullObject() {
    final Object obj = new Object();
    final Object defaultObj = new Object();
    assertSame(obj, ObjectUtils.requireNonNullElse(obj, defaultObj));
  }

  @Test
  @DisplayName("Test with null object")
  void testRequireNonNullElse_withNullObject() {
    final Object defaultObj = new Object();
    assertSame(defaultObj, ObjectUtils.requireNonNullElse(null, defaultObj));
  }

  @Test
  @DisplayName("Test with null default object")
  void testRequireNonNullElse_withNullDefaultObject() {
    final Object obj = new Object();
    assertSame(obj, ObjectUtils.requireNonNullElse(obj, null));
  }
}
