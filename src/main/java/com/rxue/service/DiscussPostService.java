package com.rxue.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.rxue.dao.DiscussPostMapper;
import com.rxue.entity.DiscussPost;
import com.rxue.util.SensitiveFilter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author RunXue
 * @create 2022-03-20 13:04
 * @Description
 */
@Service
public class DiscussPostService {

    private static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    @Autowired
    private RedisTemplate redisTemplate;

    // Caffeine核心接口: Cache, LoadingCache, AsyncLoadingCache
    //帖子列表缓存
    private LoadingCache<String, List<DiscussPost>> postListCache;

    //帖子总数缓存
    private LoadingCache<Integer, Integer> postRowsCache;

    @PostConstruct
    private void init(){
        //初始化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Override
                    public @Nullable List<DiscussPost> load(@NonNull String key) throws Exception {
                        if(key == null || key.length() == 0){
                            throw new IllegalArgumentException("参数错误");
                        }

                        String[] params = key.split(":");
                        if(params == null || params.length != 2){
                            throw new IllegalArgumentException("参数错误");
                        }
                        int offset = Integer.parseInt(params[0]);
                        int limit = Integer.parseInt(params[1]);

                        //二级缓存：Redis-》mysql
                        //如果本地缓存没有，再在二级缓存中查找
                        //查到就返回，如果没有查到，就去数据库中找
                        //并将数据库中找到的数据缓存到redis中
                        Object obj = redisTemplate.opsForValue().get(key);
                        if(obj instanceof List){
                            logger.info("load post list from Redis");
                            return (List<DiscussPost>) obj;
                        }

                        logger.info("load post list from DB");
                        //从数据库中查找
                        List<DiscussPost> discussPostList = discussPostMapper.selectDiscussPosts(0, offset, limit, 1);
                        redisTemplate.opsForValue().set(key, discussPostList, 600, TimeUnit.SECONDS);

                        return discussPostList;
                    }
                });


        //初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public @Nullable Integer load(@NonNull Integer key) throws Exception {
                        //这个key就是0 ，就是下面的userId=0
                        logger.info("load post rows from DB");
                        //从数据库中查找
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });
    }

    public List<DiscussPost> findDiscussPost(int usreId, int offset, int limit, int orderMode){
        ////只缓存热门帖子
        if(usreId == 0 && orderMode == 1){
            return postListCache.get(offset + ":" + limit);
        }

        //logger.info("load post list from DB");
        return  discussPostMapper.selectDiscussPosts(usreId, offset, limit, orderMode);
    }

    public int findDiscussPostRows(int userId){
        //只缓存帖子总数
        if(userId == 0){
            //这个userId没有意义，只不过这个方法必须要一个参数
            return postRowsCache.get(userId);
        }

        //logger.info("load post rows from DB");
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int addDiscussPost(DiscussPost post){
        if(post == null){
            throw new IllegalArgumentException("参数不能为空！");
        }

        //对html编码转译
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        //过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    public DiscussPost findDiscussById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }

    //更新评论的数量
    public int updateCommentCount(int id, int commentCount){
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    public int updateType(int id, int type){
        return discussPostMapper.updateType(id, type);
    }

    public int updateStatus(int id, int status){
        return discussPostMapper.updateStatus(id, status);
    }

    public int updateScore(int id, double score){
        return discussPostMapper.updateScore(id, score);
    }
}
