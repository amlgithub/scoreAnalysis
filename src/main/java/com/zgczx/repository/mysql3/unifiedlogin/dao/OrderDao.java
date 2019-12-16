package com.zgczx.repository.mysql3.unifiedlogin.dao;

import com.zgczx.repository.mysql3.unifiedlogin.model.OrderInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDao extends JpaRepository<OrderInfo,Integer> {
    /**
     *根据用户微信openid与货物id查看订单详情信息
     *
     * @Author chen
     * @Date 16:08 2019/7/8
     * @param openid 微信openid
     * @param goods 货物openid
     * @return
     **/
    OrderInfo findByOpenidAndGoodsId(String openid, Integer goods);

    OrderInfo findByOrderId(String orderId);
   // List<OrderInfo> findByFlagsAndGroupId(Boolean istrue, Integer groupId);
    //List<OrderInfo>findByFlagsAndGroupIdAndUpdateTimeBetween(Boolean istrue, Integer groupId, Date startTime, Date endTime);
   // List<OrderInfo>findByOpenidAndFlags(String openid, Boolean istrue);
   /* @Query("SELECT openid,COUNT(oi.openid) as cs FROM OrderInfo oi WHERE oi.groupId=?1 AND oi.flags=TRUE GROUP BY oi.openid ORDER BY cs DESC")
    public Page<Object[]> findGroupUserPayCount(Integer groupId, Pageable pageable);*/
}
