package com.rxue.controller;

import com.google.code.kaptcha.Producer;
import com.rxue.config.KaptchaConfig;
import com.rxue.dao.LoginTicketMapper;
import com.rxue.entity.User;
import com.rxue.service.UserService;
import com.rxue.util.CommunityConstant;
import com.rxue.util.RedisKeyUtil;
import com.rxue.util.newCoderUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author RunXue
 * @create 2022-03-21 10:57
 * @Description
 */
@Controller
public class LoginController implements CommunityConstant {

    private final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    //点击注册  跳转到注册页面
    @GetMapping("/register")
    public String getRegisterPage(){
        return "/site/register";
    }

    //跳转到登录页面
    @GetMapping("/login")
    public String getLoginPage(){
        return "/site/login";
    }

    @PostMapping("/register")
    public String register(Model model, User user){
        Map<String, Object> map = userService.register(user);
        if(map == null || map.isEmpty()){
            model.addAttribute("msg", "注册成功，我们已经向您的邮箱发送了一份激活邮件，请您尽快激活");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        }else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    //激活链接 http://localhost:8080/community/activation/101/code
    //Restful风格
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code){
        int result = userService.activation(userId, code);
        System.out.println("收到请求");
        if(result == ACTIVATION_SUCCESS){//激活成功跳到登录界面
            model.addAttribute("msg", "激活成功，您的账号已经可以正常使用了！");
            model.addAttribute("target", "/login");
        }else if(result == ACTIVATION_REPEAT){//重复操作跳到首页
            model.addAttribute("msg", "无效的操作，该账号已经激活过了！");
            model.addAttribute("target", "/index");
        }else {//激活失败跳到首页
            model.addAttribute("msg", "激活失败，您提供的激活码不正确！");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    @ResponseBody
    @RequestMapping("/kaptcha")
    public void getKaptcha(/*HttpSession session,*/ HttpServletResponse response){
        //生成验证码
        String text = kaptchaProducer.createText();
            //将text作为参数传给createImage就可生成对应到的验证码
        BufferedImage image = kaptchaProducer.createImage(text);

        //将验证码存入session（敏感数据 不便放入cookie中）
        //session.setAttribute("kaptcha", text);

        //将验证码存入Redis，并返回给浏览器一个kaptchaOwner的随机字符串，用来拼接验证码的键
        String kaptchaOwner = newCoderUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        //获取验证码的键
        String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        //将验证码存入Redis，并设置过期的时间
        redisTemplate.opsForValue().set(kaptchaKey, text, 60, TimeUnit.SECONDS);


        //将图片输出给浏览器
        //告诉浏览器，这个响应用图片的方式打开
        response.setContentType("image/png");
        //把图片写给浏览器
        try {
            ImageIO.write(image, "png", response.getOutputStream());
        } catch (IOException e) {
            logger.error("响应验证码失败" + e.getMessage());
        }
    }


    /**
     * @Description 登录功能
     * @param username 前端传过来的用户名
     * @param password 传过来的密码
     * @param code 验证码
     * @param rememberme 是否勾选了记住我
     * @param response 响应体 用来返回cookie给前端
     * @param session 从session中获取验证码  用来和前端传过来的做匹配
     * @param model 通过model 将map中的信息返回给前端
     * @return
     */
    @PostMapping("/login")
    public String login(String username, String password, String code, boolean rememberme, HttpServletResponse response,
                        /*HttpSession session,*/ Model model, @CookieValue("kaptchaOwner") String kaptchaOwner){
        //检查验证码
        //String kaptcha = (String) session.getAttribute("kaptcha");//从session中获取验证码


        String kaptcha = null;
        if(!StringUtils.isBlank(kaptchaOwner)){
            //获取验证码的键
            String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(kaptchaKey);
        }


        if(StringUtils.isBlank(code) || StringUtils.isBlank(kaptcha) || !kaptcha.equalsIgnoreCase(code)){//验证码检验或略大小写
            model.addAttribute("codeMsg", "验证码错误！");
            return "/site/login";
        }

        //设置登录过期时间
        int expiredSeconds = rememberme? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        //检查账户和密码
        Map<String, String> map = userService.login(username, password, expiredSeconds);
        if(map.containsKey("ticket")){//账户密码验证成功
            //生成cookie
            Cookie cookie = new Cookie("ticket", map.get("ticket"));
            //设置生效范围和时间
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index"; //重定向
        }else {//验证失败
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            return "/site/login";
        }
    }

    //退出功能
    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        //清除凭证  生成凭证在LoginTicketInterceptor中
        SecurityContextHolder.clearContext();
        return "redirect:/login";//重定向默认是get请求
    }
}
