package srpingboot.application.common.enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CaffeineCacheType {
    CAFFEINE_CACHE("caffeineCache", 1 * 60, 1000);

    private final String cacheName;         // 캐시 이름
    private final int expireAfterWrite;     // 만료 시간
    private final int maximumSize;          // 캐슁 최대 개수
}
