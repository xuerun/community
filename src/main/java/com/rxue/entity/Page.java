package com.rxue.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author RunXue
 * @create 2022-03-20 14:58
 * @Description 封装分页相关的信息
 */
public class Page {
    //当前页码 默认为1
    private int current = 1;
    //显示上限 每页显示的数据
    private int limit = 10;
    //数据总数
    private int rows;
    //查询路径（用于复用分页连接）
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current > 1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Page{" +
                "current=" + current +
                ", limit=" + limit +
                ", rows=" + rows +
                ", path='" + path + '\'' +
                '}';
    }

    /**
     * 获取当前页的起始行（当前页面第一条数据的偏移量）
     * mysql中第一条记录的偏移量为0
     *
     * @return
     */
    public int getOffset() {
        //current*limit -limit
        return (current - 1)*limit;
    }

    /**
     * @Description 获取总页数  不能整除要加一
     * @return
     */
    public int getTotal() {
        return rows % limit == 0 ? rows / limit : rows / limit + 1;
    }

    /**
     * @Description 获取起始页码（获取当前页前两页的页码）
     * @return
     */
    public int getFrom(){
        int from = current - 2;
        return from < 1? 1 : from;
    }

    /**
     * @Description 获取结束页码（获取当前页后两页的页码）
     * @return
     */
    public int getTo(){
        int to = current + 2;
        return to > getTotal()? getTotal() : to;
    }
}
