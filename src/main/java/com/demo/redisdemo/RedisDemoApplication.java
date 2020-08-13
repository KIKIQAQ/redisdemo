package com.demo.redisdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import com.demo.redisdemo.service.RedisService;

@SpringBootApplication
@MapperScan("phjkdata.mapper")
@EnableCaching
public class RedisDemoApplication implements CommandLineRunner{

	@Autowired
	RedisService redisService;
	
	public static void main(String[] args) {
		SpringApplication.run(RedisDemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		redisService.service1();
	}

}
