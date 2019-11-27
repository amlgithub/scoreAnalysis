package com.zgczx.service.scoretwo;

import com.alibaba.fastjson.JSONObject;
import com.zgczx.repository.mysql1.score.dto.MonthByYearListDTO;
import com.zgczx.repository.mysql1.score.model.ManuallyEnterGrades;
import com.zgczx.repository.mysql1.user.model.StudentInfo;
import com.zgczx.repository.mysql2.scoretwo.dto.CommentValueDTO;
import com.zgczx.repository.mysql2.scoretwo.dto.LocationComparisonDTO;
import com.zgczx.repository.mysql2.scoretwo.dto.SingleContrastInfoDTO;
import com.zgczx.repository.mysql2.scoretwo.dto.TotalScoreInfoDTO;

import java.util.List;
import java.util.Map;

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

   StudentInfo verifyStudentCode(String openid, String studentId);

   List<LocationComparisonDTO> getGapValue(String openid, String stuNumber, String examName);

   List<CommentValueDTO> getCommentValue(String openid, String stuNumber, String examName);

   List<TotalScoreInfoDTO> getTotalScoreInfo(String openid, String stuNumber, String examName,String targetRank);

   List<String> getSubjectCollection(String openid, String stuNumber, String examName);

   //List<SingleContrastInfoDTO> getSingleContrastInfo(String openid, String stuNumber, String examName, String list);

   //JSONObject getSingleContrastInfo(Map<String, Object> map);

   List<SingleContrastInfoDTO> getSingleContrastInfo(Map<String, Object> map);

}
