package com.rxue;

import com.rxue.dao.CommentMapper;
import com.rxue.dao.DiscussPostMapper;
import com.rxue.dao.LoginTicketMapper;
import com.rxue.dao.MessageMapper;
import com.rxue.dao.UserMapper;
import com.rxue.entity.Comment;
import com.rxue.entity.DiscussPost;
import com.rxue.entity.LoginTicket;
import com.rxue.entity.Message;
import com.rxue.entity.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
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
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private MessageMapper messageMapper;


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
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0, 0, 10, 0);
        for (DiscussPost post :
                list) {
            System.out.println(list);
        }

        int rows = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(rows);
    }

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(12);
        loginTicket.setTicket("2353");
        loginTicket.setStatus(1);
        loginTicket.setExpired(new Date());

        System.out.println(loginTicketMapper.insertLoginTicket(loginTicket));
    }

    @Test
    public void testSelectByTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("2353");
        System.out.println(loginTicket);
    }

    @Test
    public void testUpdateStatus(){
        System.out.println(loginTicketMapper.updateStatus("2353", 0));
    }

    @Test
    public void testAddDiscussPost(){
        DiscussPost discussPost = new DiscussPost();
        discussPost.setTitle("hello");
        discussPost.setContent("hello,content");
        discussPostMapper.insertDiscussPost(discussPost);
    }

    @Test
    public void testCommentByEntity(){
        List<Comment> comments = commentMapper.selectCommentsByEntity(2, 60, 0, 4);
        for (Comment comment :
                comments) {
            System.out.println("comment = " + comment);
        }
    }

    @Test
    public void testCountByEntity(){
        int count = commentMapper.selectCountByEntity(2, 60);
        System.out.println("count = " + count);
    }

    @Test
    public void testSelectLetters(){
        List<Message> list = messageMapper.selectConversations(111, 0, 20);
        for (Message msg :
                list) {
            System.out.println(msg);
        }

        int count = messageMapper.selectConversationCount(111);
        System.out.println("count = " + count);
    }

    @Test
    public void testUpdateStatus2(){
        List<Integer> ids = new ArrayList<>();
        ids.add(1222);
        int i = messageMapper.updateStatus(ids, 2);
        System.out.println("i = " + i);
    }

    @Test
    public void testNotic(){
        int count = messageMapper.selectNoticeCount(160, "comment");
        System.out.println("count = " + count);
    }

    @Test
    public void testLastNotice(){
        System.out.println("helloworld");
        Message comment = messageMapper.selectLastNotice(160, "comment");
        System.out.println("comment = " + comment);
    }
}
