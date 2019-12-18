package com.zgczx.repository.mysql1.exam.dao;

import com.zgczx.repository.mysql1.exam.model.UserCollect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户收藏 表
 * @author aml
 * @date 2019/12/17 16:52
 */
@Repository
public interface UserCollectDao extends JpaRepository<UserCollect, Integer> {
}
