package de.zedalite.quotes.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class QuoteOfTheDayScheduler {

  private static final Logger LOGGER = LoggerFactory.getLogger(QuoteOfTheDayScheduler.class);

  @Scheduled(cron = "@daily")
  @CacheEvict(value = "qotd", allEntries = true)
  public void resetQuoteOfTheDay() {
    LOGGER.info("Cache evicted for quote of the day.");
  }
}
