package redisdemo;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import com.demo.redisdemo.RedisDemoApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisDemoApplication.class)
public class Demo1 {

	@Autowired
	RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	
	@Test
	public void method1() {
		redisTemplate.opsForValue().set("a",1);  
        redisTemplate.opsForValue().set("b",2);  
        redisTemplate.executePipelined(new RedisCallback<Object>() {  
            @Override  
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {  
                redisConnection.openPipeline();  
                for (int i = 0;i < 10;i++){  
                    redisConnection.incr("a".getBytes());  
                }  
                System.out.println("a:"+redisTemplate.opsForValue().get("a"));  
                redisTemplate.opsForValue().set("c",3);  
                for(int j = 0;j < 20;j++){  
                    redisConnection.incr("b".getBytes());  
                }  
                System.out.println("b:"+redisTemplate.opsForValue().get("b"));  
                System.out.println("c:"+redisTemplate.opsForValue().get("c"));  
                redisConnection.closePipeline();  
                return null;  
            }  
        });  
        System.out.println("b:"+redisTemplate.opsForValue().get("b"));  
        System.out.println("a:"+redisTemplate.opsForValue().get("a"));  
	}
	
	@Test
	public void method2() {
		redisTemplate.opsForValue().set("a", "邓晨");
		stringRedisTemplate.opsForValue().set("b", "张三");
		System.out.println(redisTemplate.opsForValue().get("a"));
		System.out.println(stringRedisTemplate.opsForValue().get("a"));
		//System.out.println(redisTemplate.opsForValue().get("b"));
		System.out.println(stringRedisTemplate.opsForValue().get("b"));
	}
	
	@Test
	public void method3() {
		ValueOperations<String,Object> valueOperations = redisTemplate.opsForValue();  
        redisTemplate.setEnableTransactionSupport(true);  
        //在未提交之前是获取不到值得，同时再次循环报错  
        while (true){  
            redisTemplate.watch("multiTest");  
            redisTemplate.multi();  
            valueOperations.set("multiTest",1);  
            valueOperations.increment("multiTest",2);  
            Object o = valueOperations.get("multiTest");  
            List list = redisTemplate.exec();  
            System.out.println(list);  
            System.out.println(o);
        }
	}
	
	@Test
	public void method4() throws InterruptedException {
		String key = "multThreadTest";
		redisTemplate.delete(key);
		redisTemplate.setEnableTransactionSupport(true);
		Runnable runable = new Runnable() {
			@Override
			public void run() {
		
				redisTemplate.execute(new SessionCallback<Object>() {
		
					@Override
					@SuppressWarnings({ "unchecked", "rawtypes" })
					public Object execute(RedisOperations operations) throws DataAccessException {
						List<Object> result = null;
						do {
							int count = 0;
							operations.watch(key);  // watch某个key,当该key被其它客户端改变时,则会中断当前的操作
							String value = (String) operations.opsForValue().get(key);
							if (!StringUtils.isEmpty(value)) {
								count = Integer.parseInt(value);
							}
							count = count + 1;
							operations.multi(); //开始事务
							operations.opsForValue().set(key, String.valueOf(count));
							try {
								result = operations.exec(); //提交事务
							} catch (Exception e) { //如果key被改变,提交事务时这里会报异常
								System.out.println(e.getMessage());
							}
							System.out.println(result);
						} while (result.size() == 0 || !(boolean)result.get(0)); //如果失败则重试
						return null;
					}
		
				});
		
			}
		};
		List<Thread> threads = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			Thread thread = new Thread(runable, "thread-" + (i + 1));
			thread.start();
			threads.add(thread);
		}
		
		for (Thread thread : threads) {
			thread.join();
		}
		
		String value = (String) redisTemplate.opsForValue().get(key);
		System.out.println(value);
	}
	
	@Test
	public void method5() {
		System.out.println(redisTemplate.opsForValue().get("multThreadTest"));
	}
}
