package com.rxue.service;

import com.rxue.dao.UserMapper;
import com.rxue.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author RunXue
 * @create 2022-03-20 13:09
 * @Description
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }
}
