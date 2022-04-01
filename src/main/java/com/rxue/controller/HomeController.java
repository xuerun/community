package com.rxue.controller;

import com.rxue.entity.DiscussPost;
import com.rxue.entity.Page;
import com.rxue.entity.User;
import com.rxue.service.DiscussPostService;
import com.rxue.service.LikeService;
import com.rxue.service.UserService;
import com.rxue.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author RunXue
 * @create 2022-03-20 13:19
 * @Description
 */
@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @GetMapping("/index")
    public String getIndexPage(Model model, Page page,
                               @RequestParam(name = "orderMode", defaultValue = "0") int orderMode){//index.html中的current会传给page的current属性
        //方法调用前，SpringMVC会自动实例化Model和Page，并将Page注入Model
        //所以thymeleaf中可以直接访问Page对象中的数据
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index?orderMode=" + orderMode);
        List<DiscussPost> list = discussPostService.findDiscussPost(0, page.getOffset(), page.getLimit(), orderMode);
        List<Map<String, Object>> discussPosts = new ArrayList<>();

        //DiscussPost这个类中只有userId，不知道是哪个用户
        //所以用map将User对象和discussPost绑定起来，然后加入list中
        //让前端根据需要获取所需的属性
        if(list != null){
            for (DiscussPost post :
                    list) {
                Map<String, Object> map = new HashMap<>();
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);
                map.put("post", post);

                //点赞数量
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);

                discussPosts.add(map);
            }
        }


        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("orderMode", orderMode);
        return "index";
    }

    @GetMapping("/error")
    public String getErrorPage(){
        return "/error/500";
    }


    //权限不够时拒绝访问的提示页面
    @GetMapping("/denied")
    public String getDeniedPage(){
        return "/error/404";
    }
}
