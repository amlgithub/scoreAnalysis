package com.zgczx.repository.mysql1.user.model;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author aml
 * @date 2019/11/20 21:12
 */
@Data
@DynamicInsert
@Entity
@Table(name = "user_feed_back", schema = "score_ananlysis_dev", catalog = "")
public class UserFeedBack implements Serializable {

    //系统反馈Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //用户提交反馈id
    @Basic
    @Column(name = "user_openid")
    private String userOpenid;

    //系统反馈内容
    @Basic
    @Column(name = "content")
    private String content;

    //反馈时间
    @Basic
    @Column(name = "insert_time")
    private Date insertTime;
}
