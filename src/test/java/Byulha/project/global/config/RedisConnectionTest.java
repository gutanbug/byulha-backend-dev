package Byulha.project.global.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

public class RedisConnectionTest {

    private StringRedisTemplate redisTemplate;

    @BeforeEach
    public void init() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration("localhost", 6379);
        LettuceConnectionFactory redisConnectionFactory = new LettuceConnectionFactory(configuration);
        redisConnectionFactory.afterPropertiesSet();
        this.redisTemplate = new StringRedisTemplate(redisConnectionFactory);
    }

    @Test
    @DisplayName("Redis Connection 확인")
    @Disabled
    public void connectionTest() {
        redisTemplate.opsForSet().add("signUpAuth", "user_1");
    }
}
