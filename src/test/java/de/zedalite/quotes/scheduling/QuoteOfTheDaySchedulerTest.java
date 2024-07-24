package de.zedalite.quotes.scheduling;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(OutputCaptureExtension.class)
class QuoteOfTheDaySchedulerTest {

  private final QuoteOfTheDayScheduler instance = new QuoteOfTheDayScheduler();

  @Test
  @DisplayName("Should reset quoteOfTheDay")
  void shouldResetQuoteOfTheDayCache(final CapturedOutput output) {
    instance.resetQuoteOfTheDay();
    assertThat(output).contains("Cache evicted for quote of the day.");
  }
}
