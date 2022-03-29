package com.rxue.dao;

import com.rxue.entity.LoginTicket;
import org.apache.ibatis.annotations.Param;

/**
 * @author RunXue
 * @create 2022-03-22 10:14
 * @Description
 */
@Deprecated//过时的不推荐使用，用redis存登录凭证
public interface LoginTicketMapper {
    int insertLoginTicket(LoginTicket loginTicket);

    LoginTicket selectByTicket(String ticket);

    int updateStatus(@Param("ticket") String ticket, @Param("status") int status);
}
