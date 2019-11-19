package com.zgczx.service.scoretwo.impl;

import com.zgczx.mapper.ManuallyEnterGradesMapper;
import com.zgczx.repository.mysql1.score.dto.MonthByYearListDTO;
import com.zgczx.repository.mysql1.score.model.ManuallyEnterGrades;
import com.zgczx.enums.ResultEnum;
import com.zgczx.exception.ScoreException;
import com.zgczx.repository.mysql1.score.dao.ManuallyEnterGradesDao;
import com.zgczx.repository.mysql1.user.dao.StudentInfoDao;
import com.zgczx.repository.mysql1.user.model.StudentInfo;
import com.zgczx.repository.mysql2.scoretwo.dao.ExamCoversionTotalDao;
import com.zgczx.repository.mysql2.scoretwo.dto.LocationComparisonDTO;
import com.zgczx.repository.mysql2.scoretwo.model.ExamCoversionTotal;
import com.zgczx.service.scoretwo.ScoreTwoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.*;

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

    @Autowired
    StudentInfoDao studentInfoDao;

    @Autowired
    private ExamCoversionTotalDao examCoversionTotalDao;

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
    public List<MonthByYearListDTO> getMonthByYearList(String openid, String year) {
        String nameYear = "%"+year+"%" ;
        // 通过mybatis方式查询数据
        //List<String> months = manuallyEnterGradesMapper.getMonths(openid, nameYear);
        //通过jpa方式查询数据
        List<String> months = manuallyEnterGradesDao.getExamNameByWechatOpenidAndYear(openid, nameYear);
        List<String> list = new ArrayList<>();
        for (int i = 0 ; i < months.size(); i++){
            int c = months.get(i).indexOf("月");
            String substring = months.get(i).substring(5, c + 1);
            list.add(substring);
        }
        int countTimes = manuallyEnterGradesDao.countByWechatOpenidAndExamName(openid, nameYear);
        List<MonthByYearListDTO> listDTOS = new ArrayList<>();
        MonthByYearListDTO monthByYearListDTO = new MonthByYearListDTO();
        monthByYearListDTO.setList(list);
        monthByYearListDTO.setCountTimes(countTimes);

        listDTOS.add(monthByYearListDTO);
        return listDTOS;
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

    @Override
    public List<ManuallyEnterGrades> findAll(String openid, String examName) {
        List<ManuallyEnterGrades> allByWechatOpenidAndExamName = manuallyEnterGradesDao.findAllByWechatOpenidAndExamName(openid, examName);
        if (allByWechatOpenidAndExamName == null || allByWechatOpenidAndExamName.size() == 0){
            info = "您未录入数据";
            logger.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE,info);
        }
        return allByWechatOpenidAndExamName;
    }

    @Override
    public StudentInfo verifyStudentCode(String openid, String studentId) {
        StudentInfo studentInfoByStudentNumber = studentInfoDao.getStudentInfoByStudentNumber(studentId);
        if (studentInfoByStudentNumber == null){
            info = "暂无学校提供数据";
            logger.warn("【您暂无和学校合作】,{}",openid);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE,info);
        }

        return studentInfoByStudentNumber;
    }

    @Override
    public List<LocationComparisonDTO> getGapValue(String openid, String stuNumber, String examName) {
        ExamCoversionTotal examCoversionTotal = examCoversionTotalDao.findByStudentNumberAndExamType(stuNumber, examName);
        if (examCoversionTotal == null){
            info = "查询此学生的所有信息失败";
            logger.error(info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        String schoolName = examCoversionTotal.getSchoolName();
        String gradeName = examCoversionTotal.getGradeName();
        //年级总分降序数组，用数组下标获取年级排名
        List<String> gradeRankList = examCoversionTotalDao.findAllBySchoolNameAndGradeNameAndExamType(schoolName, gradeName, examName);
        if (gradeRankList == null){
            logger.error("【暂无此】'{}',【总的年级排名信息】; 【此用户：】{}",schoolName,openid);
            info = "【暂无此学校总的年级排名信息】";
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        Map<String, Map<String,String>> map = new HashMap<>();
        //Map<String, String> gradeMap = new LinkedHashMap<>();//排名map
        Map<String, String> scoreMap = new LinkedHashMap<>();//分数map
        Map<String, String> gapValueMap = new LinkedHashMap<>();//差值map


        Integer schoolIndex = examCoversionTotal.getSchoolIndex();//获取此用户总分的年级排名
        Double total = examCoversionTotal.getCoversionTotal();//获取此用户总分
        //第一名
        String firstOneValue = String.valueOf(gradeRankList.get(0));//获取年级第一名的总分分数
        String firstOneGap = String.valueOf(total - Double.parseDouble(firstOneValue) );//与第一名的差值
        //第50名，这里调成动态的，由前端传要看第几名
        String secondValue =  String.valueOf(gradeRankList.get(49));//获取的分数，动态的就是 i - 1
        String secondGap = String.valueOf(total - Double.parseDouble(secondValue));//与第二位（可能是50名）的差值
        //第100名，这里调成动态的，由前端传要看第几名
        String thirdValue =  String.valueOf(gradeRankList.get(99));//获取的分数，动态的就是 j - 1
        String thirdGap = String.valueOf(total - Double.parseDouble(thirdValue));//与第三位（可能是100名）的差值
        //第150名，这里调成动态的，由前端传要看第几名
        String fourValue =  String.valueOf(gradeRankList.get(149));//获取的分数，动态的就是 z - 1
        String fourGap = String.valueOf(total - Double.parseDouble(fourValue));//与第四位（可能是150名）的差值

        scoreMap.put("I", String.valueOf(total));//自己的分数
        scoreMap.put("150",fourValue);//第150的分数，可以为动态: z
        scoreMap.put("100",thirdValue);//第100的分数，可以为动态: j
        scoreMap.put("50",secondValue);//第50的分数，可以为动态: i
        scoreMap.put("1",firstOneValue);//第1的分数，这个就固定下来

        map.put("scoreMap", scoreMap);

        gapValueMap.put("I", "--");
        gapValueMap.put("150",fourGap);
        gapValueMap.put("100",thirdGap);
        gapValueMap.put("50",secondGap);
        gapValueMap.put("1",firstOneGap);

        map.put("gapValueMap",gapValueMap);

        List<LocationComparisonDTO> list = new ArrayList<>();
        LocationComparisonDTO locationComparisonDTO = new LocationComparisonDTO();
        locationComparisonDTO.setStringMap(map);
        list.add(locationComparisonDTO);

        return list;
    }
}
