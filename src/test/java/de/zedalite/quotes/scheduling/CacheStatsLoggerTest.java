package de.zedalite.quotes.scheduling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;

@ExtendWith({ MockitoExtension.class, OutputCaptureExtension.class })
class CacheStatsLoggerTest {

  @Mock
  private CacheManager cacheManager;

  @InjectMocks
  private CacheStatsLogger cacheStatsLogger;

  @Test
  @DisplayName("Should log cache stats")
  void shouldLogCacheStats(final CapturedOutput output) {
    final CaffeineCache caffeineCacheMock = mock(CaffeineCache.class);
    final Cache<Object, Object> cacheMock = mock(Cache.class);
    final CacheStats cacheStatsMock = mock(CacheStats.class);

    given(cacheManager.getCacheNames()).willReturn(List.of("cache1", "cache2"));
    given(cacheManager.getCache(anyString())).willReturn(caffeineCacheMock);
    given(caffeineCacheMock.getNativeCache()).willReturn(cacheMock);
    given(cacheMock.stats()).willReturn(cacheStatsMock);

    cacheStatsLogger.logCacheStats();

    then(cacheManager).should().getCacheNames();
    then(cacheManager).should().getCache("cache1");
    then(cacheManager).should().getCache("cache2");
    then(caffeineCacheMock).should(times(2)).getNativeCache();
    then(cacheMock).should(times(2)).stats();
    then(cacheStatsMock).should(times(2)).hitRate();
    then(cacheStatsMock).should(times(2)).missRate();
    assertThat(output).contains(
      "Stats cache1: estimatedSize=",
      "hitCount=",
      "missCount=",
      "Stats cache2: estimatedSize=",
      "hitCount=",
      "missCount="
    );
  }

  @Test
  @DisplayName("Should log nothing when caches are empty")
  void shouldLogNothingWhenCachesAreEmpty() {
    final CaffeineCache caffeineCacheMock = mock(CaffeineCache.class);
    final Cache<Object, Object> cacheMock = mock(Cache.class);
    final CacheStats cacheStatsMock = mock(CacheStats.class);

    given(cacheManager.getCacheNames()).willReturn(Collections.emptyList());

    cacheStatsLogger.logCacheStats();

    then(cacheManager).should().getCacheNames();
    then(cacheManager).shouldHaveNoMoreInteractions();
    then(caffeineCacheMock).shouldHaveNoMoreInteractions();
    then(cacheMock).shouldHaveNoMoreInteractions();
    then(cacheStatsMock).shouldHaveNoMoreInteractions();
  }
}
