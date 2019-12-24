package com.zgczx.service.scoretwo.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.zgczx.mapper.ManuallyEnterGradesMapper;
import com.zgczx.repository.mysql1.score.dao.GoalSetDao;
import com.zgczx.repository.mysql1.score.dto.ManuallyEnterGradesDTO;
import com.zgczx.repository.mysql1.score.dto.MonthByYearListDTO;
import com.zgczx.repository.mysql1.score.model.GoalSet;
import com.zgczx.repository.mysql1.score.model.ManuallyEnterGrades;
import com.zgczx.enums.ResultEnum;
import com.zgczx.exception.ScoreException;
import com.zgczx.repository.mysql1.score.dao.ManuallyEnterGradesDao;
import com.zgczx.repository.mysql1.user.dao.StudentInfoDao;
import com.zgczx.repository.mysql1.user.model.StudentInfo;
import com.zgczx.repository.mysql2.scoretwo.dao.ExamCoversionTotalDao;
import com.zgczx.repository.mysql2.scoretwo.dto.CommentValueDTO;
import com.zgczx.repository.mysql2.scoretwo.dto.LocationComparisonDTO;
import com.zgczx.repository.mysql2.scoretwo.dto.SingleContrastInfoDTO;
import com.zgczx.repository.mysql2.scoretwo.dto.TotalScoreInfoDTO;
import com.zgczx.repository.mysql2.scoretwo.model.ExamCoversionTotal;
import com.zgczx.service.scoretwo.ScoreTwoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private GoalSetDao goalSetDao;

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

        for (ManuallyEnterGrades model : list){
            String studentNumber = model.getStudentNumber();
            String subjectName = model.getSubjectName();
            String examName = model.getExamName();
            ManuallyEnterGrades manuallyEnterGrades = manuallyEnterGradesDao.findAllByStudentNumberAndExamNameAndSubjectName(studentNumber, examName, subjectName);

            if (manuallyEnterGrades != null){
                manuallyEnterGradesDao.delete(manuallyEnterGrades);//将之前录入过的成绩，删除
//                info = "您已经录过此数据，暂不允许重复录入，请重新核对再录入";
//                logger.error("重复数据为={} ", manuallyEnterGrades);
//                throw new ScoreException(ResultEnum.DATA_ALREADY_EXISTED,info);
            }
        }
        logger.info("【打印传参的list】: {}",list);

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
    public List<ManuallyEnterGradesDTO> findAll(String openid, String examName) {
        List<ManuallyEnterGrades> allByWechatOpenidAndExamName = manuallyEnterGradesDao.findAllByWechatOpenidAndExamName(openid, examName);
        if (allByWechatOpenidAndExamName == null || allByWechatOpenidAndExamName.size() == 0){
            info = "您未录入数据";
            logger.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE,info);
        }

        // 这种才是真正的list，之前都放到map中是不太对的，这样的话才真正用了DTO的返回结构
        List<ManuallyEnterGradesDTO> gradesDTOList = new ArrayList<>();
        for (ManuallyEnterGrades manuallyEnterGrades : allByWechatOpenidAndExamName){

            ManuallyEnterGradesDTO manuallyEnterGradesDTO = new ManuallyEnterGradesDTO();
            manuallyEnterGradesDTO.setManuallyEnterGrades(manuallyEnterGrades);
            List<String> stringList = new ArrayList<>();
            String imgurl = manuallyEnterGrades.getImgs();
            String[] split = null;
            if (imgurl != null){
                split = imgurl.split(",");

                for (String s : split){
                    stringList.add(s);
                }
            }
            manuallyEnterGradesDTO.setImgurllist(stringList);
            gradesDTOList.add(manuallyEnterGradesDTO);
        }


     /*   List<String> stringList = new ArrayList<>();
        String imgurl = allByWechatOpenidAndExamName.get(0).getImgurl();
        String[] split = null;
        if (imgurl != null){
            split = imgurl.split(",");

            for (String s : split){
                stringList.add(s);
            }
        }

        List<ManuallyEnterGradesDTO> list = new ArrayList<>();
        ManuallyEnterGradesDTO manuallyEnterGradesDTO = new ManuallyEnterGradesDTO();
        manuallyEnterGradesDTO.setManuallyEnterGrades(allByWechatOpenidAndExamName.get(0));
        manuallyEnterGradesDTO.setImgurllist(stringList);

        list.add(manuallyEnterGradesDTO);*/
        return gradesDTOList;
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

    @Override
    public List<CommentValueDTO> getCommentValue(String openid, String stuNumber, String examName) {
        ExamCoversionTotal examCoversionTotal = examCoversionTotalDao.findByStudentNumberAndExamType(stuNumber, examName);
        if (examCoversionTotal == null){
            info = "查询此学生的所有信息失败";
            logger.error(info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        String schoolName = examCoversionTotal.getSchoolName();
        String gradeName = examCoversionTotal.getGradeName();
        String classid = examCoversionTotal.getClassId();
        List<String[]> classTotalRank = examCoversionTotalDao.findClassTotalByClassIdAndExamType(classid, examName, schoolName, gradeName);
        if (classTotalRank == null){
            info = "暂无此班级排名信息";
            logger.error(info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        //保留两位小数
        DecimalFormat df = new DecimalFormat("#0.00");
        // 班级最高分
        String classRankFirst = String.valueOf(classTotalRank.get(0));//班级第一名分数
        //班级总人数
        int classCount = examCoversionTotalDao.countByClassIdAndExamTypeAndValidAndSchoolNameAndGradeName(classid, examName, 1, schoolName, gradeName);
       //班级总分累加和
        float classTotalSum = examCoversionTotalDao.sumCoversionTotalByClassIdAndExamTypeAndValidAndSchoolName(classid, examName, 1, schoolName, gradeName);
        //班级平均分, 保留两位小数
        String classAvg = String.valueOf(classTotalSum / classCount);

        //年级平均分
        String gradeAvg = examCoversionTotalDao.totalAverageByExamType(examName, schoolName, gradeName);
        //总分的年级排名
        List<String> gradeTotalRank = examCoversionTotalDao.findByTotalScore(examName, schoolName, gradeName);
        //年级最高分
        String gradeRankFirst = String.valueOf(gradeTotalRank.get(0));

        //封装DTO
        List<CommentValueDTO> list = new ArrayList<>();
        CommentValueDTO commentValueDTO = new CommentValueDTO();
        commentValueDTO.setClassHighScore(classRankFirst);
        commentValueDTO.setClassAvgScore(String.format("%.2f",Float.parseFloat(classAvg)));
        commentValueDTO.setGradeHighScore(gradeRankFirst);
        commentValueDTO.setGradeAvgScore(String.format("%.2f",Float.parseFloat(gradeAvg)));
        commentValueDTO.setTotalScore(String.valueOf(examCoversionTotal.getCoversionTotal()));

        list.add(commentValueDTO);
        return list;
    }

    @Override
    public List<TotalScoreInfoDTO> getTotalScoreInfo(String openid, String stuNumber, String examName, String targetRank) {
        ExamCoversionTotal examCoversionTotal = examCoversionTotalDao.findByStudentNumberAndExamType(stuNumber, examName);
        if (examCoversionTotal == null){
            info = "查询此学生的所有信息失败";
            logger.error(info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        String schoolName = examCoversionTotal.getSchoolName();
        String gradeName = examCoversionTotal.getGradeName();
        Double myTotalScore = examCoversionTotal.getCoversionTotal();//自己总分值
        // 本次考试的年级总人数
        int gradeNumber = examCoversionTotalDao.countByExamTypeAndValidAndSchoolNameAndGradeName(examName, 1, schoolName, gradeName);
        if (Integer.valueOf(targetRank) > gradeNumber){
            info = "您设定的目标值大于总人数，请核对后再设定";
            logger.error(info);
            throw new ScoreException(ResultEnum.DATA_OUT_RANGE, info);
        }
        //年级排名数组
        List<String> gradeRankList = examCoversionTotalDao.findAllBySchoolNameAndGradeNameAndExamType(schoolName, gradeName, examName);
        //我的排名
        int myRank = gradeRankList.indexOf(myTotalScore) + 1;
        //可能有并列，但是并列也是自己的排名
        if (targetRank.equals(myRank)){
            info = "您设定的目标值为您自己的排名，请重新设定";
            logger.error(info);
            throw new ScoreException(ResultEnum.DATA_IS_WRONG, info);
        }
        // 目标排名要从 list中获取分数值 时的值
        int target = Integer.valueOf(targetRank) - 1;
        // 目标分数
        String targetScore = String.valueOf(gradeRankList.get(target));
        // 差值：我的分数 - 目标分数
        String scoreDifferentValue = String.valueOf(myTotalScore - Double.parseDouble(targetScore));

        List<TotalScoreInfoDTO> list = new ArrayList<>();
        TotalScoreInfoDTO totalScoreInfoDTO = new TotalScoreInfoDTO();
        totalScoreInfoDTO.setMyRank(myRank);
        totalScoreInfoDTO.setMyScore(String.valueOf(myTotalScore));
        totalScoreInfoDTO.setTargetRank(Integer.parseInt(targetRank));
        totalScoreInfoDTO.setTargetScore(targetScore);
        totalScoreInfoDTO.setScoreDifferentValue(scoreDifferentValue);
        list.add(totalScoreInfoDTO);

        return list;
    }

    @Override
    public List<String> getSubjectCollection(String openid, String stuNumber, String examName) {
        ExamCoversionTotal examCoversionTotal = examCoversionTotalDao.findByStudentNumberAndExamType(stuNumber, examName);
        if (examCoversionTotal == null){
            info = "查询此学生的所有信息失败";
            logger.error(info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }

        List<String> list = new LinkedList<>();
        list.add("语文");
        list.add("数学");
        list.add("英语");

        if (!examCoversionTotal.getWuliCoversion().toString().equals("0.0")){
            list.add("物理");
        }
        if (!examCoversionTotal.getHuaxueCoversion().toString().equals("0.0")){
            list.add("化学");
        }
        if (!examCoversionTotal.getShengwuCoversion().toString().equals("0.0")){
            list.add("生物");
        }
        if (!examCoversionTotal.getZhengzhiCoversion().toString().equals("0.0")){
            list.add("政治");
        }
        if (!examCoversionTotal.getLishiCoversion().toString().equals("0.0") ){
            list.add("历史");
        }
        if (!examCoversionTotal.getDiliCoversion().toString().equals("0.0")){
            list.add("地理");
        }

//        List<List<String>> list1 = new ArrayList<>();
//        SubjectCollectionDTO subjectCollectionDTO = new SubjectCollectionDTO();
//        subjectCollectionDTO.setList(list);
//        list1.add(list);

        return list;
    }

    @Override
    @Transactional
    public List<SingleContrastInfoDTO> getSingleContrastInfo(Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject();
        String stuNumber = null;
        if (!map.containsKey("stuNumber")) {
            jsonObject.put("errno", ResultEnum.RESULE_DATA_NONE);
            jsonObject.put("errmsg", "stuNumber not exit!");
           // return jsonObject;
        } else {
            stuNumber =  map.get("stuNumber").toString().trim();
        }
        String examName = null;
        if (!map.containsKey("examName")) {
            jsonObject.put("errno", ResultEnum.RESULE_DATA_NONE);
            jsonObject.put("errmsg", "examName not exit!");
           // return jsonObject;
        } else {
            examName =  map.get("examName").toString().trim();
        }
        String openid = null;// 用户openid
        if (!map.containsKey("openid")) {
            jsonObject.put("errno", ResultEnum.RESULE_DATA_NONE);
            jsonObject.put("errmsg", "openid not exit!");
            // return jsonObject;
        } else {
            openid =  map.get("openid").toString().trim();
        }

        // 创建目标设定表的实体,往里面存放设定的目标值
        GoalSet goalSet = new GoalSet();
        goalSet.setStudentNumber(stuNumber);
        goalSet.setExamName(examName);
        goalSet.setOpenid(openid);

        //LinkedHashMap将map中的顺序按照添加顺序排列
        Map<String, Map<String, String>> hashMap = new LinkedHashMap<>();

        Map<String, String> totalMap = new HashMap<>();//总分map
        //定义九门课的map
        Map<String, String> yuwenMap = new HashMap<>();
        Map<String, String> shuxueMap = new HashMap<>();
        Map<String, String> yingyuMap = new HashMap<>();
        Map<String, String> wuliMap = new HashMap<>();
        Map<String, String> huaxueMap = new HashMap<>();
        Map<String, String> shengwuMap = new HashMap<>();
        Map<String, String> diliMap = new HashMap<>();
        Map<String, String> lishiMap = new HashMap<>();
        Map<String, String> zhengzhiMap = new HashMap<>();
        ExamCoversionTotal examCoversionTotal = examCoversionTotalDao.findByStudentNumberAndExamType(stuNumber, examName);
        if (examCoversionTotal == null){
            info = "查询此学生的所有信息失败";
            logger.error(info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        String schoolName = examCoversionTotal.getSchoolName();
        String gradeName = examCoversionTotal.getGradeName();
        //语文分数
        Double yuwenScore = examCoversionTotal.getYuwenScore();
        //语文年级排名
        List<String> yuwenRankList = examCoversionTotalDao.findByYuwenScoreAndSchoolNameAndValid(examName, schoolName, 1, gradeName);
        //语文年级人数
        int yuwenNum = yuwenRankList.size();
        String yuwenTargeRank = map.get("yuwen").toString().trim();
        goalSet.setYuwen(yuwenTargeRank);// 语文目标名次
        if (Integer.valueOf(yuwenTargeRank) > yuwenNum){
            info = "您语文设定的目标值大于总人数，请核对后再设定";
            logger.error(info);
            throw new ScoreException(ResultEnum.DATA_OUT_RANGE, info);
        }
        //我的排名
        int myyuwenRank = yuwenRankList.indexOf(Float.valueOf(yuwenScore.toString())) + 1;
        //可能有并列，但是并列也是自己的排名
        if (yuwenTargeRank.equals(myyuwenRank)){
            info = "您设定的语文目标值为您自己的排名，请重新设定";
            logger.error(info);
            throw new ScoreException(ResultEnum.DATA_IS_WRONG, info);
        }
        // 目标排名要从 list中获取分数值 时的值
        int yuwentarget = Integer.valueOf(yuwenTargeRank) - 1;
        // 目标分数
        String yuwenTargetScore = String.valueOf(yuwenRankList.get(yuwentarget));
        // 差值：我的分数 - 目标分数
        String yuwenScoreDifferentValue = String.valueOf(yuwenScore - Double.parseDouble(yuwenTargetScore));

        yuwenMap.put("myRank", String.valueOf(myyuwenRank));
        yuwenMap.put("targetRank",yuwenTargeRank);
        yuwenMap.put("myScore", String.valueOf(yuwenScore));
        yuwenMap.put("targetScore",yuwenTargetScore);
        yuwenMap.put("scoreDifferentValue",yuwenScoreDifferentValue);
        yuwenMap.put("title", "语文");
        hashMap.put("yuwen",yuwenMap);
        //数学分数
        Double shuxueScore = examCoversionTotal.getShuxueScore();
        //数学年级排名
        List<String> shuxueRankList = examCoversionTotalDao.findByShuxueScoreAndSchoolNameAndValid(examName, schoolName, 1, gradeName);
        //数学年级人数
        int shuxueNum = shuxueRankList.size();
        String shuxueTargeRank = map.get("shuxue").toString().trim();
        goalSet.setShuxue(shuxueTargeRank);//数学目标名次
        if (Integer.valueOf(shuxueTargeRank) > shuxueNum){
            info = "您数学设定的目标值大于总人数，请核对后再设定";
            logger.error(info);
            throw new ScoreException(ResultEnum.DATA_OUT_RANGE, info);
        }
        //我的排名
        int myshuxueRank = shuxueRankList.indexOf(Float.valueOf(shuxueScore.toString())) + 1;
        //可能有并列，但是并列也是自己的排名
        if (shuxueTargeRank.equals(myshuxueRank)){
            info = "您设定的数学目标值为您自己的排名，请重新设定";
            logger.error(info);
            throw new ScoreException(ResultEnum.DATA_IS_WRONG, info);
        }
        // 目标排名要从 list中获取分数值 时的值
        int shuxuetarget = Integer.valueOf(shuxueTargeRank) - 1;
        // 目标分数
        String shuxueTargetScore = String.valueOf(shuxueRankList.get(shuxuetarget));
        // 差值：我的分数 - 目标分数
        String shuxueScoreDifferentValue = String.valueOf(shuxueScore - Double.parseDouble(shuxueTargetScore));
        shuxueMap.put("myRank", String.valueOf(myshuxueRank));
        shuxueMap.put("targetRank",shuxueTargeRank);
        shuxueMap.put("myScore", String.valueOf(shuxueScore));
        shuxueMap.put("targetScore",shuxueTargetScore);
        shuxueMap.put("scoreDifferentValue",shuxueScoreDifferentValue);
        shuxueMap.put("title", "数学");
        hashMap.put("shuxue",shuxueMap);

        //英语分数
        Double yingyuScore = examCoversionTotal.getYingyuScore();
        //英语年级排名
        List<String> yingyuRankList = examCoversionTotalDao.findByYingyuScoreAndSchoolNameAndValid(examName, schoolName, 1, gradeName);
        //英语年级人数
        int yingyuNum = yingyuRankList.size();
        String yingyuTargeRank = map.get("yingyu").toString().trim();
        goalSet.setYingyu(yingyuTargeRank);//英语目标名次
        if (Integer.valueOf(yingyuTargeRank) > yingyuNum){
            info = "您英语设定的目标值大于总人数，请核对后再设定";
            logger.error(info);
            throw new ScoreException(ResultEnum.DATA_OUT_RANGE, info);
        }
        //我的排名
        int myyingyuRank = yingyuRankList.indexOf(Float.valueOf(yingyuScore.toString())) + 1;
        //可能有并列，但是并列也是自己的排名
        if (yingyuTargeRank.equals(myyingyuRank)){
            info = "您设定的英语目标值为您自己的排名，请重新设定";
            logger.error(info);
            throw new ScoreException(ResultEnum.DATA_IS_WRONG, info);
        }
        // 目标排名要从 list中获取分数值 时的值
        int yingyutarget = Integer.valueOf(yingyuTargeRank) - 1;
        // 目标分数
        String yingyuTargetScore = String.valueOf(yingyuRankList.get(yingyutarget));
        // 差值：我的分数 - 目标分数
        String yingyuScoreDifferentValue = String.valueOf(yingyuScore - Double.parseDouble(yingyuTargetScore));
        yingyuMap.put("myRank", String.valueOf(myyingyuRank));
        yingyuMap.put("targetRank",yingyuTargeRank);
        yingyuMap.put("myScore", String.valueOf(yingyuScore));
        yingyuMap.put("targetScore",yingyuTargetScore);
        yingyuMap.put("scoreDifferentValue",yingyuScoreDifferentValue);
        yingyuMap.put("title", "英语");
        hashMap.put("yingyu",yingyuMap);

        if (!examCoversionTotal.getWuliCoversion().toString().equals("0.0")){
            //物理分数
            Double wuliScore = examCoversionTotal.getWuliCoversion();
            //物理年级排名
            List<String> wuliRankList = examCoversionTotalDao.findByWuliCoversionAndSchoolNameAndValid(examName, schoolName, 1, gradeName);
            //物理年级人数
            int wuliNum = wuliRankList.size();
            String wuliTargeRank = map.get("wuli").toString().trim();
            goalSet.setWuli(wuliTargeRank);//物理目标名次
            if (Integer.valueOf(wuliTargeRank) > wuliNum){
                info = "您物理设定的目标值大于总人数，请核对后再设定";
                logger.error(info);
                throw new ScoreException(ResultEnum.DATA_OUT_RANGE, info);
            }
            //我的排名
            int mywuliRank = wuliRankList.indexOf(Float.valueOf(wuliScore.toString())) + 1;
            //可能有并列，但是并列也是自己的排名
            if (wuliTargeRank.equals(mywuliRank)){
                info = "您设定的物理目标值为您自己的排名，请重新设定";
                logger.error(info);
                throw new ScoreException(ResultEnum.DATA_IS_WRONG, info);
            }
            // 目标排名要从 list中获取分数值 时的值
            int wulitarget = Integer.valueOf(wuliTargeRank) - 1;
            // 目标分数
            String wuliTargetScore = String.valueOf(wuliRankList.get(wulitarget));
            // 差值：我的分数 - 目标分数
            String wuliScoreDifferentValue = String.valueOf(wuliScore - Double.parseDouble(wuliTargetScore));
            wuliMap.put("myRank", String.valueOf(mywuliRank));
            wuliMap.put("targetRank",wuliTargeRank);
            wuliMap.put("myScore", String.valueOf(wuliScore));
            wuliMap.put("targetScore",wuliTargetScore);
            wuliMap.put("scoreDifferentValue",wuliScoreDifferentValue);
            wuliMap.put("title", "物理");
            hashMap.put("wuli",wuliMap);
        }

        if (!examCoversionTotal.getHuaxueCoversion().toString().equals("0.0")){
            //化学分数
            Double huaxueScore = examCoversionTotal.getHuaxueCoversion();
            //化学年级排名
            List<String> huaxueRankList = examCoversionTotalDao.findByHuaxueCoversionAndSchoolNameAndValid(examName, schoolName, 1, gradeName);
            //化学年级人数
            int huaxueNum = huaxueRankList.size();
            String huaxueTargeRank = map.get("huaxue").toString().trim();
            goalSet.setHuaxue(huaxueTargeRank);//化学目标名次
            if (Integer.valueOf(huaxueTargeRank) > huaxueNum){
                info = "您化学设定的目标值大于总人数，请核对后再设定";
                logger.error(info);
                throw new ScoreException(ResultEnum.DATA_OUT_RANGE, info);
            }
            //我的排名
            int myhuaxueRank = huaxueRankList.indexOf(Float.valueOf(huaxueScore.toString())) + 1;
            //可能有并列，但是并列也是自己的排名
            if (huaxueTargeRank.equals(myhuaxueRank)){
                info = "您设定的化学目标值为您自己的排名，请重新设定";
                logger.error(info);
                throw new ScoreException(ResultEnum.DATA_IS_WRONG, info);
            }
            // 目标排名要从 list中获取分数值 时的值
            int huaxuetarget = Integer.valueOf(huaxueTargeRank) - 1;
            // 目标分数
            String huaxueTargetScore = String.valueOf(huaxueRankList.get(huaxuetarget));
            // 差值：我的分数 - 目标分数
            String huaxueScoreDifferentValue = String.valueOf(huaxueScore - Double.parseDouble(huaxueTargetScore));
            huaxueMap.put("myRank", String.valueOf(myhuaxueRank));
            huaxueMap.put("targetRank",huaxueTargeRank);
            huaxueMap.put("myScore", String.valueOf(huaxueScore));
            huaxueMap.put("targetScore",huaxueTargetScore);
            huaxueMap.put("scoreDifferentValue",huaxueScoreDifferentValue);
            huaxueMap.put("title", "化学");
            hashMap.put("huaxue",huaxueMap);
        }
        if (!examCoversionTotal.getShengwuCoversion().toString().equals("0.0")){
            //生物分数
            Double shengwuScore = examCoversionTotal.getShengwuCoversion();
            //生物年级排名
            List<String> shengwuRankList = examCoversionTotalDao.findByShengwuCoversionAndSchoolNameAndValid(examName, schoolName, 1, gradeName);
            //生物年级人数
            int shengwuNum = shengwuRankList.size();
            String shengwuTargeRank = map.get("shengwu").toString().trim();
            goalSet.setShengwu(shengwuTargeRank);//生物目标名次
            if (Integer.valueOf(shengwuTargeRank) > shengwuNum){
                info = "您生物设定的目标值大于总人数，请核对后再设定";
                logger.error(info);
                throw new ScoreException(ResultEnum.DATA_OUT_RANGE, info);
            }
            //我的排名
            int myshengwuRank = shengwuRankList.indexOf(Float.valueOf(shengwuScore.toString())) + 1;
            //可能有并列，但是并列也是自己的排名
            if (shengwuTargeRank.equals(myshengwuRank)){
                info = "您设定的生物目标值为您自己的排名，请重新设定";
                logger.error(info);
                throw new ScoreException(ResultEnum.DATA_IS_WRONG, info);
            }
            // 目标排名要从 list中获取分数值 时的值
            int shengwutarget = Integer.valueOf(shengwuTargeRank) - 1;
            // 目标分数
            String shengwuTargetScore = String.valueOf(shengwuRankList.get(shengwutarget));
            // 差值：我的分数 - 目标分数
            String shengwuScoreDifferentValue = String.valueOf(shengwuScore - Double.parseDouble(shengwuTargetScore));
            shengwuMap.put("myRank", String.valueOf(myshengwuRank));
            shengwuMap.put("targetRank",shengwuTargeRank);
            shengwuMap.put("myScore", String.valueOf(shengwuScore));
            shengwuMap.put("targetScore",shengwuTargetScore);
            shengwuMap.put("scoreDifferentValue",shengwuScoreDifferentValue);
            shengwuMap.put("title", "生物");
            hashMap.put("shengwu",shengwuMap);
        }
        if (!examCoversionTotal.getLishiCoversion().toString().equals("0.0") ){
            //历史分数
            Double lishiScore = examCoversionTotal.getLishiCoversion();
            //历史年级排名
            List<String> lishiRankList = examCoversionTotalDao.findByLishiCoversionAndSchoolNameAndValid(examName, schoolName, 1, gradeName);
            //历史年级人数
            int lishiNum = lishiRankList.size();
            String lishiTargeRank = map.get("lishi").toString().trim();
            goalSet.setLishi(lishiTargeRank);//历史目标名次
            if (Integer.valueOf(lishiTargeRank) > lishiNum){
                info = "您历史设定的目标值大于总人数，请核对后再设定";
                logger.error(info);
                throw new ScoreException(ResultEnum.DATA_OUT_RANGE, info);
            }
            //我的排名
            int mylishiRank = lishiRankList.indexOf(Float.valueOf(lishiScore.toString())) + 1;
            //可能有并列，但是并列也是自己的排名
            if (lishiTargeRank.equals(mylishiRank)){
                info = "您设定的历史目标值为您自己的排名，请重新设定";
                logger.error(info);
                throw new ScoreException(ResultEnum.DATA_IS_WRONG, info);
            }
            // 目标排名要从 list中获取分数值 时的值
            int lishitarget = Integer.valueOf(lishiTargeRank) - 1;
            // 目标分数
            String lishiTargetScore = String.valueOf(lishiRankList.get(lishitarget));
            // 差值：我的分数 - 目标分数
            String lishiScoreDifferentValue = String.valueOf(lishiScore - Double.parseDouble(lishiTargetScore));
            lishiMap.put("myRank", String.valueOf(mylishiRank));
            lishiMap.put("targetRank",lishiTargeRank);
            lishiMap.put("myScore", String.valueOf(lishiScore));
            lishiMap.put("targetScore",lishiTargetScore);
            lishiMap.put("scoreDifferentValue",lishiScoreDifferentValue);
            lishiMap.put("title", "历史");
            hashMap.put("lishi",lishiMap);
        }
        if (!examCoversionTotal.getDiliCoversion().toString().equals("0.0")){
            //地理分数
            Double diliScore = examCoversionTotal.getDiliCoversion();
            //地理年级排名
            List<String> diliRankList = examCoversionTotalDao.findByDiliCoversionAndSchoolNameAndValid(examName, schoolName, 1, gradeName);
            //地理年级人数
            int diliNum = diliRankList.size();
            String diliTargeRank = map.get("dili").toString().trim();
            goalSet.setDili(diliTargeRank);//地理目标名次
            if (Integer.valueOf(diliTargeRank) > diliNum){
                info = "您地理设定的目标值大于总人数，请核对后再设定";
                logger.error(info);
                throw new ScoreException(ResultEnum.DATA_OUT_RANGE, info);
            }
            //我的排名
            int mydiliRank = diliRankList.indexOf(Float.valueOf(diliScore.toString())) + 1;
            //可能有并列，但是并列也是自己的排名
            if (diliTargeRank.equals(mydiliRank)){
                info = "您设定的地理目标值为您自己的排名，请重新设定";
                logger.error(info);
                throw new ScoreException(ResultEnum.DATA_IS_WRONG, info);
            }
            // 目标排名要从 list中获取分数值 时的值
            int dilitarget = Integer.valueOf(diliTargeRank) - 1;
            // 目标分数
            String diliTargetScore = String.valueOf(diliRankList.get(dilitarget));
            // 差值：我的分数 - 目标分数
            String diliScoreDifferentValue = String.valueOf(diliScore - Double.parseDouble(diliTargetScore));
            diliMap.put("myRank", String.valueOf(mydiliRank));
            diliMap.put("targetRank",diliTargeRank);
            diliMap.put("myScore", String.valueOf(diliScore));
            diliMap.put("targetScore",diliTargetScore);
            diliMap.put("scoreDifferentValue",diliScoreDifferentValue);
            diliMap.put("title", "地理");
            hashMap.put("dili",diliMap);
        }
        if (!examCoversionTotal.getZhengzhiCoversion().toString().equals("0.0")){
            //政治分数
            Double zhengzhiScore = examCoversionTotal.getZhengzhiCoversion();
            //政治年级排名
            List<String> zhengzhiRankList = examCoversionTotalDao.findByZhengzhiCoversionAndSchoolNameAndValid(examName, schoolName, 1, gradeName);
            //政治年级人数
            int zhengzhiNum = zhengzhiRankList.size();
            String zhengzhiTargeRank = map.get("zhengzhi").toString().trim();
            goalSet.setZhengzhi(zhengzhiTargeRank);//政治目标名次
            if (Integer.valueOf(zhengzhiTargeRank) > zhengzhiNum){
                info = "您政治设定的目标值大于总人数，请核对后再设定";
                logger.error(info);
                throw new ScoreException(ResultEnum.DATA_OUT_RANGE, info);
            }
            //我的排名
            int myzhengzhiRank = zhengzhiRankList.indexOf(Float.valueOf(zhengzhiScore.toString())) + 1;
            //可能有并列，但是并列也是自己的排名
            if (zhengzhiTargeRank.equals(myzhengzhiRank)){
                info = "您设定的政治目标值为您自己的排名，请重新设定";
                logger.error(info);
                throw new ScoreException(ResultEnum.DATA_IS_WRONG, info);
            }
            // 目标排名要从 list中获取分数值 时的值
            int zhengzhitarget = Integer.valueOf(zhengzhiTargeRank) - 1;
            // 目标分数
            String zhengzhiTargetScore = String.valueOf(zhengzhiRankList.get(zhengzhitarget));
            // 差值：我的分数 - 目标分数
            String zhengzhiScoreDifferentValue = String.valueOf(zhengzhiScore - Double.parseDouble(zhengzhiTargetScore));
            zhengzhiMap.put("myRank", String.valueOf(myzhengzhiRank));
            zhengzhiMap.put("targetRank",zhengzhiTargeRank);
            zhengzhiMap.put("myScore", String.valueOf(zhengzhiScore));
            zhengzhiMap.put("targetScore",zhengzhiTargetScore);
            zhengzhiMap.put("scoreDifferentValue",zhengzhiScoreDifferentValue);
            zhengzhiMap.put("title", "政治");
            hashMap.put("zhengzhi",zhengzhiMap);
        }

        Double myTotalScore = examCoversionTotal.getCoversionTotal();//自己总分值
        String targetRank = map.get("total").toString().trim();//总分目标设定
        goalSet.setTotalScore(targetRank);//总分目标名次
        // 本次考试的年级总人数
        int gradeNumber = examCoversionTotalDao.countByExamTypeAndValidAndSchoolNameAndGradeName(examName, 1, schoolName, gradeName);
        if (Integer.valueOf(targetRank) > gradeNumber){
            info = "您设定的目标值大于总人数，请核对后再设定";
            logger.error(info);
            throw new ScoreException(ResultEnum.DATA_OUT_RANGE, info);
        }
        //年级排名数组
        List<String> gradeRankList = examCoversionTotalDao.findAllBySchoolNameAndGradeNameAndExamType(schoolName, gradeName, examName);
        //我的排名
        int myRank = gradeRankList.indexOf(Float.valueOf(myTotalScore.toString())) + 1;
        //可能有并列，但是并列也是自己的排名
        if (targetRank.equals(myRank)){
            info = "您设定的目标值为您自己的排名，请重新设定";
            logger.error(info);
            throw new ScoreException(ResultEnum.DATA_IS_WRONG, info);
        }
        // 目标排名要从 list中获取分数值 时的值
        int target = Integer.valueOf(targetRank) - 1;
        // 目标分数
        String targetScore = String.valueOf(gradeRankList.get(target));
        // 差值：我的分数 - 目标分数
        String scoreDifferentValue = String.valueOf(myTotalScore - Double.parseDouble(targetScore));
        totalMap.put("myRank", String.valueOf(myRank));
        totalMap.put("targetRank",targetRank);
        totalMap.put("myScore", String.valueOf(myTotalScore));
        totalMap.put("targetScore",targetScore);
        totalMap.put("scoreDifferentValue",scoreDifferentValue);
        totalMap.put("title", "总分");
        hashMap.put("total",totalMap);

        //保存目标名次表数据
        GoalSet save = goalSetDao.save(goalSet);
        logger.info("【保存的各科目标数据：】,{}",save);

        logger.info("map: {}",map);
        List<SingleContrastInfoDTO> list = new ArrayList<>();
        SingleContrastInfoDTO singleContrastInfoDTO = new SingleContrastInfoDTO();
        singleContrastInfoDTO.setMap(hashMap);
        list.add(singleContrastInfoDTO);
        return list;
    }

    @Override
    public GoalSet findTargetValue(String stuNumber, String examName) {
        List<GoalSet> targetValue = goalSetDao.findTargetValue(stuNumber, examName);
        if (targetValue.size() == 0){
            info = "您为首次使用此功能，请您设定目标对比值";
            logger.error(info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }

        return targetValue.get(0);
    }

    @Transactional
    @Modifying
    @Override
    public int deleteManuallyEnter(String stuNumber, String openid, String examName,String subject) {
        ManuallyEnterGrades allByWechatOpenidAndExamName = manuallyEnterGradesDao.findAllByStudentNumberAndExamNameAndSubjectName(stuNumber, examName,subject);
        if (allByWechatOpenidAndExamName == null){
            info = "您未录入此数据";
            logger.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE,info);
        }
        // 是否还需要 记录谁 操作的，openid
        //int a = manuallyEnterGradesDao.deleteByStudentNumberAndExamNameAndSubjectName(stuNumber, examName, subject);
        return manuallyEnterGradesDao.deleteByStudentNumberAndExamNameAndSubjectName(stuNumber, examName, subject);

    }

    @Override
    public ManuallyEnterGrades updateManuallyEnter(String stuNumber, String openid, String oldexamName,String subject,ManuallyEnterGrades manuallyEnterGrades) {
       // ManuallyEnterGrades model = manuallyEnterGradesDao.findByWechatOpenidAndStudentNumberAndExamName(openid, stuNumber,oldexamName);
        ManuallyEnterGrades model = manuallyEnterGradesDao.findAllByStudentNumberAndExamNameAndSubjectName(stuNumber, oldexamName,subject);
        if (model == null ){
            info = "您未录入此数据";
            logger.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE,info);
        }

        model.setSubjectName(manuallyEnterGrades.getSubjectName());
        model.setScore(manuallyEnterGrades.getScore());
        model.setClassRank(manuallyEnterGrades.getClassRank());
        model.setGradeRank(manuallyEnterGrades.getGradeRank());
        model.setExamName(manuallyEnterGrades.getExamName());
        model.setImgs(manuallyEnterGrades.getImgs());

        Timestamp date = new Timestamp(System.currentTimeMillis());
        model.setUpdatetime(date);

        ManuallyEnterGrades save = manuallyEnterGradesDao.save(model);

        return save;
    }
}
