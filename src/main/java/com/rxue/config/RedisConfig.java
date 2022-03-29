package com.rxue.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author RunXue
 * @create 2022-03-26 15:39
 * @Description
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        //设置key的序列化方式
        template.setKeySerializer(RedisSerializer.string());
        //设置hash中key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        //设置value的序列化方法  这边的json方法就是用的jackson2Json  因为Springboot自带Jackson
        template.setValueSerializer(RedisSerializer.json());
        //设置哈希中value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet();
        return template;
    }
}
