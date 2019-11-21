package com.zgczx.repository.mysql1.user.dao;

import com.zgczx.repository.mysql1.user.model.UserFeedBack;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author aml
 * @date 2019/9/6 21:15
 */
public interface UserFeedBackDao extends JpaRepository<UserFeedBack, Long> {
}
