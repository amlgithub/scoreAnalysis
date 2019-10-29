package com.zgczx.service.scoretwo.impl;

import com.zgczx.dataobject.score.ManuallyEnterGrades;
import com.zgczx.enums.ResultEnum;
import com.zgczx.exception.ScoreException;
import com.zgczx.repository.score.ManuallyEnterGradesDao;
import com.zgczx.service.scoretwo.ScoreTwoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

/**
 * dao还是用原来的dao，就是impl不超过2000行
 * @author aml
 * @date 2019/10/29 12:32
 */
@Service
public class ScoreTwoServiceImpl implements ScoreTwoService {

    private static final Logger logger = LoggerFactory.getLogger(ScoreTwoServiceImpl.class);

    @Autowired
    private ManuallyEnterGradesDao manuallyEnterGradesDao;

    private String info;
    @Override
    public ManuallyEnterGrades saveEntity(String wechatOpneid, String studenNumber, String subject, String score, String classRank, String gradeRank, String examName) {
        ManuallyEnterGrades manuallyEnterGrades = new ManuallyEnterGrades();
        manuallyEnterGrades.setWechatOpenid(wechatOpneid);
        manuallyEnterGrades.setStudentNumber(studenNumber);
        manuallyEnterGrades.setSubjectName(subject);
        manuallyEnterGrades.setScore(score);
        manuallyEnterGrades.setClassRank(classRank);
        manuallyEnterGrades.setGradeRank(gradeRank);
        manuallyEnterGrades.setExamName(examName);
        Timestamp time  = new Timestamp(System.currentTimeMillis());
        manuallyEnterGrades.setInserttime(time);
        manuallyEnterGrades.setUpdatetime(time);
        ManuallyEnterGrades save;
        try {
             save = manuallyEnterGradesDao.save(manuallyEnterGrades);
        }catch (Exception e){
            info = "学号和考试名称已经存在，不能重复插入";
            logger.error("【{}】 {}",info, e);
            throw new ScoreException(ResultEnum.DATABASE_OP_EXCEPTION,info);
        }

        if (save == null){
            throw new ScoreException(ResultEnum.DATA_IS_WRONG,"数据为null");
        }

        return save;
    }
}
