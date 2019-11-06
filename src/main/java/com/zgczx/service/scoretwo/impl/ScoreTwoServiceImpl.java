package com.zgczx.service.scoretwo.impl;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.zgczx.mapper.ManuallyEnterGradesMapper;
import com.zgczx.repository.mysql1.score.model.ManuallyEnterGrades;
import com.zgczx.enums.ResultEnum;
import com.zgczx.exception.ScoreException;
import com.zgczx.repository.mysql1.score.dao.ManuallyEnterGradesDao;
import com.zgczx.service.scoretwo.ScoreTwoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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

    // 导入mybatis映射SQL语句，不能加private
    @Autowired
    ManuallyEnterGradesMapper manuallyEnterGradesMapper;

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
        logger.info("【实体对象】: {}", manuallyEnterGrades);
        ManuallyEnterGrades save = manuallyEnterGradesDao.save(manuallyEnterGrades);


        // try {
//             save = manuallyEnterGradesDao.save(manuallyEnterGrades);
//        }catch (Exception e){
//            info = "学号和考试名称已经存在，不能重复插入";
//            logger.error("【{}】 {}",info, e);
//            throw new ScoreException(ResultEnum.DATABASE_OP_EXCEPTION,info);
//        }

        if (save == null){
            throw new ScoreException(ResultEnum.DATA_IS_WRONG,"数据为null");
        }

        return save;
    }

    @Override
    public List<ManuallyEnterGrades> saveList(List<ManuallyEnterGrades> list) {
        if (list == null || list.size() == 0){
            info = "list为空";
            logger.error("批量录入list={} ,为空", list);
            throw new ScoreException(ResultEnum.DATA_IS_WRONG,"数据为null");
        }
        List<ManuallyEnterGrades> save = manuallyEnterGradesDao.save(list);
        if (save == null || save.size() == 0){
            info = "批量插入手动录入成绩出错";
            logger.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.SPECIFIED_QUESTIONED_BULK_INSERT_FAILED,info);
        }
        return save;
    }

    @Override
    public List<String> getYearList(String openid) {
        List<String> years = manuallyEnterGradesMapper.getYears(openid);
        if (years == null || years.size() == 0){
            info = "您未录入数据";
            logger.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE,info);
        }
        return years;
    }

    @Override
    public List<String> getMonthByYearList(String openid, String year) {
        String nameYear = "%"+year+"%" ;
        //List<String> months = manuallyEnterGradesMapper.getMonths(openid, nameYear);
        List<String> months = manuallyEnterGradesDao.getExamNameByWechatOpenidAndYear(openid, nameYear);
        List<String> list = new ArrayList<>();
        for (int i = 0 ; i < months.size(); i++){
            int c = months.get(i).indexOf("月");
            String substring = months.get(i).substring(5, c + 1);
            list.add(substring);
        }
        return list;
    }


    @Override
    public List<String> getExamNameByYearMonthList(String openid, String yearMonth) {
        String nameYear = "%"+yearMonth+"%" ;
        List<String> months = manuallyEnterGradesDao.getExamNameByYearMonthAndWechatOpenid(openid, nameYear);
        List<String> list = new ArrayList<>();
        for (int i = 0 ; i < months.size(); i++){
            int c = months.get(i).indexOf("月");
            String substring = months.get(i).substring(c+1, months.get(i).length());
            list.add(substring);
        }
        return list;
    }
}
