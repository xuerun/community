package com.rxue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @author RunXue
 * @create 2022-03-26 16:15
 * @Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NewCoderApplication.class)
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testString(){
        String reidsKey = "test:count";
        redisTemplate.opsForValue().set(reidsKey, 1);
        System.out.println(redisTemplate.opsForValue().get(reidsKey));
    }

    @Test
    public void testMutil() throws InterruptedException {
        String reidsKey = "test:count";
        redisTemplate.opsForValue().set(reidsKey, 24);
        redisTemplate.opsForValue().set("test:age", 1);
        redisTemplate.expire(reidsKey,10, TimeUnit.SECONDS);
        Thread.sleep(12000);
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                Integer count = (Integer) operations.opsForValue().get(reidsKey);
                operations.opsForValue().increment("test:age");
                System.out.println(count);
                operations.opsForValue().set("test:name", "panhu");
                return operations.exec();
            }
        });
        Thread.sleep(5000);
        System.out.println(redisTemplate.opsForValue().get("test:age"));

    }
}
