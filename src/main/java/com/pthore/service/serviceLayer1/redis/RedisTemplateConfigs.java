package com.pthore.service.serviceLayer1.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pthore.service.serviceLayer1.redis.models.IConsumerObject;

@Configuration
public class RedisTemplateConfigs {


	@Bean
	public LettuceConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory( new RedisStandaloneConfiguration("localhost", 6379));
	}
	
	@Primary
	@Bean(name="redisTemplateForIConsumerObject")
	@DependsOn("redisConnectionFactory")
	public RedisTemplate<String, IConsumerObject<?>> redisTemplateForWrite() {
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		RedisTemplate<String, IConsumerObject<?>> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new IConsumerObjectValueSerde());
		redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		return redisTemplate;
	}
	
	@Bean(name="redisTemplateForString")
	@Primary
	public RedisTemplate<String, String> redisTemplateForRead() {
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new  StringRedisSerializer());
		redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		return redisTemplate;
	}
		
}
