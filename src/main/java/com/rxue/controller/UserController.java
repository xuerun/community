package com.rxue.controller;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.rxue.annotation.LoginRequired;
import com.rxue.entity.User;
import com.rxue.service.FollowService;
import com.rxue.service.LikeService;
import com.rxue.service.UserService;
import com.rxue.util.CommunityConstant;
import com.rxue.util.HostHolder;
import com.rxue.util.newCoderUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.AUTH;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

/**
 * @author RunXue
 * @create 2022-03-22 21:01
 * @Description
 */
@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private OSS ossClient;

    @Value("${newCoder.path.upload}")
    private String uploadPath;

    @Value("${newCoder.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${aliyun.bucket.headerUrl}")
    private String bucketHeaderUrl;

    @Value("${aliyun.headerEndPoint}")
    private String headerEndPoint;

    @LoginRequired//自定义注解，标识：登录了才能访问
    @GetMapping("/setting")
    public String getSettingPage(){
        return "/site/setting";
    }


    //将头像上传到云服务器
    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(Model model, MultipartFile headerImage){

        if(headerImage == null){
            model.addAttribute("error", "您还没有选择图片！");
            return "/site/setting";
        }

        //获取初始的文件名 需要获取图片的格式，并修改文件的名称（使用随机字符串表示），防止多个用户上传的头像名称一致
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf('.'));//获取文件的类型  .jpg .png
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error", "文件格式不正确！");
            return "/site/setting";
        }

        //生成随机文件名
        filename = newCoderUtil.generateUUID() + suffix;
        try {

            InputStream inputStream = headerImage.getInputStream();

            //PutObjectRequest putObjectRequest = new PutObjectRequest(bucketHeaderUrl, filename,inputStream);
            // 上传文件
            ossClient.putObject(bucketHeaderUrl, filename, inputStream);
        }catch (IOException e) {
            logger.error("头像上传失败：" + e.getMessage());
        }finally {
            if(ossClient != null){
                ossClient.shutdown();
            }
        }

        //修改图像路径
        String headUrl = headerEndPoint + filename;
        userService.updateHeaderUrl(hostHolder.getUser().getId(), headUrl);
        return "redirect:/index";
    }

    ////废弃  上传到本地
    //@LoginRequired
    //@PostMapping("/upload")
    //public String uploadHeader(Model model, MultipartFile headerImage){
    //    if(headerImage == null){
    //        model.addAttribute("error", "您还没有选择图片！");
    //        return "/site/setting";
    //    }
    //
    //    //获取初始的文件名 需要获取图片的格式，并修改文件的名称（使用随机字符串表示），防止多个用户上传的头像名称一致
    //    String filename = headerImage.getOriginalFilename();
    //    String suffix = filename.substring(filename.lastIndexOf('.'));//获取文件的类型  .jpg .png
    //    if(StringUtils.isBlank(suffix)){
    //        model.addAttribute("error", "文件格式不正确！");
    //        return "/site/setting";
    //    }
    //
    //    //生成随机文件名
    //    filename = newCoderUtil.generateUUID() + suffix;
    //    //确定存放的路径
    //    File dest = new File(uploadPath + filename);
    //    //上传图片
    //    //transferTo(File dest) 用来把 MultipartFile 转换换成 File
    //    try {
    //        headerImage.transferTo(dest);
    //    } catch (IOException e) {
    //        logger.error("上传文件失败：" + e.getMessage());
    //        throw new RuntimeException("上传文件失败，服务器发生异常！", e);
    //    }
    //
    //    //更新当前用户的头像路径
    //    User user = hostHolder.getUser();
    //    String headUrl = domain + contextPath + "/user/header/" + filename;//这个header/filename是获取头像的请求
    //    userService.updateHeaderUrl(user.getId(), headUrl);
    //    return "redirect:/index";
    //}


    ////获取头像
    //@GetMapping("/header/{filename}")
    //public void getHeader(HttpServletResponse response, @PathVariable("filename") String filename){
    //    //服务器文件路径
    //    filename = uploadPath +  filename;
    //    String suffix = filename.substring(filename.lastIndexOf("."));
    //    //告诉浏览器这个响应用图片的形式打开
    //    response.setContentType("image/" + suffix);
    //    FileInputStream fileInputStream = null;
    //    //读取图片至前端
    //    try {
    //        fileInputStream = new FileInputStream(filename);
    //        ServletOutputStream outputStream = response.getOutputStream();
    //        byte[] cbuf = new byte[1024];
    //        int len;
    //        while ( (len = fileInputStream.read(cbuf)) != -1){0
    //            outputStream.write(cbuf, 0 , len);
    //        }
    //    } catch (IOException e) {
    //        logger.error("读取头像失败：" + e.getMessage());
    //    }finally {
    //        //response打开的输出流不用我们手动关闭，springMVC会自动关闭
    //        //我们自己生成的输入流需要我们自己关闭
    //        if(fileInputStream != null){
    //            try {
    //                fileInputStream.close();
    //            } catch (IOException e) {
    //                e.printStackTrace();
    //            }
    //        }
    //    }
    //}

    //修改密码
    @LoginRequired
    @PostMapping("/updatePassword")
    public String updatePassword(String oldPassword, String newPassword, Model model){
        //获取user对象
        User user = hostHolder.getUser();
        //获取service层的返回对象，并根据map是否为空进行判断处理
        Map<String, String> map = userService.updatePassword(user, newPassword, oldPassword);

        if(map == null || map.isEmpty()){
            return "redirect:/index";
        }

        model.addAttribute("oldPasswordMsg", map.get("oldPasswordMsg"));
        model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
        return "/site/setting";
    }

    //显示个人主页中的信息和赞数  可能是其他人的个人主页，所以不能用hostholder
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在");
        }
        long likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("user", user);
        model.addAttribute("likeCount", likeCount);

        //关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        //被关注数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        //是否已关注
        boolean hasFollowed = false;
        if(hostHolder.getUser() != null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("followeeCount", followeeCount);
        model.addAttribute("followerCount", followerCount);
        model.addAttribute("hasFollowed", hasFollowed);
        return "/site/profile";
    }
}
