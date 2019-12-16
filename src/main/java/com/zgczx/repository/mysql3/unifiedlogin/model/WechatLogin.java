package com.zgczx.repository.mysql3.unifiedlogin.model;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * @author aml
 * @date 2019/11/17 19:24
 */
@DynamicUpdate//生成动态的update语句,如果这个字段的值是null就不会被加入到update语句中
@DynamicInsert// 如果这个字段的值是null就不会加入到insert语句当中.
@Entity
@Table(name = "wechat_login", schema = "unified-login", catalog = "")
public class WechatLogin {
    private int id;
    private String openid;
    private String diyid;
    private String nickName;
    private String headimgurl;
    private String wechatSource;
    private Timestamp inserttime;
    private Timestamp updatetime;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "openid")
    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    @Basic
    @Column(name = "diyid")
    public String getDiyid() {
        return diyid;
    }

    public void setDiyid(String diyid) {
        this.diyid = diyid;
    }

    @Basic
    @Column(name = "nick_name")
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Basic
    @Column(name = "headimgurl")
    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    @Basic
    @Column(name = "wechat_source")
    public String getWechatSource() {
        return wechatSource;
    }

    public void setWechatSource(String wechatSource) {
        this.wechatSource = wechatSource;
    }

    @Basic
    @Column(name = "inserttime")
    public Timestamp getInserttime() {
        return inserttime;
    }

    public void setInserttime(Timestamp inserttime) {
        this.inserttime = inserttime;
    }

    @Basic
    @Column(name = "updatetime")
    public Timestamp getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Timestamp updatetime) {
        this.updatetime = updatetime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WechatLogin that = (WechatLogin) o;
        return id == that.id &&
                Objects.equals(openid, that.openid) &&
                Objects.equals(diyid, that.diyid) &&
                Objects.equals(nickName, that.nickName) &&
                Objects.equals(headimgurl, that.headimgurl) &&
                Objects.equals(wechatSource, that.wechatSource) &&
                Objects.equals(inserttime, that.inserttime) &&
                Objects.equals(updatetime, that.updatetime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, openid, diyid, nickName, headimgurl, wechatSource, inserttime, updatetime);
    }
}
