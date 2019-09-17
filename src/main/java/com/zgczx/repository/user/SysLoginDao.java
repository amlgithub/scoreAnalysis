package com.zgczx.repository.user;

import com.zgczx.dataobject.user.SysLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author aml
 * @date 2019/9/10 20:03
 */
@Repository
public interface SysLoginDao extends JpaRepository<SysLogin, Integer> {

}
