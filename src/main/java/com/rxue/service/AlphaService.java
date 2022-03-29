package com.rxue.service;

import com.rxue.dao.DiscussPostMapper;
import com.rxue.dao.UserMapper;
import com.rxue.entity.DiscussPost;
import com.rxue.entity.User;
import com.rxue.util.newCoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

/**
 * @author RunXue
 * @create 2022-03-24 15:15
 * @Description
 */
@Service
public class AlphaService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    //声明式事务
    //事务A调事务B
    //REQUIRED 支持当前事务（外部事务），如果不存在则创建新事务
    //REQUIRED_NEW 创建一个新的事务，并且暂停当前事务（外部事务）
    //NESTED 如果当前存在事务（外部事务），则嵌套在该事务中执行（独立的提交和回滚），否则就会和REQUIRED一样
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1(){
        //新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(newCoderUtil.generateUUID().substring(0, 5));
        user.setPassword(newCoderUtil.md5("123" + user.getSalt()));
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setEmail("alpha@qq.com");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //新增帖子
        DiscussPost discussPost = new DiscussPost();
        discussPost.setCreateTime(new Date());
        discussPost.setTitle("hello");
        discussPost.setContent("新人报道");
        discussPost.setUserId(user.getId());
        discussPostMapper.insertDiscussPost(discussPost);

        Integer.valueOf("abc");
        return "ok";
    }

    //编程式事务
    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                //新增用户
                User user = new User();
                user.setUsername("beta");
                user.setSalt(newCoderUtil.generateUUID().substring(0, 5));
                user.setPassword(newCoderUtil.md5("123" + user.getSalt()));
                user.setHeaderUrl("http://image.nowcoder.com/head/999.png");
                user.setEmail("beta@qq.com");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                //新增帖子
                DiscussPost discussPost = new DiscussPost();
                discussPost.setCreateTime(new Date());
                discussPost.setTitle("你好");
                discussPost.setContent("我是新人报道");
                discussPost.setUserId(user.getId());
                discussPostMapper.insertDiscussPost(discussPost);

                Integer.valueOf("abc");
                return "ok";
            }
        });
    }
}
