package com.rxue;

import com.rxue.util.RedisKeyUtil;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
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

    //统计二十万个重复数据的独立总数
    @Test
    public void testHyperLogLog(){
        //String redisKey = "test:hll:01";
        //for (int i = 0; i <= 100000; i++) {
        //    redisTemplate.opsForHyperLogLog().add(redisKey, i);
        //}
        //
        //for (int i = 0; i <= 100000; i++) {
        //    redisTemplate.opsForHyperLogLog().add(redisKey, (int)(Math.random()*100000 + 1));
        //}
        //System.out.println(redisTemplate.opsForHyperLogLog().size(redisKey));
        String uvKey = RedisKeyUtil.getUvKey(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        redisTemplate.opsForHyperLogLog().add(uvKey, 3, 4);
        System.out.println("redisTemplate.opsForHyperLogLog().size(uvKey) = " + redisTemplate.opsForHyperLogLog().size(uvKey));
    }

    //统计一组数据的布尔值
    @Test
    public void testBitMap(){
        String redisKey = "test:bm:01";

        redisTemplate.opsForValue().setBit(redisKey,1,true);
        redisTemplate.opsForValue().setBit(redisKey, 4, true);
        redisTemplate.opsForValue().setBit(redisKey, 5, true);

        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 1));

        //统计
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount(redisKey.getBytes());
            }
        });

    }

}
