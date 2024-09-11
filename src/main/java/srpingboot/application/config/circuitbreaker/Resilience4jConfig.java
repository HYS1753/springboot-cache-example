package srpingboot.application.config.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class Resilience4jConfig {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Bean
    public CircuitBreaker customCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)                             // 실패율 임계값 (퍼센트), 실패율이 임계값보다 크거나 같다면, 서킷브레이커는 상태를 OPEN으로 변경하고 모든 호출을 차단한다. (Default : 50)
                .slowCallRateThreshold(100)                           // 느린 호출 임계 값 (퍼센트), 서킷브레이커는 호출 시간이 slowCallDurationThreshold 보다 길어진다면 느린 호출로 판단. 그렇게 판단된 느린 호출의 비율이 임계값 보다 크거나 같다면 서킷브레이커는 상태를 OPEN으로 변경하고 모든 호출을 차단한다. (Default : 100)
                .slowCallDurationThreshold(Duration.ofMillis(1000))   // 호출 시간 임계값. 해당 시간보다 호출 시간이 길어진다면 서킷브레이커는 해당 호출을 느린 호출으로 판단한다.(Default : 60000ms)
                .permittedNumberOfCallsInHalfOpenState(10)            // 서킷브레이커의 상태가 HALF_OPEN 일때 허용되는 호출의 수. (Default : 10)
                .maxWaitDurationInHalfOpenState(Duration.ofMillis(0)) // 서킷브레이커가 HALF_OPEN 상태에서 가장 오래 대기하는 시간, 해당 시간 이후 OPEN으로 변경된다. 값이 0 이면 서킷브레이커가 허용된 모든 호출이 완료될 때 까지 HALF_OPEN 상태에서 대기하는 것을 의미. (Default : 0)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)  // 슬라이딩 윈도우에서 서킷 브레이커가 CLOSED일 때 값을 집계 할 방식. (COUNT_BASED, TIME_BASED) (Default : COUNT_BASED)
                .slidingWindowSize(10)                                // 슬라이딩 윈도우의 크기. 해당 값은 서킷 브레이커가 CLOSED인 경우 값을 집계 할 때 사용. (Default : 100)
                .minimumNumberOfCalls(10)                             // 서킷브레이커가 값을 집계하기 전 최소 호출해야 하는 횟수. (Default : 100)
                .waitDurationInOpenState(Duration.ofMillis(1000))     // 서킷브레이커가 상태를 OPEN에서 HALF_OPEN으로 변경시키기 전까지 대기하는 시간. (Default : 60000ms)
                .automaticTransitionFromOpenToHalfOpenEnabled(false)  // OPEN에서 HALF_OPEN 으로 넘어갈 떄, 자동으로 넘어갈지 여부. false 라면 waitDurationOpenState 시간이 지나가더라도 어떠한 호출이 일어나지 않으면 상태 변경이 이뤄지지 않음. (Default : false)
                .recordExceptions(                                    // 실패로 측정될 exception list
                        List.of(java.net.SocketTimeoutException.class, java.net.ConnectException.class, org.springframework.data.redis.RedisSystemException.class).toArray(new Class[0])
                )
                .ignoreExceptions(                                    // 실패와 성공 둘 다로 측정되지 않을 exception list.
                        List.of(java.lang.IllegalStateException.class).toArray(new Class[0])
                )
                .build();

        return circuitBreakerRegistry.circuitBreaker("redisCircuitBreaker", config);
    }
}
