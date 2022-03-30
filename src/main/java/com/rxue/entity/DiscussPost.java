package com.rxue.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.security.Principal;
import java.util.Date;

/**
 * @author RunXue
 * @create 2022-03-20 10:13
 * @Description 帖子贴
 */
@Document(indexName = "discusspost", shards = 6, replicas = 3)
@Data
public class DiscussPost{
    @Id
    private int id;

    @Field(type = FieldType.Integer)
    private int userId;

    //analyzer 存储时的解析器 使用ik_max_word分词器，能拆分出更多的词来
    //searchanalyzer 搜索时的解析器  使用ik_smart分析器，拆分出较少的词来
    //比如互联网校招  ik_max_word 会拆分 互联、联网、互联网、网校、校招
    //ik_smart_word 会拆成互联网、校招  更加智能，适合搜索时使用
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    @Field(type = FieldType.Integer)
    private int type;

    @Field(type = FieldType.Integer)
    private int status;

    @Field(type = FieldType.Date)
    private Date createTime;

    @Field(type = FieldType.Integer)
    private int commentCount;

    @Field(type = FieldType.Double)
    private double score;
}
