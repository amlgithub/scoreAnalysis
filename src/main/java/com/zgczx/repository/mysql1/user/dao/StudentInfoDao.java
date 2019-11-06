package com.zgczx.repository.mysql1.user.dao;

import com.zgczx.repository.mysql1.user.model.StudentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author aml
 * @date 2019/9/10 15:53
 */
@Repository
public interface StudentInfoDao extends JpaRepository<StudentInfo, Integer> {

}
