package redisdemo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit4.SpringRunner;

import com.demo.redisdemo.RedisDemoApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisDemoApplication.class)
public class Demo6 {

	@Autowired
	RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	
	@Test
	public void method1() {
		redisTemplate.opsForZSet().add("zSetValue","A",1);  
		redisTemplate.opsForZSet().add("zSetValue","B",2);  
		redisTemplate.opsForZSet().add("zSetValue","C",3);  
		redisTemplate.opsForZSet().add("zSetValue","D",4);  
		Set zSetValue = redisTemplate.opsForZSet().range("zSetValue",0,-1);  
		System.out.println("通过range(K key, long start, long end)方法获取指定区间的元素:" + zSetValue); 
		RedisZSetCommands.Range range = new RedisZSetCommands.Range();  
		range.gte("A");  
		range.lt("D");  
		zSetValue = redisTemplate.opsForZSet().rangeByLex("zSetValue", range);  
		System.out.println("通过rangeByLex(K key, RedisZSetCommands.Range range)方法获取满足非score的排序取值元素:" + zSetValue);  
	}
	
	@Test
	public void method2() {
		Set zSetValue = stringRedisTemplate.opsForZSet().range("myzset",0,-1);  
		System.out.println("通过range(K key, long start, long end)方法获取指定区间的元素:" + zSetValue); 
		RedisZSetCommands.Range range = new RedisZSetCommands.Range();
		RedisZSetCommands.Limit limit = new RedisZSetCommands.Limit(); 
		//range.gte("A");
		//range.lte("C");
		limit.count(100);  
		//起始下标为0  
		limit.offset(0);  
		//用于获取满足非score的设置下标开始的长度排序取值
		zSetValue = stringRedisTemplate.opsForZSet().rangeByLex("zSetValue", range, limit);  
		System.out.println("通过rangeByLex(K key, RedisZSetCommands.Range range, RedisZSetCommands.Limit limit)方法获取满足非score的排序取值元素:" + zSetValue);
	}
	
	@Test
	public void method3() {
		stringRedisTemplate.opsForZSet().add("myzset","A",2);  
		stringRedisTemplate.opsForZSet().add("myzset","B",3);  
		stringRedisTemplate.opsForZSet().add("myzset","C",1);  
		stringRedisTemplate.opsForZSet().add("myzset","D",4);
		//运用该函数用stringRedisTemplate, 排序仍以score优先, range圈定范围和在score相等时生效
		RedisZSetCommands.Range range = new RedisZSetCommands.Range();
		range.gte("A");
		range.lte("C");
		System.out.println(stringRedisTemplate.opsForZSet().rangeByLex("myzset",range));
	}
	
	@Test
	public void method4() {
		ZSetOperations.TypedTuple<Object> typedTuple1 = new DefaultTypedTuple<Object>("E",6.0);  
		ZSetOperations.TypedTuple<Object> typedTuple2 = new DefaultTypedTuple<Object>("F",7.0);  
		ZSetOperations.TypedTuple<Object> typedTuple3 = new DefaultTypedTuple<Object>("G",5.0);  
		Set<ZSetOperations.TypedTuple<Object>> typedTupleSet = new HashSet<ZSetOperations.TypedTuple<Object>>();  
		typedTupleSet.add(typedTuple1);  
		typedTupleSet.add(typedTuple2);  
		typedTupleSet.add(typedTuple3);  
		//通过TypedTuple方式新增数据。
		redisTemplate.opsForZSet().add("typedTupleSet",typedTupleSet);  
		Set zSetValue = redisTemplate.opsForZSet().range("typedTupleSet",0,-1);  
		System.out.println("通过add(K key, Set<ZSetOperations.TypedTuple<V>> tuples)方法添加元素:" + zSetValue);  
	}
	
	@Test
	public void method5() {
		Set zSetValue = stringRedisTemplate.opsForZSet().rangeByScore("myzset",1,2);  
		System.out.println("通过rangeByScore(K key, double min, double max)方法根据设置的score获取区间值:" + zSetValue);  
		zSetValue = redisTemplate.opsForZSet().rangeByScore("zSetValue",1,3,1,2);  
		System.out.println("通过rangeByScore(K key, double min, double max, long offset, long count)方法根据设置的score获取区间值:" + zSetValue); 
	}

	@Test
	public void method6() {
		//redis的zset下标不会越界
		Set<ZSetOperations.TypedTuple<Object>> typedTupleSet = redisTemplate.opsForZSet().rangeWithScores("typedTupleSet",0,100);  
		Iterator<ZSetOperations.TypedTuple<Object>> iterator = typedTupleSet.iterator();  
		while (iterator.hasNext()){  
		    ZSetOperations.TypedTuple<Object> typedTuple = iterator.next();  
		    Object value = typedTuple.getValue();  
		    double score = typedTuple.getScore();  
		    System.out.println("通过rangeWithScores(K key, long start, long end)方法获取RedisZSetCommands.Tuples的区间值:" + value + "---->" + score );  
		}  
	}
	
	@Test
	public void method7() {
		Set<ZSetOperations.TypedTuple<Object>> typedTupleSet = redisTemplate.opsForZSet().rangeByScoreWithScores("typedTupleSet",5,6);  
		Iterator<ZSetOperations.TypedTuple<Object>> iterator = typedTupleSet.iterator();  
		while (iterator.hasNext()){  
		    ZSetOperations.TypedTuple<Object> typedTuple = iterator.next();  
		    Object value = typedTuple.getValue();  
		    double score = typedTuple.getScore();  
		    System.out.println("通过rangeByScoreWithScores(K key, double min, double max)方法获取RedisZSetCommands.Tuples的区间值通过分值:" + value + "---->" + score );  
		}  
	}
	
	@Test
	public void method8() {
		//和rangeByScore区别在于返回值不同，该返回的有分值
		Set<ZSetOperations.TypedTuple<Object>> typedTupleSet = redisTemplate.opsForZSet().rangeByScoreWithScores("typedTupleSet",5,8,1,1);  
		Iterator<ZSetOperations.TypedTuple<Object>> iterator = typedTupleSet.iterator();  
		while (iterator.hasNext()){  
		    ZSetOperations.TypedTuple<Object> typedTuple = iterator.next();  
		    Object value = typedTuple.getValue();  
		    double score = typedTuple.getScore();  
		    System.out.println("通过rangeByScoreWithScores(K key, double min, double max, long offset, long count)方法获取RedisZSetCommands.Tuples的区间值从给定下标和给定长度获取最终值通过分值:" + value + "---->" + score );  
		}  
	}
	
	@Test
	public void method9() {
		Set zSetValue = redisTemplate.opsForZSet().range("zSetValue",0,-1);  
		System.out.println("通过range(K key, long start, long end)方法获取指定区间的元素:" + zSetValue);
		long count = redisTemplate.opsForZSet().count("zSetValue",1,2);  
		System.out.println("通过count(K key, double min, double max)方法获取区间值的个数:" + count);  
		long index = redisTemplate.opsForZSet().rank("zSetValue","B");  
		System.out.println("通过rank(K key, Object o)方法获取变量中元素的索引:" + index);  
	}
	
	@Test
	public void method10() {
		//Cursor<Object> cursor = redisTemplate.opsForSet().scan("setValue", ScanOptions.NONE);  
		Cursor<ZSetOperations.TypedTuple<Object>> cursor = redisTemplate.opsForZSet().scan("zSetValue", ScanOptions.NONE);  
		while (cursor.hasNext()){  
		    ZSetOperations.TypedTuple<Object> typedTuple = cursor.next();  
		    System.out.println("通过scan(K key, ScanOptions options)方法获取匹配元素:" + typedTuple.getValue() + "--->" + typedTuple.getScore());  
		}  
		double score = redisTemplate.opsForZSet().score("zSetValue","B");  
		System.out.println("通过score(K key, Object o)方法获取元素的分值:" + score);
		//相当于获取集合size
		long zCard = redisTemplate.opsForZSet().zCard("zSetValue");  
		System.out.println("通过zCard(K key)方法获取变量的长度:" + zCard); 
		//修改变量中的元素的分值
		double incrementScore = redisTemplate.opsForZSet().incrementScore("zSetValue","C",5);  
		System.out.print("通过incrementScore(K key, V value, double delta)方法修改变量中的元素的分值:" + incrementScore);  
		score = redisTemplate.opsForZSet().score("zSetValue","C");  
		System.out.print(",修改后获取元素的分值:" + score);  
		Set zSetValue = redisTemplate.opsForZSet().range("zSetValue",0,-1);  
		System.out.println("，修改后排序的元素:" + zSetValue); 
	}
	
	@Test
	public void method11() {
		//reverse类函数在原先函数基础上倒序
		Set zSetValue = redisTemplate.opsForZSet().reverseRange("zSetValue",0,-1);  
		System.out.println("通过reverseRange(K key, long start, long end)方法倒序排列元素:" + zSetValue);
		zSetValue = redisTemplate.opsForZSet().reverseRangeByScore("zSetValue",1,5);  
		System.out.println("通过reverseRangeByScore(K key, double min, double max)方法倒序排列指定分值区间元素:" + zSetValue);
		zSetValue = redisTemplate.opsForZSet().reverseRangeByScore("zSetValue",1,5,1,2);  
		System.out.println("通过reverseRangeByScore(K key, double min, double max, long offset, long count)方法倒序排列从给定下标和给定长度分值区间元素:" + zSetValue);  
		Set<ZSetOperations.TypedTuple<Object>> typedTupleSet = redisTemplate.opsForZSet().reverseRangeByScoreWithScores("zSetValue",1,5);  
		Iterator<ZSetOperations.TypedTuple<Object>> iterator = typedTupleSet.iterator();  
		while (iterator.hasNext()){  
		    ZSetOperations.TypedTuple<Object> typedTuple = iterator.next();  
		    Object value = typedTuple.getValue();  
		    double score1 = typedTuple.getScore();  
		    System.out.println("通过reverseRangeByScoreWithScores(K key, double min, double max)方法倒序排序获取RedisZSetCommands.Tuples的区间值:" + value + "---->" + score1 );  
		}  
		typedTupleSet = redisTemplate.opsForZSet().reverseRangeByScoreWithScores("zSetValue",1,5,1,2);  
		iterator = typedTupleSet.iterator();  
		while (iterator.hasNext()){  
		    ZSetOperations.TypedTuple<Object> typedTuple = iterator.next();  
		    Object value = typedTuple.getValue();  
		    double score1 = typedTuple.getScore();  
		    System.out.println("通过reverseRangeByScoreWithScores(K key, double min, double max, long offset, long count)方法倒序排序获取RedisZSetCommands.Tuples的从给定下标和给定长度区间值:" + value + "---->" + score1 );  
		}  
		typedTupleSet = redisTemplate.opsForZSet().reverseRangeWithScores("zSetValue",1,5);  
		iterator = typedTupleSet.iterator();  
		while (iterator.hasNext()){  
		    ZSetOperations.TypedTuple<Object> typedTuple = iterator.next();  
		    Object value = typedTuple.getValue();  
		    double score1 = typedTuple.getScore();  
		    System.out.println("通过reverseRangeWithScores(K key, long start, long end)方法索引倒序排列区间值:" + value + "----->" + score1);  
		}  
		long reverseRank = redisTemplate.opsForZSet().reverseRank("zSetValue","B");  
		System.out.println("通过reverseRank(K key, Object o)获取倒序排列的索引值:" + reverseRank);  
	}
	
	@Test
	public void method12() {
		System.out.println("2020-07-11 15:40:21.122".compareTo("2020-07-11 15:42:21"));
	}
	
}
