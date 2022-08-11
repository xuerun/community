package com.rxue.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author RunXue
 * @create 2022-03-23 18:39
 * @Description 敏感词过滤器
 */
@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACEMENT = "***";

    //根节点 根节点中没有值
    private TrieNode rootNode = new TrieNode();

    @PostConstruct//在构造器方法调用之后执行
    public void init(){//读取敏感词文件，将敏感词加入到前缀树中
        BufferedReader bfr = null;
        try {
            //类加载器从类路径classPath下加载资源
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-word.txt");
            if(is != null){
                //将字节流转为字符流，再套接成缓冲流
                bfr = new BufferedReader(new InputStreamReader(is));
                String keyword;
                while ((keyword = bfr.readLine()) != null){//txt中每行一个敏感词
                    //添加至前缀树
                    this.addKeyword(keyword);
                }
            }
        } catch (Exception e) {
            logger.error("加载敏感词文件失败！" + e.getMessage());
        } finally {
            if(bfr != null){
                try {
                    bfr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //将一个敏感词加入到前缀树中
    private void addKeyword(String keyword){
        TrieNode tempNode = rootNode;//每次添加敏感词从根节点开始添加，每条链表都是一个敏感词
        for (int i = 0; i < keyword.length(); i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode == null){//判端当前父节点下有没有存放c的子节点，如果没有则创建子节点，并添加c
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }
            //指向子节点进行下一轮循环
            tempNode = subNode;

            //在敏感词的结尾设置结束标识
            if(i == keyword.length() -1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * @Description 过滤敏感词
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        //指向前缀树的节点  指针1
        TrieNode tempNode = rootNode;
        //指针2 用来遍历text  指针2疑似敏感词是停止，指向敏感词的头部
        int begin = 0;
        //指针3  指针3每次循环都要走
        int position = 0;
        //结果
        StringBuilder sb = new StringBuilder();
        while (position < text.length()){
            char c = text.charAt(position);

            //跳过字符
            if (isSymbol(c)) {
                // 若指针1处于根节点，将此符号计入结果，让指针2指向下一步
                if (tempNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                //无论符号在开头或者中间，指针3都向下走一步
                position++;
                continue;
            }

            //检查下一节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                //说明begin指向的字符不是敏感词的组成部分
                sb.append(text.charAt(begin));
                begin++;
                position = begin;
                //重新指向根节点，从下一个字符来判断是否是敏感词
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd) {
                //isKeywordEnd为true的话，说明已经找到了敏感词，用替换符进行替换，
                sb.append(REPLACEMENT);
                position++;
                begin = position;
                tempNode = rootNode;
            } else {
                //检查下一个字符
                position++;
            }
            //}else {
            //    //position遍历越界未匹配到敏感词不代表以begin的下一位开始不是敏感词的的开始，比如abc是敏感词，text的最后四个字符是fabc，而根节点中有个敏感词为fabd
            //    //这时候回顺着fabd这个分支去寻找，没找到应该让begin右移移位继续寻找
            //    sb.append(text.charAt(begin));
            //    begin++;
            //    position = begin;
            //    tempNode = rootNode;
            //}
        }
        return sb.toString();
    }

    // 判断是否为符号
    private boolean isSymbol(Character c){
        //0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //定义前缀树
    private class TrieNode{
        //关键词结束标识  当为true时，说明root到这个节点之间是一个敏感词
        private boolean isKeywordEnd = false;

        //子节点 key是下级字符，value是下级节点  每一个父节点下可能有多个子节点 不在节点内存值，而是将值作为key
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd(){
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd){
            isKeywordEnd = keywordEnd;
        }

        //添加子节点
        public void addSubNode(Character c, TrieNode node){
            subNodes.put(c, node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }
}
