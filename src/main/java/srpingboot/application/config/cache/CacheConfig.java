package srpingboot.application.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import srpingboot.application.common.enumerations.CaffeineCacheType;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.username}")
    private String username;

    @Value("${spring.data.redis.password}")
    private String password;

    @Value("${spring.data.redis.pool.max-active}")
    private int maxActive;

    @Value("${spring.data.redis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.data.redis.pool.min-idle}")
    private int minIdle;

    @Value("${spring.data.redis.pool.max-wait}")
    private Long maxWait;

    @Value("${spring.data.redis.pool.time-between-eviction-runs}")
    private Long timeBetweenEvictionRuns;

    /**
     * Caffeine 내부 캐쉬 설정
     * @return
     */
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

    /**
     * Redis 외부 캐쉬 설정
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // 기본 Redis 접근 설정
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(host);
        redisConfiguration.setPort(port);
        redisConfiguration.setUsername(username);
        redisConfiguration.setPassword(password);

        // Redis Connection pool 관련 설정
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWaitMillis(maxWait);
        poolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRuns);

        LettuceClientConfiguration clientConfiguration =
                LettucePoolingClientConfiguration.builder()
                        .poolConfig(poolConfig)
                        .clientOptions(
                                ClientOptions.builder()
                                             .socketOptions(
                                                     SocketOptions.builder()
                                                                  .connectTimeout(Duration.ofMillis(500))
                                                                  .build()
                                             )
                                             .build()
                        )
                        .commandTimeout(Duration.ofMillis(500)).build();

        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfiguration, clientConfiguration);
        return lettuceConnectionFactory;
    }


}
