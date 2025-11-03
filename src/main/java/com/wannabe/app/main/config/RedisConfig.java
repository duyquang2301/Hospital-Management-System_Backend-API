package com.wannabe.app.main.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.time.Duration;

@Configuration
@EnableCaching
@EnableRedisHttpSession
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.timeout-millis}")
    private long timeout;
    @Value("#{new Boolean('${spring.redis.cluster-mode}')}")
    private Boolean clusterMode;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        if (!clusterMode) {
            /* standalone mode config */
            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
            redisStandaloneConfiguration.setHostName(host);
            redisStandaloneConfiguration.setPort(port);
            redisStandaloneConfiguration.setPassword(password);
            return new LettuceConnectionFactory(redisStandaloneConfiguration);
        } else {
            /* cluster mode config */
            RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
            clusterConfiguration.clusterNode(host, port);
            LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
                .clientOptions(ClientOptions.builder()
                    .socketOptions(SocketOptions.builder()
                        .connectTimeout(Duration.ofMillis(timeout)).build())
                    .build())
                .commandTimeout(Duration.ofSeconds(timeout)).build();
            return new LettuceConnectionFactory(clusterConfiguration, clientConfiguration);
        }
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        return redisTemplate;
    }
}
