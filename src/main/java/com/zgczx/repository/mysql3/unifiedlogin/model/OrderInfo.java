package com.zgczx.repository.mysql3.unifiedlogin.model;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * @ClassName: Jason
 * @Author: 陈志恒
 * @Date: 2019/5/23 13:06
 * @Description:
 */
@Entity
@Data
@ToString
@Table(name = "orderinfo")
@DynamicUpdate
@DynamicInsert
public class OrderInfo {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    /*用户微信openid*/
    private String openid;
    /*用户是否支付过*/
    private Boolean flags;
    /*订单号*/
    private String orderId;
    /*商品号*/
    private Integer goodsId;
    private Date createTime;

    private Date updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Boolean getFlags() {
        return flags;
    }

    public void setFlags(Boolean flags) {
        this.flags = flags;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
