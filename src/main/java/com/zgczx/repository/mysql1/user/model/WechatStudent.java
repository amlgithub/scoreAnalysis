package com.zgczx.repository.mysql1.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
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

    @NotEmpty(message = "统一登录平台的主键id，用作此表的外键，必填")
    @Basic
    @Column(name = "foreign_ke_id")
    private Integer foreignKeId;

    @NotEmpty(message = "用户openid必填")
    @Basic
    @Column(name = "openid")
    private String openid;

    @NotEmpty(message = "注册的用户名，必填且唯一")
    @Basic
    @Column(name = "username")
    private String username;

    @NotEmpty(message = "注册的密码，必填")
    @Size(min = 4,max = 20,message = "密码的长度应该在4和20之间")
    @Basic
    @Column(name = "password")
    private String password;

    @Basic
    @Column(name = "student_id")
    private String studentId;//学号，可以暂时不用注册

    @NotEmpty(message = "学校全称，必填")
    @Basic
    @Column(name = "school_name")
    private String schoolName;

    @NotEmpty(message = "年级全称，例如：高一，必填")
    @Basic
    @Column(name = "grade")
    private String grade;

    @NotEmpty(message = "班级名称，必填")
    @Basic
    @Column(name = "class_name")
    private String className;


    @Pattern(regexp = "^1(3|4|5|7|8)\\d{9}$",message = "手机号码格式错误")
    @NotBlank(message = "手机号码不能为空")
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
