package com.zgczx.repository.mysql3.unifiedlogin.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * @ProjectName login_platform
 * @ClassName UserLogin
 * @Author lixu
 * @Date 2019/9/16 16:15
 * @Version 1.0
 * @Description TODO
 */
@Data
@Entity
@Table(name = "user_login")
public class UserLogin {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    @Column(name = "diyid")
    private String diyid;
    @Column(name = "account")
    private String account;
    @Column(name = "passwdmd5")
    private String passwdmd5;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "messagecode")
    private String messagecode;
    @Column(name = "role")
    private String role;
    @Column(name = "shool_code")
    private String shoolCode;
    @Column(name = "school_name")
    private String schoolName;
    @Column(name = "tel_number")
    private String telNumber;
    @Column(name = "qq_id")
    private String qqId;
    @Column(name = "wechat_id")
    private String wechatId;
    @Column(name = "headimg")
    private String headimg;
    @Column(name = "token")
    private String token;
    @Column(name = "state")
    private int state;
    @Column(name = "grade")
    private String grade;
    @Column(name = "level")
    private String level;

    @Column(name = "grade_level")
    private String gradeLevel;
    @Column(name = "class_name")
    private String className;

    private Timestamp updatetime;

    private Timestamp inserttime;
    @Column(name = "confirm_passwordmd5")
    private String confirmPasswordmd5;

    @Column(name = "real_name")
    private String realName;

    @Column(name = "nick_name")
    private String nickName;

    private String sex;

    private String birthday;

    private String location;

    private String signature;


    @Column(name = "serial_number")
    private Long serialNumber;

    @Column(name = "number_id")
    private String numberId;

    private String nation;

    @Column(name = "contact_man")
    private String contactMan;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "contact_relation")
    private String contactRelation;

    @Column(name = "contact_address")
    private String contactAddress;

    private String department;
}
