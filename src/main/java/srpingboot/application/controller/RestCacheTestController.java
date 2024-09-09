package srpingboot.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import srpingboot.application.dto.TestInput;
import srpingboot.application.dto.TestResult;
import srpingboot.application.service.CaffeineCacheService;
import srpingboot.application.service.RedisCacheService;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Cache Test API", description = "스프링부트 캐쉬 테스트 API")
@RequestMapping("/api/")
public class RestCacheTestController {

    private final CaffeineCacheService caffeineCacheService;
    private final RedisCacheService redisCacheService;

    @Operation(summary = "Caffeine 내부 캐쉬 테스트", description = "Caffeine 내부 캐쉬 테스트")
    @GetMapping(value = "caffeine")
    public ResponseEntity<TestResult> caffeineCache(@Validated @ParameterObject TestInput request) {
        long before = System.currentTimeMillis();
        TestResult result = caffeineCacheService.enableCache(request);
        long after = System.currentTimeMillis();

        log.debug("조회 소요시간 : " + (after - before));

        return ResponseEntity.ok().body(result);
    }

    @Operation(summary = "Caffeine 내부 캐쉬 제거 테스트", description = "Caffeine 내부 캐쉬 제거 테스트")
    @GetMapping(value = "caffeine/evict")
    public ResponseEntity<TestResult> caffeineEvictCache(@Validated @ParameterObject TestInput request) {
        TestResult result = caffeineCacheService.evictCache(request);

        return ResponseEntity.ok().body(result);
    }

    @Operation(summary = "Redis 외부 캐쉬 테스트", description = "Redis 외부 캐쉬 테스트")
    @GetMapping(value = "redis")
    public ResponseEntity<TestResult> redisCache(@Validated @ParameterObject TestInput request) {
        long before = System.currentTimeMillis();
        TestResult result = redisCacheService.enableCache(request);
        long after = System.currentTimeMillis();

        log.debug("조회 소요시간 : " + (after - before));

        return ResponseEntity.ok().body(result);
    }
}
