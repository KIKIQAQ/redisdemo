package redisdemo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.demo.redisdemo.RedisDemoApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisDemoApplication.class)
public class Demo5 {

	@Autowired
	RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	
	@Test
	public void method1() {
		redisTemplate.opsForSet().add("setValue","A","B","C","B","D","E","F");  
		Set set = redisTemplate.opsForSet().members("setValue");  
		System.out.println("通过members(K key)方法获取变量中的元素值:" + set);
		long setLength = redisTemplate.opsForSet().size("setValue");  
		System.out.println("通过size(K key)方法获取变量中元素值的长度:" + setLength);
		//随机到的元素可能相同
		Object randomMember = redisTemplate.opsForSet().randomMember("setValue");  
		System.out.println("通过randomMember(K key)方法随机获取变量中的元素:" + randomMember);  
		List randomMembers = redisTemplate.opsForSet().randomMembers("setValue",2);  
		System.out.println("通过randomMembers(K key, long count)方法随机获取变量中指定个数的元素:" + randomMembers);  
		boolean isMember = redisTemplate.opsForSet().isMember("setValue","A");  
		System.out.println("通过isMember(K key, Object o)方法检查给定的元素是否在变量中:" + isMember);  
	}
	
	@Test
	public void method2() {
		boolean isMove = redisTemplate.opsForSet().move("setValue","A","destSetValue"); 
		Set set;
		if(isMove){  
			set = redisTemplate.opsForSet().members("setValue");  
		    System.out.print("通过move(K key, V value, K destKey)方法转移变量的元素值到目的变量后的剩余元素:" + set);  
		    set = redisTemplate.opsForSet().members("destSetValue");  
		    System.out.println(",目的变量中的元素值:" + set);  
		}  	
		Object popValue = redisTemplate.opsForSet().pop("setValue");  
		System.out.print("通过pop(K key)方法弹出变量中的元素:" + popValue);  
		set = redisTemplate.opsForSet().members("setValue");  
		System.out.println(",剩余元素:" + set);
		long removeCount = redisTemplate.opsForSet().remove("setValue","E","F","G");  
		System.out.print("通过remove(K key, Object... values)方法移除变量中的元素个数:" + removeCount);  
		set = redisTemplate.opsForSet().members("setValue");  
		System.out.println(",剩余元素:" + set);
	}
	
	@Test
	public void method3() {
		Cursor<Object> cursor = redisTemplate.opsForSet().scan("setValue", ScanOptions.NONE);  
		while (cursor.hasNext()){  
		    Object object = cursor.next();  
		    System.out.println("通过scan(K key, ScanOptions options)方法获取匹配的值:" + object);  
		}  
	}
	
	@Test
	public void method4() {
		List list = new ArrayList();  
		list.add("destSetValue");  
		Set differenceSet = redisTemplate.opsForSet().difference("setValue",list);  
		System.out.println("通过difference(K key, Collection<K> otherKeys)方法获取变量中与给定集合中变量不一样的值:" + differenceSet);
		differenceSet = redisTemplate.opsForSet().difference("setValue","destSetValue");  
		System.out.println("通过difference(K key, Collection<K> otherKeys)方法获取变量中与给定变量不一样的值:" + differenceSet);  
		redisTemplate.opsForSet().differenceAndStore("setValue","destSetValue","storeSetValue");  
		Set set = redisTemplate.opsForSet().members("storeSetValue");  
		System.out.println("通过differenceAndStore(K key, K otherKey, K destKey)方法将求出来的差值元素保存:" + set);
		redisTemplate.opsForSet().differenceAndStore("setValue",list,"storeSetValue");  
		set = redisTemplate.opsForSet().members("storeSetValue");  
		System.out.println("通过differenceAndStore(K key, Collection<K> otherKeys, K destKey)方法将求出来的差值元素保存:" + set);  
	}
	
	@Test
	public void method5() {
		List<String> list = new ArrayList<String>();  
		list.add("setValue");
		list.add("destSetValue");
		Set set = redisTemplate.opsForSet().distinctRandomMembers("setValue",2);  
		System.out.println("通过distinctRandomMembers(K key, long count)方法获取去重的随机元素:" + set); 
		set = redisTemplate.opsForSet().intersect("setValue","destSetValue");  
		System.out.println("通过intersect(K key, K otherKey)方法获取交集元素:" + set); 
		//该集合参数需存储redis中的键值
		set = redisTemplate.opsForSet().intersect("destSetValue", Arrays.asList("A"));  
		System.out.println("通过intersect(K key, Collection<K> otherKeys)方法获取交集元素:" + set);
		redisTemplate.opsForSet().intersectAndStore("setValue","destSetValue","intersectValue");  
		set = redisTemplate.opsForSet().members("intersectValue");  
		System.out.println("通过intersectAndStore(K key, K otherKey, K destKey)方法将求出来的交集元素保存:" + set); 
		redisTemplate.opsForSet().intersectAndStore("setValue",list,"intersectListValue");  
		set = redisTemplate.opsForSet().members("intersectListValue");  
		System.out.println("通过intersectAndStore(K key, Collection<K> otherKeys, K destKey)方法将求出来的交集元素保存:" + set);  
	}
	
	@Test
	public void method6() {
		List<String> list = new ArrayList<String>();  
		list.add("setValue");
		list.add("destSetValue");
		Set set = redisTemplate.opsForSet().union("setValue","destSetValue");  
		System.out.println("通过union(K key, K otherKey)方法获取2个变量的合集元素:" + set);  
		set = redisTemplate.opsForSet().union("setValue",list);  
		System.out.println("通过union(K key, Collection<K> otherKeys)方法获取多个变量的合集元素:" + set); 
		redisTemplate.opsForSet().unionAndStore("setValue","destSetValue","unionValue");  
		set = redisTemplate.opsForSet().members("unionValue");  
		System.out.println("通过unionAndStore(K key, K otherKey, K destKey)方法将求出来的交集元素保存:" + set);
		redisTemplate.opsForSet().unionAndStore("setValue",list,"unionListValue");  
		set = redisTemplate.opsForSet().members("unionListValue");  
		System.out.println("通过unionAndStore(K key, Collection<K> otherKeys, K destKey)方法将求出来的交集元素保存:" + set);  
	}
	
	@Test
	public void method7() {
		redisTemplate.opsForSet().add("zhou7", "a","b","c","d","e");
		redisTemplate.opsForSet().add("zhou8", "c","d","e","f","g");
        //key对应的无序集合与otherKey对应的无序集合求交集
        Set<Object> intersect = redisTemplate.opsForSet().intersect("zhou7", "zhou8");
        System.out.println(intersect);//[d, c, e]
        redisTemplate.opsForSet().add("zhou9", "c","h");
        //key对应的无序集合与多个otherKey对应的无序集合求交集
        System.out.println(redisTemplate.opsForSet().intersect("zhou7", Arrays.asList("zhou8","zhou9")));//[c]
        //key无序集合与otherkey无序集合的交集存储到destKey无序集合中
        System.out.println(redisTemplate.opsForSet().intersectAndStore("zhou7", "zhou8","zhou10"));//3
        System.out.println(redisTemplate.opsForSet().members("zhou10"));//[e, c, d]
        //key对应的无序集合与多个otherKey对应的无序集合求交集存储到destKey无序集合中
        System.out.println(redisTemplate.opsForSet().intersectAndStore("zhou7", Arrays.asList("zhou8","zhou9"),"zhou11"));//1
        System.out.println(redisTemplate.opsForSet().members("zhou11"));//[c]
	}
	 
}
