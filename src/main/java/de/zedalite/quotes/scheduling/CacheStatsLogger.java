package de.zedalite.quotes.scheduling;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CacheStatsLogger {

  private static final Logger LOGGER = LoggerFactory.getLogger(CacheStatsLogger.class);

  private final CacheManager cacheManager;

  public CacheStatsLogger(final CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @Scheduled(cron = "@hourly")
  public void logCacheStats() {
    cacheManager
      .getCacheNames()
      .forEach(cacheName -> {
        if (cacheManager.getCache(cacheName) instanceof CaffeineCache caffeine) {
          final Cache<Object, Object> nativeCache = caffeine.getNativeCache();
          final CacheStats stats = nativeCache.stats();
          LOGGER.info(
            "Stats {}: estimatedSize={}; hitCount={}; missCount={}",
            cacheName,
            nativeCache.estimatedSize(),
            String.format("%.2f%%", stats.hitRate() * 100),
            String.format("%.2f%%", stats.missRate() * 100)
          );
        }
      });
  }
}
