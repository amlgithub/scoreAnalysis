package com.zgczx.service.scoretwo;

import com.zgczx.dataobject.score.ManuallyEnterGrades;

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
}
