package com.demo.redisdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.redisdemo.service.RedisService;

@RestController
public class TestController {
	
	@Autowired
	RedisService redisService;
	
	@GetMapping(value = "controller1")
	public String controller1() {
		return redisService.service2();
	}
}
