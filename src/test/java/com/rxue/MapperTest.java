package com.rxue;

import com.rxue.dao.DiscussPostMapper;
import com.rxue.dao.UserMapper;
import com.rxue.entity.DiscussPost;
import com.rxue.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

/**
 * @author RunXue
 * @create 2022-03-20 08:26
 * @Description
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = NewCoderApplication.class)
public class MapperTest {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("123435");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int row = userMapper.insertUser(user);
        System.out.println(row);
        System.out.println(user.getId());
    }

    @Test
    public void  testUpdateUser(){
        int row = userMapper.updateStatus(150, 1);
        System.out.println(row);

        row = userMapper.updatePassword(150, "fserwe");
        System.out.println(row);

        row = userMapper.updateHeader(150, "http://www.nowcoder.com/102.png");
        System.out.println(row);
    }

    @Test
    public void testSelectDiscussPosts(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0, 0, 10);
        for (DiscussPost post :
                list) {
            System.out.println(list);
        }

        int rows = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(rows);
    }
}
