package com.rxue.service;

import com.rxue.dao.LoginTicketMapper;
import com.rxue.dao.UserMapper;
import com.rxue.entity.LoginTicket;
import com.rxue.entity.User;
import com.rxue.util.CommunityConstant;
import com.rxue.util.MailClient;
import com.rxue.util.RedisKeyUtil;
import com.rxue.util.newCoderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author RunXue
 * @create 2022-03-20 13:09
 * @Description
 */
@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    //@Autowired
    //private LoginTicketMapper loginTicketMapper;

    //邮件客户端
    @Autowired
    private MailClient mailClient;

    //注入模板引擎Thymeleaf
    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private RedisTemplate redisTemplate;

    //域名
    @Value("${newCoder.path.domain}")
    private String domain;

    //项目路径
    @Value("${server.servlet.context-path}")
    private String contextPath;

    //激活路径
    @Value("${newCoder.path.activation}")
    private String activationPath;

    public User findUserById(int id){
        //return userMapper.selectById(id);
        //从缓存中取值
        User user = getCache(id);
        //取不到值初始化缓存
        if(user == null){
            user = initCache(id);
        }
        return user;
    }


    /**
     * @Description 注册 如果注册信息不正确 返回map提示信息，正确则将用户信息加入数据库
     * 并发送激活码
     * @param user 用户注册时的信息
     * @return
     */
    public Map<String, Object> register(User user){
        Map<String, Object> map = new HashMap<>();

        //对空值进行处理
        if(user == null){
            throw new IllegalArgumentException("参数不能为空！");
        }

        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }

        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }

        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }

        //验证账号是否已有
        User oldUser  = userMapper.selectByName(user.getUsername());
        if (oldUser != null){
            map.put("usernameMsg", "该账号已存在！");
            return map;
        }

        //验证邮箱是否已有
        oldUser = userMapper.selectByEmail(user.getEmail());
        if (oldUser != null) {
            map.put("emailMsg", "该邮箱已被注册 ");
            return map;
        }

        //注册用户

        // 生成salt
        user.setSalt(newCoderUtil.generateUUID().substring(0, 5));
        //将用户设置的密码加上salt进行md5码加密
        user.setPassword(newCoderUtil.md5(user.getPassword() +user.getSalt()));
        //账户的类型设置
        user.setType(0);
        //账户的状态设置
        user.setStatus(0);
        //账户设置激活码
        user.setActivationCode(newCoderUtil.generateUUID());
        //设置随机的头像 用%d作为占位符
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());

        //将用户信息添加到数据库中
        userMapper.insertUser(user);

        //发送激活邮件
        Context context = new Context();//thymeleaf的context， 可以通过templateEngine将值传过去
        context.setVariable("email", user.getEmail());
        //激活链接 http://localhost:8080/community/activation/101/code  该链接中携带userId和激活码信息
        String url = domain + contextPath + activationPath + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        System.out.println("url = " + url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }



    /**
     * @Description 激活
     * @param userId 从激活链接中获得的用户id
     * @param code 从激活链接中获得的激活码
     * @return 激活的状态 成功、失败、重复激活
     */
    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId, 1);

            //数据更新时清除缓存数据
            clearCache(userId);

            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAIL;
        }
    }

    //登录功能
    public Map<String, String> login(String username, String password, int expiredSecondes){
        Map<String, String> map = new HashMap<>();

        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg", "该账号不存在！");
            return map;
        }

        if(user.getStatus() == 0){
            map.put("usernameMsg", "该账号未激活！");
            return map;
        }

        password = newCoderUtil.md5(password + user.getSalt());
        System.out.println(password);
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg", "密码不正确！");
            return map;
        }

        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(0);
        loginTicket.setTicket(newCoderUtil.generateUUID());
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSecondes * 1000));
        //loginTicketMapper.insertLoginTicket(loginTicket);

        //获取登录凭证的key
        String ticketKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        //存入Redis中
        redisTemplate.opsForValue().set(ticketKey, loginTicket);

        //返回登录凭证给浏览器
        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    //退出功能
    public void logout(String ticket){
        //loginTicketMapper.updateStatus(ticket, 1);
        //在Redis中更改状态
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(ticketKey, loginTicket);
    }

    //根据ticket查询LoginTicket
    public LoginTicket findLoginTicket(String ticket){
        //return loginTicketMapper.selectByTicket(ticket);
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
    }

    //更新用户的头像
    public void updateHeaderUrl(int userId, String headerUrl){
        userMapper.updateHeader(userId, headerUrl);
        //清除缓存
        clearCache(userId);
    }

    //修改密码
    public Map<String, String> updatePassword(User user, String newPassword, String oldPassword){
        Map<String, String> map = new HashMap<>();

        if(StringUtils.isBlank(oldPassword)){
            map.put("newPasswordMsg", "原始密码不能为空！");
            return map;
        }

        if(StringUtils.isBlank(newPassword)){
            map.put("newPasswordMsg", "新的密码不能为空！");
            return map;
        }

        //判断原始密码是否正确
        oldPassword = newCoderUtil.md5(oldPassword + user.getSalt());
        if (!user.getPassword().equals(oldPassword)) {
            map.put("oldPasswordMsg", "原始密码错误！");
            return map;
        }

        //将新密码添加进数据库
        userMapper.updatePassword(user.getId(), newCoderUtil.md5(newPassword + user.getSalt()));
        return map;
    }

    public User findByUserName(String username){
        return userMapper.selectByName(username);
    }


    //优先从缓存中取值
    private User getCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }

    //取不到时初始化缓存数据
    private User initCache(int userId){
        User user = userMapper.selectById(userId);
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(userKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    //数据更新时删除缓存数据（先更新后清除）
    private void clearCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }
}
