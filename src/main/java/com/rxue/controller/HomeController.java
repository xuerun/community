package com.rxue.controller;

import com.rxue.dao.DiscussPostMapper;
import com.rxue.dao.UserMapper;
import com.rxue.entity.DiscussPost;
import com.rxue.entity.Page;
import com.rxue.entity.User;
import com.rxue.service.DiscussPostServiec;
import com.rxue.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
public class HomeController {
    @Autowired
    private DiscussPostServiec discussPostServiec;

    @Autowired
    private UserService userService;

    @GetMapping("/index")
    public String getIndexPage(Model model, Page page){
        //方法调用前，SpringMVC会自动实例化Model和Page，并将Page注入Model
        //所以thymeleaf中可以直接访问Page对象中的数据
        page.setRows(discussPostServiec.findDiscussPostRows(0));
        page.setPath("/index");
        List<DiscussPost> list = discussPostServiec.findDiscussPost(0, page.getOffset(), page.getLimit());
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
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        return "index";
    }
}
