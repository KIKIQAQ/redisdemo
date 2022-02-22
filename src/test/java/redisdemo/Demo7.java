package redisdemo;

import com.demo.redisdemo.RedisDemoApplication;
import com.demo.redisdemo.utils.RedisUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisDemoApplication.class)
public class Demo7 {

    @Autowired
    RedisUtils redisUtils;

    private static CountDownLatch c = new CountDownLatch(16);

    @Test
    public void method1() throws JsonProcessingException {
        redisUtils.set("change", "success", 2);

    }

    @Test
    public void method2 () {
        for (int i = 0; i < 16; i++) {
            new Thread(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    c.countDown();
                    c.await();
                    long seq = Thread.currentThread().getId();
                    System.out.println("---------------------------"+seq);
                    synchronized (redisUtils) {
                        redisUtils.set("process7", "success", (int) (seq%16));
                    }
                }
            }).start();
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void method3(){
        for (int i = 0; i < 15; i++) {
            String process3 = redisUtils.get("process7", i);
            System.out.println("---------------" + process3);

        }
    }

    @Test
    public void method4(){
        System.out.println(StringUtils.isNumeric("0"));
    }

}
