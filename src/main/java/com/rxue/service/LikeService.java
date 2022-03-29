package com.rxue.service;

import com.rxue.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @author RunXue
 * @create 2022-03-26 18:46
 * @Description 点赞业务
 */
@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    //点赞，第一次为点赞，点击第二次为取消点赞
    //entityType和entityId是为了拼接处key
    //userId 是为了存值
    public void like(int entityType, int entityId, int userId, int entityUserId){
        //String likeEntityKey = RedisKeyUtil.getLikeEntityKey(entityType, entityId);
        ////判断是第几次点击赞
        //Boolean isMember = redisTemplate.opsForSet().isMember(likeEntityKey, userId);
        //if(isMember){//如果已经点赞了 就将userId从set中移除
        //    redisTemplate.opsForSet().remove(likeEntityKey, userId);
        //}else {//没点赞 就将userId添加到set中
        //    redisTemplate.opsForSet().add(likeEntityKey, userId);
        //}

        //重构点赞功能  加入以userId为key的统计点赞数量
        //有两条增加数据需要满足原子性 所以需要使用到redis的事务
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String likeEntityKey = RedisKeyUtil.getLikeEntityKey(entityType, entityId);
                //这个user对象时被点赞的对象
                String likeUserKey = RedisKeyUtil.getLikeUserKey(entityUserId);
                //判断是第几次点赞 这个要写在事务的外面，因为如果加入到事务的里面不会立即执行，只需把插入的那两步加入事务就行了
                Boolean isMember = operations.opsForSet().isMember(likeEntityKey, userId);
                //开启事务
                operations.multi();
                if(isMember){
                    operations.opsForSet().remove(likeEntityKey, userId);
                    operations.opsForValue().decrement(likeUserKey);
                }else {
                    operations.opsForSet().add(likeEntityKey, userId);
                    operations.opsForValue().increment(likeUserKey);
                }
                //执行事务
                return operations.exec();
            }
        });
    }

    //查阅某实体点赞的数量  注意点赞的数量可能会有很多  用long类型接收
    public long findEntityLikeCount(int entityType, int entityId){
        String likeEntityKey = RedisKeyUtil.getLikeEntityKey(entityType, entityId);
        return redisTemplate.opsForSet().size(likeEntityKey);
    }

    //查询某人对某实体的点赞状态，用int来接收（具有扩展性），后面还有踩，bool表现不出来第三种状态
    public int findEntityLikeStatus(int entityType, int entityId, int userId){
        String likeEntityKey = RedisKeyUtil.getLikeEntityKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(likeEntityKey, userId)? 1 : 0;
    }

    //查询某个用户获得的赞
    public long findUserLikeCount(int userId){
        String likeUserKey = RedisKeyUtil.getLikeUserKey(userId);
        Integer userLikeCount = (Integer)redisTemplate.opsForValue().get(likeUserKey);
        return userLikeCount==null?0:userLikeCount;
    }
}
