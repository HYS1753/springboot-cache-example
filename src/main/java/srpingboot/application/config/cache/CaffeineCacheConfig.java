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
                                        .softValues()       // 메모리가 충분할 때는 GC에 의해 회수되지 않고, 시스템 메모리 부족 상태일 때 소프트 참조된 객체를 회수 (weakKey, weakValue의 경우 강함 참조가 없어지면 GC에 의해 언제든지 제거될 수 있음)
                                        .build()))
                        .toList();

        cacheManager.setCaches(caches);
        return cacheManager;
    }

}
