package com.demo.redisdemo.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;

@Configuration
public class RedisConfig {
	@Lazy
	@Autowired
	private RedisConnectionFactory redisConnectionFactory;
	@Value("${spring.redis.sentinel.host}")
	String sentinelHost;
	@Value("${spring.redis.sentinel2.host}")
	String sentinelHost2;
	@Value("${spring.redis.sentinel3.host}")
	String sentinelHost3;
	@Value("${spring.redis.sentinel.port}")
	String sentinelPort;
	@Value("${spring.redis.sentinel2.port}")
	String sentinelPort2;
	@Value("${spring.redis.sentinel3.port}")
	String sentinelPort3;
	@Value("${spring.redis.password}")
	String password;
	@Bean
	public RedisSentinelConfiguration redisSentinelConfiguration(){
		RedisSentinelConfiguration config = new RedisSentinelConfiguration();
		config.addSentinel(new RedisNode(sentinelHost,Integer.parseInt(sentinelPort)));
		config.addSentinel(new RedisNode(sentinelHost2,Integer.parseInt(sentinelPort2)));
		config.addSentinel(new RedisNode(sentinelHost3,Integer.parseInt(sentinelPort3)));
		config.setMaster("mymaster");
		config.setPassword(password);
		
		return config;
	}	

}
