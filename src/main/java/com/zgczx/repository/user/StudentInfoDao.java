package com.zgczx.repository.user;

import com.zgczx.dataobject.user.StudentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author aml
 * @date 2019/9/10 15:53
 */
@Repository
public interface StudentInfoDao extends JpaRepository<StudentInfo, Integer> {

}
