package com.spring.nuqta.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.net.URI;

@Configuration
@Profile("prod")
public class RedisConfig {

    @Value("${REDIS_URL}")
    private String redisUrl;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        URI redisUri = URI.create(redisUrl);
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();

        config.setHostName(redisUri.getHost());
        config.setPort(redisUri.getPort());
        String userInfo = redisUri.getUserInfo();
        if (userInfo != null && userInfo.contains(":")) {
            config.setPassword(RedisPassword.of(userInfo.split(":", 2)[1]));
        }

        return new LettuceConnectionFactory(config);
    }
}

