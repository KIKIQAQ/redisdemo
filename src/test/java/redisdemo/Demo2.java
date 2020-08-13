package redisdemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
public class Demo2 {

	@Autowired
	RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	
	@Test
	public void method1() {
		//使用redisTemplate则不能在字符串后添加
		stringRedisTemplate.opsForValue().set("stringValue","bbb"); 
		stringRedisTemplate.opsForValue().append("stringValue","aaa");  
		String stringValueAppend = (String) stringRedisTemplate.opsForValue().get("stringValue");  
		System.out.println("通过append(K key, String value)方法修改后的字符串:"+stringValueAppend);  
	}
	
	@Test
	public void method2() {
		//左闭右闭
		String cutString = redisTemplate.opsForValue().get("stringValue",0,3);  
		System.out.println("通过get(K key, long start, long end)方法获取截取的字符串:"+cutString); 
	}
	
	@Test
	public void method3() {
		//用stringRedisTemplate
		stringRedisTemplate.opsForValue().set("stringValue1", "6");
		stringRedisTemplate.opsForValue().setBit("stringValue1", 1, true);  
		String newStringValue = (String) stringRedisTemplate.opsForValue().get("stringValue1");  
		System.out.println("通过setBit(K key,long offset,boolean value)方法修改过后的值:"+newStringValue); 
	}
	
	@Test
	public void method4() {
		boolean bitBoolean = redisTemplate.opsForValue().getBit("stringValue",1);  
		System.out.println("通过getBit(K key,long offset)方法判断指定bit位的值是:" + bitBoolean); 
		Long stringValueLength = redisTemplate.opsForValue().size("stringValue");  
		System.out.println("通过size(K key)方法获取字符串的长度:"+stringValueLength); 
	}
	
	@Test
	public void method5() {
		double stringValueDouble = redisTemplate.opsForValue().increment("doubleValue",5);   
		System.out.println("通过increment(K key, double delta)方法以增量方式存储double值:" + stringValueDouble);  
	}
	
	@Test
	public void method6() {
		//如果键不存在则新增,存在则不改变已经有的值。
		boolean absentBoolean = redisTemplate.opsForValue().setIfAbsent("absentValue","fff");  
		System.out.println("通过setIfAbsent(K key, V value)方法判断变量值absentValue是否存在:" + absentBoolean);  
		if(absentBoolean){  
		    String absentValue = redisTemplate.opsForValue().get("absentValue")+"";  
		    System.out.print(",不存在，则新增后的值是:"+absentValue);  
		    boolean existBoolean = redisTemplate.opsForValue().setIfAbsent("absentValue","eee");  
		    System.out.print(",再次调用setIfAbsent(K key, V value)判断absentValue是否存在并重新赋值:" + existBoolean);  
		    if(!existBoolean){  
		        absentValue = redisTemplate.opsForValue().get("absentValue")+"";  
		        System.out.print("如果存在,则重新赋值后的absentValue变量的值是:" + absentValue);  
		    }  
		}  
	}
	
	@Test
	public void method7() throws InterruptedException {
		//设置过期时间
		redisTemplate.opsForValue().set("timeOutValue","timeOut",5,TimeUnit.SECONDS);  
		String timeOutValue = redisTemplate.opsForValue().get("timeOutValue")+"";  
		System.out.println("通过set(K key, V value, long timeout, TimeUnit unit)方法设置过期时间，过期之前获取的数据:"+timeOutValue);  
		Thread.sleep(5*1000);  
		timeOutValue = redisTemplate.opsForValue().get("timeOutValue")+"";  
		System.out.print(",等待10s过后，获取的值:"+timeOutValue);  
	}
	
	@Test
	public void method8() {
		stringRedisTemplate.opsForValue().set("absentValue","aaa");
		stringRedisTemplate.opsForValue().set("absentValue","dd",1);  
		String overrideString = stringRedisTemplate.opsForValue().get("absentValue");  
		System.out.println("通过set(K key, V value, long offset)方法覆盖部分的值:"+overrideString); 
	}
	
	@Test
	public void method9() {
		Map<String, String> valueMap = new HashMap<String, String>();  
		valueMap.put("valueMap1","map1");  
		valueMap.put("valueMap2","map2");  
		valueMap.put("valueMap3","map3");  
		redisTemplate.opsForValue().multiSet(valueMap); 
		List<String> valueList = new ArrayList<String>();
		valueList.add("valueMap1");
		valueList.add("valueMap2");
		valueList.add("valueMap3");
		
		List<Object> multiGet = redisTemplate.opsForValue().multiGet(valueList);
		Iterator<Object> iterator = multiGet.iterator();
		while(iterator.hasNext()) {
			System.out.println(iterator.next());
		}
	}
	
	@Test
	public void method10() {
		//相当于是复合操作
		Map<String, String> valueMap = new HashMap<String, String>();  
		valueMap.put("valueMap1","map1");  
		valueMap.put("valueMap2","map2");  
		valueMap.put("valueMap3","map3");  
		redisTemplate.opsForValue().multiSetIfAbsent(valueMap); 
	}
}
