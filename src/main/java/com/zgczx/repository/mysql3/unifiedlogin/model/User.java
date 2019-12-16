package com.zgczx.repository.mysql3.unifiedlogin.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    private String account;
    private String diyid;
    private String wechatid;
    private String qqid;
    private String email;
    private String role;
    private String passwdmd5Md5;
    private String messagecode;
    private String messagecodeinserttime;
    private String showname;
    private String headimage;
    private String updatetime;
    private String reserve1;
    private String reserve2;
    private String resever3;
    private String reserve4;
    private String reserve5;
    private String openid;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public long getId() {
     return id;
    }

     public void setId(long id) {
      this.id = id;
    }


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }


    public String getDiyid() {
        return diyid;
    }

    public void setDiyid(String diyid) {
        this.diyid = diyid;
    }


    public String getWechatid() {
        return wechatid;
    }

    public void setWechatid(String wechatid) {
        this.wechatid = wechatid;
    }


    public String getQqid() {
        return qqid;
    }

    public void setQqid(String qqid) {
        this.qqid = qqid;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public String getPasswdmd5Md5() {
        return passwdmd5Md5;
    }

    public void setPasswdmd5Md5(String passwdmd5Md5) {
        this.passwdmd5Md5 = passwdmd5Md5;
    }


    public String getMessagecode() {
        return messagecode;
    }

    public void setMessagecode(String messagecode) {
        this.messagecode = messagecode;
    }


    public String getMessagecodeinserttime() {
        return messagecodeinserttime;
    }

    public void setMessagecodeinserttime(String messagecodeinserttime) {
        this.messagecodeinserttime = messagecodeinserttime;
    }


    public String getShowname() {
        return showname;
    }

    public void setShowname(String showname) {
        this.showname = showname;
    }


    public String getHeadimage() {
        return headimage;
    }

    public void setHeadimage(String headimage) {
        this.headimage = headimage;
    }


    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }


    public String getReserve1() {
        return reserve1;
    }

    public void setReserve1(String reserve1) {
        this.reserve1 = reserve1;
    }


    public String getReserve2() {
        return reserve2;
    }

    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2;
    }


    public String getResever3() {
        return resever3;
    }

    public void setResever3(String resever3) {
        this.resever3 = resever3;
    }


    public String getReserve4() {
        return reserve4;
    }

    public void setReserve4(String reserve4) {
        this.reserve4 = reserve4;
    }

    public String getReserve5() {
        return reserve5;
    }

    public void setReserve5(String reserve5) {
        this.reserve5 = reserve5;
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", account='" + account + '\'' +
                ", diyid='" + diyid + '\'' +
                ", wechatid='" + wechatid + '\'' +
                ", qqid='" + qqid + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", passwdmd5Md5='" + passwdmd5Md5 + '\'' +
                ", messagecode='" + messagecode + '\'' +
                ", messagecodeinserttime='" + messagecodeinserttime + '\'' +
                ", showname='" + showname + '\'' +
                ", headimage='" + headimage + '\'' +
                ", updatetime='" + updatetime + '\'' +
                ", reserve1='" + reserve1 + '\'' +
                ", reserve2='" + reserve2 + '\'' +
                ", resever3='" + resever3 + '\'' +
                ", reserve4='" + reserve4 + '\'' +
                ", reserve5='" + reserve5 + '\'' +
                '}';
    }
}
