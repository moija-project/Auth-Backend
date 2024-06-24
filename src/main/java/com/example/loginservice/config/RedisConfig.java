package com.example.loginservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;
import org.springframework.data.redis.core.convert.MappingConfiguration;
import org.springframework.data.redis.core.index.IndexConfiguration;
import org.springframework.data.redis.core.index.IndexDefinition;
import org.springframework.data.redis.core.index.SimpleIndexDefinition;
import org.springframework.data.redis.core.mapping.RedisMappingContext;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Collections;

@Configuration
// enableKeyspaceEvents : indexed가 삭제되지 않는 이슈 해결 방안 -> ttl끝나면 index도 알람보내서 삭제
@EnableRedisRepositories(indexConfiguration = RedisConfig.MyIndexConfiguration.class,enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
public class RedisConfig {

    @Autowired
    Environment env;

    //@Value("${spring.redis.port}")
    private int port = 6379;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        System.out.println(env.getProperty("spring.data.redis.host"));
        return new LettuceConnectionFactory(env.getProperty("spring.data.redis.host"), port);
    }


    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // 일반적인 key:value의 경우 시리얼라이저
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        // Hash를 사용할 경우 시리얼라이저
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        // 모든 경우
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());

        return redisTemplate;
    }

    @Bean
    public RedisMappingContext keyValueMappingContext() {
        return new RedisMappingContext( new MappingConfiguration(new MyIndexConfiguration(), new KeyspaceConfiguration()));
    }
    public static class MyIndexConfiguration extends IndexConfiguration {

        @Override
        protected Iterable<IndexDefinition> initialConfiguration() {
            return Collections.singleton(new SimpleIndexDefinition("refresh", "token"));
        }
    }
}