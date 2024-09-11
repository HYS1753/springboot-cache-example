package srpingboot.application.service.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import srpingboot.application.dto.TestInput;
import srpingboot.application.dto.TestResult;
import srpingboot.application.service.RedisCacheService;

@Service
@Slf4j
public class RedisCacheServiceImpl implements RedisCacheService {

    @Override
    @Cacheable(cacheNames = "redisCache", key = "#request.param1", cacheManager = "RedisCacheManager")
    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "enableCacheFallback")
    public TestResult enableCache(TestInput request) {
        log.debug("Redis Cache 초기화");
        this.pauseProcess(1000);
        return TestResult.builder()
                .message("success - " + request.getParam2())
                .build();
    }

    @Override
    @CacheEvict(cacheNames = "redisCache", key = "#request.param1", cacheManager = "RedisCacheManager")
    public TestResult evictCache(TestInput request) {
        return TestResult.builder()
                .message("evict cache key : " + request.getParam1())
                .build();
    }

    private TestResult enableCacheFallback(TestInput request, Throwable throwable) {
        log.debug("Redis 조회 실패. fallback 함수 실행. Error : " + throwable.getMessage());
        this.pauseProcess(1000);
        return TestResult.builder()
                .message("success - " + request.getParam2())
                .build();
    }

    private void pauseProcess(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
