package com.zgczx.mapper;

import com.zgczx.repository.mysql1.score.model.ManuallyEnterGrades;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 这个实体类的mapper接口，主要查询此类中的所有数据接口方法
 * @author aml
 * @date 2019/11/6 13:51
 */
public interface ManuallyEnterGradesMapper {

    List<ManuallyEnterGrades> getManuallyEnterGrades();

    ManuallyEnterGrades getManuallyEnterGradesById(int id);

    // 获取此用户的所有录入数据的年份
    List<String> getYears(String openid);

    // 获取此用户的所有录入数据的年份
    List<String> getMonths(@Param("openid")String openid, @Param("year")String year);
}
