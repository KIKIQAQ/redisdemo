package redisdemo;

import com.demo.redisdemo.RedisDemoApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisDemoApplication.class)
public class Demo7 {

    @Autowired
    @Qualifier("changeRedisTemplate")
    RedisTemplate changeRedisTemplate;

    @Test
    public void method1(){

    }
}
