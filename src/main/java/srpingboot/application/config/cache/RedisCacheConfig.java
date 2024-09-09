package srpingboot.application.config.cache;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisCacheConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.username}")
    private String username;

    @Value("${spring.data.redis.password}")
    private String password;

    @Value("${spring.data.redis.default-ttl}")
    private long defaultTtl;

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

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public RedisCacheManager RedisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofSeconds(defaultTtl));

        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();

        // cache 유형 별 설정
        redisCacheConfigurationMap.put("redisCache", redisCacheConfiguration.entryTtl(Duration.ofSeconds(defaultTtl)));  // 시간은 좌측과 같이 수정 가능

        return RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .withInitialCacheConfigurations(redisCacheConfigurationMap)
                .build();
    }
}
