package com.zgczx.repository.mysql1.user.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * @author aml
 * @date 2019/9/10 20:02
 */
@Entity
@Data
@Table(name = "sys_login", schema = "score_ananlysis_dev", catalog = "")
public class SysLogin {
    private int id;
    private String username;
    private String password;
    private Timestamp createTime;
    private String email;
    private String imageUrl;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic
    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "create_time")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Basic
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "image_url")
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SysLogin sysLogin = (SysLogin) o;
        return id == sysLogin.id &&
                Objects.equals(username, sysLogin.username) &&
                Objects.equals(password, sysLogin.password) &&
                Objects.equals(createTime, sysLogin.createTime) &&
                Objects.equals(email, sysLogin.email) &&
                Objects.equals(imageUrl, sysLogin.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, createTime, email, imageUrl);
    }
}
