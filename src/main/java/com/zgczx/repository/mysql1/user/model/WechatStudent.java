package com.zgczx.repository.mysql1.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * @author aml
 * @date 2019/11/8 19:35
 */
@Data
@Entity
@Table(name = "wechat_student", schema = "score_ananlysis_dev", catalog = "")
public class WechatStudent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "foreign_ke_id")
    private Integer foreignKeId;
    @Basic
    @Column(name = "openid")
    private String openid;
    @Basic
    @Column(name = "username")
    private String username;
    @Basic
    @Column(name = "password")
    private String password;
    @Basic
    @Column(name = "student_id")
    private String studentId;
    @Basic
    @Column(name = "school_name")
    private String schoolName;
    @Basic
    @Column(name = "grade")
    private String grade;
    @Basic
    @Column(name = "class_name")
    private String className;
    @Basic
    @Column(name = "phone")
    private String phone;
    @Basic
    @Column(name = "verify_code")
    private String verifyCode;
    //解决返回给前端的是一个时间戳，改成为年月日时分秒格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp inserttime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp updatetime;






}
