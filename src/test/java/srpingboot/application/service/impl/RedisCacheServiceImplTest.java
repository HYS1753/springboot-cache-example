package srpingboot.application.service.impl;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import srpingboot.application.dto.TestInput;
import srpingboot.application.dto.TestResult;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisCacheServiceImplTest {

    @Autowired
    RedisCacheServiceImpl service;
    @Autowired
    CircuitBreakerRegistry circuitBreakerRegistry;

    @Test
    void redisCircuitBreakerTest() throws InterruptedException {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("redisCircuitBreaker");
        System.out.println(String.valueOf(circuitBreaker.getCircuitBreakerConfig()));
        logCircuitBreakerState(circuitBreaker);

        TestInput testInput = TestInput.builder().param1("aaa").param2("bbb").build();
        System.out.println("정상 상태 테스트 start");
        for (int i = 0; i < 30; i++) {
            TestResult result = service.enableCache(testInput);
            System.out.println(result.getMessage());
            logCircuitBreakerState(circuitBreaker);
        }

        System.out.println("정상 상태 테스트 end ");
        logCircuitBreakerState(circuitBreaker);
        Thread.sleep(5000);

        System.out.println("비 정상 상태 테스트 start");
        logCircuitBreakerState(circuitBreaker);
        for (int i = 0; i < 30; i++) {
            TestResult result = service.enableCache(testInput);
            System.out.println(result.getMessage());
            logCircuitBreakerState(circuitBreaker);
        }
        System.out.println("비 정상 상태 테스트 end");

        Assert.assertTrue(circuitBreaker.getState() == CircuitBreaker.State.CLOSED);

    }

    private void logCircuitBreakerState(CircuitBreaker circuitBreaker) {
        System.out.println(circuitBreaker.getState());
        System.out.println("FailureRate : " + circuitBreaker.getMetrics().getFailureRate());
        System.out.println("NumberOfSuccessfulCalls : " + circuitBreaker.getMetrics().getNumberOfSuccessfulCalls());
    }
}