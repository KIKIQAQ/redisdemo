package redisdemo;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.demo.redisdemo.RedisDemoApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisDemoApplication.class)
public class Demo3 {

	@Autowired
	RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	
	@Test
	public void method1() {
		redisTemplate.opsForList().rightPush("list","a");  
		redisTemplate.opsForList().rightPush("list","b");  
		redisTemplate.opsForList().rightPush("list","c"); 
		System.out.println(redisTemplate.opsForList().index("list", 1));
	}
	
	@Test
	public void method2() {
		List<Object> list =  redisTemplate.opsForList().range("list",0,-1);  
		for(Object o : list) {
			System.out.println(o);
		}
		System.out.println("通过range(K key, long start, long end)方法获取指定范围的集合值:"+list);  
		
		redisTemplate.opsForList().leftPush("list","a","n");  
		list =  redisTemplate.opsForList().range("list",0,-1);  
		System.out.println("通过leftPush(K key, V pivot, V value)方法把值放到指定参数值前面:" + list);
	}
	
	@Test
	public void method3() {
		//如果存在集合则添加元素
		redisTemplate.opsForList().rightPush("presentList","a");  
		redisTemplate.opsForList().rightPush("presentList","b");  
		redisTemplate.opsForList().rightPush("presentList","c");  
		redisTemplate.opsForList().leftPushIfPresent("presentList","o");  
		List<Object> list =  redisTemplate.opsForList().range("presentList",0,-1);  
		System.out.println("通过leftPushIfPresent(K key, V value)方法向已存在的集合添加元素:" + list);  
	}
	
	@Test
	public void method4() {
		//阻塞的
		Object leftPop = redisTemplate.opsForList().leftPop("presentList",10, TimeUnit.SECONDS);  
		System.out.print("通过leftPop(K key, long timeout, TimeUnit unit)方法移除的元素是:" + leftPop);  
		List<Object> list =  redisTemplate.opsForList().range("presentList",0,-1);  
		System.out.println(",剩余的元素是:" + list);
	}
	
	@Test
	public void method5() {
		List<Object> list =  redisTemplate.opsForList().range("list",0,-1);
		System.out.println("list的元素是:" + list); 
		List<Object> list1 =  redisTemplate.opsForList().range("presentList",0,-1);  
		System.out.println("presentList的元素是:" + list1); 
		Object rightPopAndLeftPush = redisTemplate.opsForList().rightPopAndLeftPush("list", "presentList");  
		System.out.print("通过rightPopAndLeftPush(K sourceKey, K destinationKey)方法移除的元素是:" + rightPopAndLeftPush);  
		list =  redisTemplate.opsForList().range("list",0,-1);  
		System.out.println(",剩余的元素是:" + list); 
		list1 =  redisTemplate.opsForList().range("presentList",0,-1);  
		System.out.println("presentList的元素是:" + list1); 
	}
	
	@Test
	public void method6() {
		List<Object> list =  redisTemplate.opsForList().range("list",0,-1);
		System.out.println("list的元素是:" + list); 
		long removeCount = redisTemplate.opsForList().remove("list",-1,"a");  
		list =  redisTemplate.opsForList().range("list",0,-1);  
		System.out.println("通过remove(K key, long count, Object value)方法移除元素数量:" + removeCount);  
		System.out.println(",剩余的元素:" + list); 
	}
	
	@Test
	public void method7() {
		List<Object> list =  redisTemplate.opsForList().range("list",0,-1);
		System.out.println("list的元素是:" + list); 
		redisTemplate.opsForList().trim("list",0,2);  
		list =  redisTemplate.opsForList().range("list",0,-1);  
		System.out.println("通过trim(K key, long start, long end)方法截取后剩余元素:" + list);  
	}
}
