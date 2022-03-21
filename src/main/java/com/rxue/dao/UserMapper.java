package com.rxue.dao;

import com.rxue.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author RunXue
 * @create 2022-03-19 21:39
 * @Description
 */

public interface UserMapper {

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);//返回值是在第多少行插入的

    int updateStatus(@Param("id") int id, @Param("status") int status);//返回值是修改了多少行

    int updateHeader(@Param("id") int id, @Param("headerUrl") String headerUrl);

    int updatePassword(@Param("id") int id,  @Param("password") String password);

}
