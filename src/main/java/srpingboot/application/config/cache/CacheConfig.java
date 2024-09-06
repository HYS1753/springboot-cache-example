package srpingboot.application.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.checkerframework.checker.units.qual.C;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.caffeine.CaffeineCache;
import srpingboot.application.common.enumerations.CaffeineCacheType;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager CaffeineCacheManager() {
        final SimpleCacheManager cacheManager = new SimpleCacheManager();

        List<CaffeineCache> caches =
                Arrays.stream(CaffeineCacheType.values())
                        .map(cache -> new CaffeineCache(
                                cache.getCacheName(),
                                Caffeine.newBuilder()
                                        .recordStats()
                                        .expireAfterWrite(cache.getExpireAfterWrite(), TimeUnit.SECONDS)
                                        .maximumSize(cache.getMaximumSize())
                                        .build()))
                        .toList();

        cacheManager.setCaches(caches);
        return cacheManager;
    }
}
