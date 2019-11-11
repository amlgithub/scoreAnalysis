package com.zgczx.service.scoretwo;

import com.zgczx.repository.mysql1.score.dto.MonthByYearListDTO;
import com.zgczx.repository.mysql1.score.model.ManuallyEnterGrades;

import java.util.List;

/**
 * @author aml
 * @date 2019/10/29 12:31
 */

public interface ScoreTwoService {

   ManuallyEnterGrades saveEntity(String wechatOpneid,
                            String studenNumber,
                            String subject,
                            String score,
                            String classRank,
                            String gradeRank,
                            String examName);

   List<ManuallyEnterGrades> saveList(List<ManuallyEnterGrades> list);

   List<String> getYearList(String openid);

   List<MonthByYearListDTO> getMonthByYearList(String openid, String year);

   List<String> getExamNameByYearMonthList(String openid,String yearMonth);

   List<ManuallyEnterGrades> findAll(String openid,String examName);

}
