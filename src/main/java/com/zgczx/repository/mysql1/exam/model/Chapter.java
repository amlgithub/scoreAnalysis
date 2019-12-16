package com.zgczx.repository.mysql1.exam.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 章节表
 * @author aml
 * @date 2019/12/11 11:23
 */
@DynamicUpdate//生成动态的update语句,如果这个字段的值是null就不会被加入到update语句中
@DynamicInsert// 如果这个字段的值是null就不会加入到insert语句当中.
@Entity
@Data
@Table(name="e_chapter")
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "subject_id")
    private int subjectId;

    private String subject;
    private String chapter;

    private String section;

    private String level;
    @Column(name = "level_name")
    private String levelName;

    //解决返回给前端的是一个时间戳，改成为年月日时分秒格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp inserttime;
    //解决返回给前端的是一个时间戳，改成为年月日时分秒格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp updatetime;

}
