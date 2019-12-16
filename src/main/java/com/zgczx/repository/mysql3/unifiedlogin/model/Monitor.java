package com.zgczx.repository.mysql3.unifiedlogin.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * @ProjectName login_platform
 * @ClassName Monitor
 * @Author lixu
 * @Date 2019/9/16 13:28
 * @Version 1.0
 * @Description TODO
 */
@Entity
public class Monitor {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private String date;
    private String ip;
    private String name;
    private String country;
    private String address;
    private int pv;
    private int threshold;
    private Timestamp updatetime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPv() {
        return pv;
    }

    public void setPv(int pv) {
        this.pv = pv;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public Timestamp getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Timestamp updatetime) {
        this.updatetime = updatetime;
    }

    @Override
    public String toString() {
        return "Monitor{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", ip='" + ip + '\'' +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", address='" + address + '\'' +
                ", pv=" + pv +
                ", threshold=" + threshold +
                ", updatetime=" + updatetime +
                '}';
    }
}
