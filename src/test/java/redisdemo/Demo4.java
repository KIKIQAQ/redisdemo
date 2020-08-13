package redisdemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.demo.redisdemo.RedisDemoApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisDemoApplication.class)
public class Demo4 {

	@Autowired
	RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	
	@Test
	public void method1() {
		redisTemplate.opsForHash().put("hashValue","map1","map1-1");  
		redisTemplate.opsForHash().put("hashValue","map2","map2-2");  
		List<Object> hashList = redisTemplate.opsForHash().values("hashValue");  
		System.out.println("通过values(H key)方法获取变量中的hashMap值:" + hashList); 
		Map<Object,Object> map = redisTemplate.opsForHash().entries("hashValue");  
		System.out.println("通过entries(H key)方法获取变量中的键值对:" + map); 
		Object mapValue = redisTemplate.opsForHash().get("hashValue","map1");  
		System.out.println("通过get(H key, Object hashKey)方法获取map键的值:" + mapValue); 
		boolean hashKeyBoolean = redisTemplate.opsForHash().hasKey("hashValue","map3");  
		System.out.println("通过hasKey(H key, Object hashKey)方法判断变量中是否存在map键:" + hashKeyBoolean); 
		Set<Object> keySet = redisTemplate.opsForHash().keys("hashValue");  
		System.out.println("通过keys(H key)方法获取变量中的键:" + keySet);
		long hashLength = redisTemplate.opsForHash().size("hashValue");  
		System.out.println("通过size(H key)方法获取变量的长度:" + hashLength); 
	}
	
	@Test
	public void method2() {
		Map<String, Integer> newMap = new HashMap<String, Integer>();  
		newMap.put("map1", 1);  
		newMap.put("map2", 2);  
		redisTemplate.opsForHash().putAll("hashInc",newMap);
		double hashIncDouble = redisTemplate.opsForHash().increment("hashInc","map1",3);  
		System.out.println("通过increment(H key, HK hashKey, double delta)方法使变量中的键以值的大小进行自增长:" + hashIncDouble);
		Object object = redisTemplate.opsForHash().get("hashInc", "map1");
		System.out.println("自增后的值为：" + object);
	}
	
	@Test
	public void method3() {
		Map map = redisTemplate.opsForHash().entries("hashValue");
		System.out.println("hashValue的值：" + map);
		map = new HashMap<String, String>();  
		map.put("map3","map3-3");  
		map.put("map5","map5-6");  
		redisTemplate.opsForHash().putAll("hashValue",map);  
		map = redisTemplate.opsForHash().entries("hashValue");  
		System.out.println("通过putAll(H key, Map<? extends HK,? extends HV> m)方法以map集合的形式添加键值对:" + map); 
		List<Object> list = new ArrayList<Object>();  
		list.add("map1");  
		list.add("map2");  
		List<Object> mapValueList = redisTemplate.opsForHash().multiGet("hashInc",list);  
		System.out.println("通过multiGet(H key, Collection<HK> hashKeys)方法以集合的方式获取变量中的值:"+mapValueList);  
	}
	
	@Test
	public void method4() {
		//如果变量值存在，在变量中可以添加不存在的的键值对，如果变量不存在，则新增一个变量，同时将键值对添加到该变量。
		redisTemplate.opsForHash().putIfAbsent("hashValue","map6","map6-6");  
		Map map = redisTemplate.opsForHash().entries("hashValue");  
		System.out.println("通过putIfAbsent(H key, HK hashKey, HV value)方法添加不存在于变量中的键值对:" + map);  
	}
	
	@Test
	public void method5() {
		//Cursor<Map.Entry<Object,Object>> cursor = redisTemplate.opsForHash().scan("hashValue",ScanOptions.scanOptions().match("^map").build());  
		Cursor<Map.Entry<Object,Object>> cursor = redisTemplate.opsForHash().scan("hashValue",ScanOptions.NONE);  
		while (cursor.hasNext()){  
		    Map.Entry<Object,Object> entry = cursor.next();  
		    System.out.println("通过scan(H key, ScanOptions options)方法获取匹配键值对:" + entry.getKey() + "---->" + entry.getValue());  
		}  
	}
	
	@Test
	public void method6() {
		redisTemplate.opsForHash().delete("hashValue","map1","map2");  
		Map map = redisTemplate.opsForHash().entries("hashValue");  
		System.out.println("通过delete(H key, Object... hashKeys)方法删除变量中的键值对后剩余的:" + map);  
	}
}
