package com.rxue.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

/**
 * @author RunXue
 * @create 2022-03-21 12:42
 * @Description
 */
public class newCoderUtil {

    //生成随机字符串
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    //MD5加密  用户设置的密码 + salt（一段随机的字符串）
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    public static String getJsonString(int code, String msg, Map<String, Object> map){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        if(map != null){
            for (Map.Entry<String, Object> entry:
                    map.entrySet()){
                jsonObject.put(entry.getKey(), entry.getValue());
            }
        }
        return jsonObject.toJSONString();
    }

    //重载
    public static String getJsonString(int code, String msg){
        return getJsonString(code, msg, null);
    }

    public static String getJsonString(int code, Map<String, Object> map){
        return getJsonString(code, null, map);
    }

}
