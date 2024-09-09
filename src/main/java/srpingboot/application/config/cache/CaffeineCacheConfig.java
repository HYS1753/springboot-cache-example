package srpingboot.application.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Primary;
import srpingboot.application.common.enumerations.CaffeineCacheType;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineCacheConfig {

    /**
     * Caffeine 내부 캐쉬 설정
     * @return
     */
    @Primary
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
