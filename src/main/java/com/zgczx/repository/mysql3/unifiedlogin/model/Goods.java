package com.zgczx.repository.mysql3.unifiedlogin.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

/**
 * @ClassName: Jason
 * @Author: 陈志恒
 * @Date: 2019/7/8 16:02
 * @Description:
 */
@Entity
@Data
@ToString
@Table(name = "orderinfo")
public class Goods {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)

    /*以这种注解形式，补全每个参数含义*/
    private Integer goodsId;

    /*补全*/
    private String goodsName;
    private Double price;

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
