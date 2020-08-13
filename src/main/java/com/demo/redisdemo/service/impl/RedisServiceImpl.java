package com.demo.redisdemo.service.impl;

import com.demo.redisdemo.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisServiceImpl implements RedisService {

	@Autowired
	RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	
	@Override
	public void service1() {
		redisTemplate.opsForValue().set("dengc6", "中国");
		stringRedisTemplate.opsForValue().set("dengc1", "中国");
	}

	@Override
	public String service2() {
		return (String) redisTemplate.opsForValue().get("dengc6")+stringRedisTemplate.opsForValue().get("dengc1");
	}

}
