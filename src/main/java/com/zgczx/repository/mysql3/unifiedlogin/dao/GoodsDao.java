package com.zgczx.repository.mysql3.unifiedlogin.dao;

import com.zgczx.repository.mysql3.unifiedlogin.model.Goods;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @ClassName: Jason
 * @Author: 陈志恒
 * @Date: 2019/7/8 16:04
 * @Description:
 */
public interface GoodsDao extends JpaRepository<Goods,Integer> {
    Goods findByGoodsId(Integer goodsId);
    Goods findByGoodsName(String name);
}
