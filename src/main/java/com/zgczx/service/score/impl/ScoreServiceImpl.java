package com.zgczx.service.score.impl;

import com.zgczx.dataobject.score.*;
import com.zgczx.dataobject.user.SysLogin;
import com.zgczx.dto.*;
import com.zgczx.enums.ResultEnum;
import com.zgczx.exception.ScoreException;
import com.zgczx.repository.score.*;
import com.zgczx.repository.user.StudentInfoDao;
import com.zgczx.repository.user.SysLoginDao;
import com.zgczx.service.score.ScoreService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.math.BigInteger;
import java.text.DecimalFormat;
import com.zgczx.utils.DateFormatUtil;
import java.util.*;


import static com.zgczx.utils.DateUtil.getNowTime;

/**
 * @author aml
 * @date 2019/9/10 17:15
 */
@Service
public class ScoreServiceImpl implements ScoreService {

    private static final Logger logger = LoggerFactory.getLogger(ScoreServiceImpl.class);

    @Autowired
    private StudentInfoDao studentInfoDao;

    @Autowired
    private SysLoginDao sysLoginDao;

    @Autowired
    private ExamCoversionTotalDao examCoversionTotalDao;

    @Autowired
    private ExamInfoDao examInfoDao;

    @Autowired
    private ExamFullScoreSetDao examFullScoreSetDao;

    @Autowired
    private SubjectFullScoreDao subjectFullScoreDao;

    @Autowired
    private ImportConversionScoreDao importConversionScoreDao;

    @Autowired
    private SubjectDTODao subjectDTODao;

    @Autowired
    EntityManagerFactory ntityManagerFactory;

    //DateFormatUtil中的两个方法不是静态方法，只能new 对象，用对象去调用
    DateFormatUtil dateFormatUtil = new DateFormatUtil();

    private String info;

    @Override
    public ExamCoversionTotal getExamCoversionTotal(Integer userId, String examType) {
        SysLogin sysLogin = sysLoginDao.findOne(userId);
        if (null == sysLogin) {
//            info = userId + " 此参数不存在";   这个加上userId,就报错。。。。
            info = " 此参数不存在";
            logger.error(info);
            throw new ScoreException(ResultEnum.PARAM_IS_INVALID, info);
        }
        String stuNumber = sysLogin.getUsername();
        if (null == stuNumber) {
            info = " 没有此用户";
            logger.error(info);
            throw new ScoreException(ResultEnum.PARAM_EXCEPTION, info);
        }
        ExamCoversionTotal examCoversionTotal = examCoversionTotalDao.findByStudentNumberAndExamType(stuNumber,examType);
        if (examCoversionTotal == null){
            info = " 暂无本次考试的数据";
            logger.error(info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        return examCoversionTotal;
//        return examCoversionTotalDao.findByStudentNumberAndExamType(stuNumber, examType);
    }

    @Override
    public List<ExamInfo> getListExamInfols() {
        List<ExamInfo> examInfoList = examInfoDao.findAll();
        if (examInfoList == null || examInfoList.size() == 0) {
            info = "查无结果";
            logger.error("查询所有考试结果: {}",info);
            throw new ScoreException(ResultEnum.DATABASE_OP_EXCEPTION, info);
        }
//        for (ExamInfo examInfo: examInfoList){
//            try {
//                String examName = examInfo.getExamName();
//                //String s1 = dateFormatUtil.dateFormat(examName);
//                String s1 = dateFormatUtil.dateFormat(examName);
//                System.out.println(s1);
//            }catch (Exception e){
//                info = "调用DateFormatUtil类转换考试名称中的日期异常";
//                throw new ScoreException(ResultEnum.PARAM_STRING_EXCEPTION, info);
//            }
//
//        }

        return examInfoList;
    }

    @Override
    public List<ExamCoversionTotalDTO> getExamCoversionTotalInfo(String stuNumber, String examType) {
        ExamCoversionTotal examCoversionTotal = examCoversionTotalDao.findByStudentNumberAndExamType(stuNumber, examType);
        if (null == examCoversionTotal) {
            info = "查询此学生的所有信息失败";
            logger.error(info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        List<ExamInfo> examInfoList = examInfoDao.findAll();
        if (examInfoList == null || examInfoList.size() == 0) {
            info = "查无结果";
            logger.error(info);
            throw new ScoreException(ResultEnum.DATABASE_OP_EXCEPTION, info);
        }
        String oldExamType = null;
        for (int i = 0; i < examInfoList.size(); i++) {
            if (examType.equals(examInfoList.get(0).getExamName())){
                info = "本次为首次考试，暂无排名波动情况";
                // 年级波动名次，进退名次
                int waveGrade = 0;
                // 班级波动名称，进退名次
                int waveClass =0;

                List<ExamCoversionTotalDTO> examCoversionTotalDTOS = new ArrayList<>();

                ExamCoversionTotalDTO examCoversionTotalDTO = new ExamCoversionTotalDTO();
                examCoversionTotalDTO.setExamCoversionTotal(examCoversionTotal);
                /* 将年级进退名次放入到封装对象中*/
                examCoversionTotalDTO.setWaveGrade(waveGrade);
                /* 将班级进退名次放入到封装对象中*/
                examCoversionTotalDTO.setWaveClass(waveClass);
                examCoversionTotalDTOS.add(examCoversionTotalDTO);
                return examCoversionTotalDTOS;
            } else  if (examType.equals(examInfoList.get(i).getExamName())) {
                oldExamType = examInfoList.get(i - 1).getExamName();
            }
        }
        // 获取上次考试的所有成绩信息
        ExamCoversionTotal oldExamCoversionTotal = examCoversionTotalDao.findByStudentNumberAndExamType(stuNumber, oldExamType);
        if (null == oldExamCoversionTotal) {
            info = "查询此学生上次考试的所有信息失败";
            logger.error(info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }

        // 年级波动名次，进退名次
        int waveGrade = examCoversionTotal.getSchoolIndex() - oldExamCoversionTotal.getSchoolIndex();
        // 班级波动名称，进退名次
        int waveClass = examCoversionTotal.getClassIndex() - oldExamCoversionTotal.getClassIndex();

        List<ExamCoversionTotalDTO> examCoversionTotalDTOS = new ArrayList<>();

        ExamCoversionTotalDTO examCoversionTotalDTO = new ExamCoversionTotalDTO();
        examCoversionTotalDTO.setExamCoversionTotal(examCoversionTotal);
        /* 将年级进退名次放入到封装对象中*/
        examCoversionTotalDTO.setWaveGrade(waveGrade);
        /* 将班级进退名次放入到封装对象中*/
        examCoversionTotalDTO.setWaveClass(waveClass);
        examCoversionTotalDTOS.add(examCoversionTotalDTO);
        return examCoversionTotalDTOS;
    }

    @Override
    public List<ExamCoversionTotalSingleDTO> getExamCoversionTotalSingleInfo(String stuNumber, String examType, String subject) {
        ExamCoversionTotal examCoversionTotal = examCoversionTotalDao.findByStudentNumberAndExamType(stuNumber, examType);
        if (null == examCoversionTotal) {
            info = "查询此学生的所有信息失败";
            logger.error(info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        //使用原生SQL
        EntityManager em = ntityManagerFactory.createEntityManager();
        //String querysql = "SELECT * FROM exam_coversion_total WHERE class_id="+examCoversionTotal.getClassId()+ "AND exam_type="+examType+" ORDER BY "+ subject + " DESC"; 这个是错的
        //String querysql = "SELECT * FROM exam_coversion_total WHERE class_id=\""+examCoversionTotal.getClassId()+ "\" AND exam_type=\""+examType+"\" ORDER BY \""+ subject + "\" DESC"; 这个可以得用java转译字符 \"
        // 本次班级排名
        String querysql = "SELECT * FROM exam_coversion_total WHERE class_id='"+examCoversionTotal.getClassId()+ "' AND exam_type='"+examType+"' ORDER BY "+ subject + " DESC"; //这个是直接拼接
        logger.info("查询本次班级排名-->" + querysql);
        Query query = em.createNativeQuery(querysql, ExamCoversionTotal.class);
        @SuppressWarnings("unchecked")
        List<ExamCoversionTotal> examCoversionTotalSubject = query.getResultList();
        //本次年级排名
        String gradeQuerysql = "select * from exam_coversion_total where exam_type='"+examType+"' order by "+ subject + " desc";
        logger.info("查询本次年级排名-->" + gradeQuerysql);
        Query gradeQuery = em.createNativeQuery(gradeQuerysql, ExamCoversionTotal.class);
        @SuppressWarnings("unchecked")
        List<ExamCoversionTotal> gradeExamCoversionTotal = gradeQuery.getResultList();
        // 动态获取某科目的成绩
        String subjectSql = "select "+subject+" FROM exam_coversion_total WHERE student_number='"+stuNumber+"'and  exam_type='"+examType+"'";
        System.out.println(subjectSql);
        Query nativeQuery = em.createNativeQuery(subjectSql);
        @SuppressWarnings("unchecked")
        List<Double> subjectScore = nativeQuery.getResultList();

        em.close();

       // List<ExamCoversionTotal> examCoversionTotalSubject = examCoversionTotalDao.findAllByClassIdAndExamType(examCoversionTotal.getClassId(), examType,subject);
        //当前试卷的班级排名map
        Map<String, Integer> mapClass = new HashMap<>();
        for(int i = 1; i < examCoversionTotalSubject.size(); i++){
            if (subject.equals("yuwen_score")){
                mapClass.put(examCoversionTotalSubject.get(0).getStudentNumber(),1);
                if (examCoversionTotalSubject.get(i-1).getYuwenScore().equals(examCoversionTotalSubject.get(i).getYuwenScore())){
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i - 1);
                }else {
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("shuxue_score")){
                mapClass.put(examCoversionTotalSubject.get(0).getStudentNumber(),1);
                if (examCoversionTotalSubject.get(i-1).getShuxueScore().equals(examCoversionTotalSubject.get(i).getShuxueScore())){
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i - 1);
                }else {
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("yingyu_score")){
                mapClass.put(examCoversionTotalSubject.get(0).getStudentNumber(),1);
                if (examCoversionTotalSubject.get(i-1).getYingyuScore().equals(examCoversionTotalSubject.get(i).getYingyuScore())){
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i - 1);
                }else {
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("wuli_coversion")){
                mapClass.put(examCoversionTotalSubject.get(0).getStudentNumber(),1);
                if (examCoversionTotalSubject.get(i-1).getWuliCoversion().equals(examCoversionTotalSubject.get(i).getWuliCoversion())){
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i - 1);
                }else {
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("huaxue_coversion")){
                mapClass.put(examCoversionTotalSubject.get(0).getStudentNumber(),1);
                if (examCoversionTotalSubject.get(i-1).getHuaxueCoversion().equals(examCoversionTotalSubject.get(i).getHuaxueCoversion())){
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i - 1);
                }else {
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("shengwu_coversion")){
                mapClass.put(examCoversionTotalSubject.get(0).getStudentNumber(),1);
                if (examCoversionTotalSubject.get(i-1).getShengwuCoversion().equals(examCoversionTotalSubject.get(i).getShengwuCoversion())){
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i - 1);
                }else {
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("lishi_coversion")){
                mapClass.put(examCoversionTotalSubject.get(0).getStudentNumber(),1);
                if (examCoversionTotalSubject.get(i-1).getLishiCoversion().equals(examCoversionTotalSubject.get(i).getLishiCoversion())){
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i - 1);
                }else {
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("dili_coversion")){
                mapClass.put(examCoversionTotalSubject.get(0).getStudentNumber(),1);
                if (examCoversionTotalSubject.get(i-1).getDiliCoversion().equals(examCoversionTotalSubject.get(i).getDiliCoversion())){
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i - 1);
                }else {
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("zhengzhi_coversion")){
                mapClass.put(examCoversionTotalSubject.get(0).getStudentNumber(),1);
                if (examCoversionTotalSubject.get(i-1).getZhengzhiCoversion().equals(examCoversionTotalSubject.get(i).getZhengzhiCoversion())){
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i - 1);
                }else {
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i + 1);
                }
            }

        }
        //当前试卷的年级排名map
        Map<String, Integer> mapGrade = new HashMap<>();
        for (int i = 1; i < gradeExamCoversionTotal.size(); i++ ){
            if (subject.equals("yuwen_score")){
                mapGrade.put(gradeExamCoversionTotal.get(0).getStudentNumber(), 1);
                if (gradeExamCoversionTotal.get(i-1).getYuwenScore().equals(gradeExamCoversionTotal.get(i).getYuwenScore())){
                    mapGrade.put(gradeExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    mapGrade.put(gradeExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("shuxue_score")){
                mapGrade.put(gradeExamCoversionTotal.get(0).getStudentNumber(),1);
                if (gradeExamCoversionTotal.get(i-1).getShuxueScore().equals(gradeExamCoversionTotal.get(i).getShuxueScore())){
                    mapGrade.put(gradeExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    mapGrade.put(gradeExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("yingyu_score")){
                mapGrade.put(gradeExamCoversionTotal.get(0).getStudentNumber(),1);
                if (gradeExamCoversionTotal.get(i-1).getYingyuScore().equals(gradeExamCoversionTotal.get(i).getYingyuScore())){
                    mapGrade.put(gradeExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    mapGrade.put(gradeExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("wuli_coversion")){
                mapGrade.put(gradeExamCoversionTotal.get(0).getStudentNumber(),1);
                if (gradeExamCoversionTotal.get(i-1).getWuliCoversion().equals(gradeExamCoversionTotal.get(i).getWuliCoversion())){
                    mapGrade.put(gradeExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    mapGrade.put(gradeExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("huaxue_coversion")){
                mapGrade.put(gradeExamCoversionTotal.get(0).getStudentNumber(),1);
                if (gradeExamCoversionTotal.get(i-1).getHuaxueCoversion().equals(gradeExamCoversionTotal.get(i).getHuaxueCoversion())){
                    mapGrade.put(gradeExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    mapGrade.put(gradeExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("shengwu_coversion")){
                mapGrade.put(gradeExamCoversionTotal.get(0).getStudentNumber(),1);
                if (gradeExamCoversionTotal.get(i-1).getShengwuCoversion().equals(gradeExamCoversionTotal.get(i).getShengwuCoversion())){
                    mapGrade.put(gradeExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    mapGrade.put(gradeExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("lishi_coversion")){
                mapGrade.put(gradeExamCoversionTotal.get(0).getStudentNumber(),1);
                if (gradeExamCoversionTotal.get(i-1).getLishiCoversion().equals(gradeExamCoversionTotal.get(i).getLishiCoversion())){
                    mapGrade.put(gradeExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    mapGrade.put(gradeExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("dili_coversion")){
                mapGrade.put(gradeExamCoversionTotal.get(0).getStudentNumber(),1);
                if (gradeExamCoversionTotal.get(i-1).getDiliCoversion().equals(gradeExamCoversionTotal.get(i).getDiliCoversion())){
                    mapGrade.put(gradeExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    mapGrade.put(gradeExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("zhengzhi_coversion")){
                mapGrade.put(gradeExamCoversionTotal.get(0).getStudentNumber(),1);
                if (gradeExamCoversionTotal.get(i-1).getZhengzhiCoversion().equals(gradeExamCoversionTotal.get(i).getZhengzhiCoversion())){
                    mapGrade.put(gradeExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    mapGrade.put(gradeExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }
        }

        // 获取所有考试列表
        List<ExamInfo> examInfoList = examInfoDao.findAll();
        if (examInfoList == null || examInfoList.size() == 0) {
            info = "查无结果";
            logger.error(info);
            throw new ScoreException(ResultEnum.DATABASE_OP_EXCEPTION, info);
        }
        int examTnfoId = examInfoDao.findByExamName(examType);
        SubjectFullScore subjectFullScore = subjectFullScoreDao.findById(examTnfoId);
        // 本次考试的全科总分
        int sum = Math.toIntExact(subjectFullScore.getYuwen() + subjectFullScore.getShuxue() + subjectFullScore.getYingyu() + subjectFullScore.getWuli() + subjectFullScore.getHuaxue()
                + subjectFullScore.getShengwu() + subjectFullScore.getZhengzhi() + subjectFullScore.getDili() + subjectFullScore.getLishi()) - 300;

        String oldExamType = null;
        for (int i = 0; i < examInfoList.size(); i++) {
            if (examType.equals(examInfoList.get(0).getExamName())){
                info = "本次为首次考试，暂无排名波动情况";
                // 年级波动名次，进退名次
                int waveGrade = 0;
                // 班级波动名称，进退名次
                int waveClass =0;

                List<ExamCoversionTotalSingleDTO> examCoversionTotalSingleDTOList = new ArrayList<>();
                ExamCoversionTotalSingleDTO examCoversionTotalSingleDTO = new ExamCoversionTotalSingleDTO();
                examCoversionTotalSingleDTO.setExamCoversionTotal(examCoversionTotal);
                examCoversionTotalSingleDTO.setClassRank(mapClass.get(stuNumber));//班排名
                examCoversionTotalSingleDTO.setGradeRank(mapGrade.get(stuNumber));//年排名
                examCoversionTotalSingleDTO.setWaveGrade(waveGrade);//年级进退名次
                examCoversionTotalSingleDTO.setWaveClass(waveClass);//班级进退名次

                examCoversionTotalSingleDTO.setClassNumber(examCoversionTotalSubject.size());//班级人数
                examCoversionTotalSingleDTO.setGradeNumber(gradeExamCoversionTotal.size());// 年级人数
                examCoversionTotalSingleDTO.setSumScore(sum);//总分标准
                examCoversionTotalSingleDTO.setLanguageScore(Math.toIntExact(subjectFullScore.getYuwen()));//语文满分
                examCoversionTotalSingleDTO.setMathScore(Math.toIntExact(subjectFullScore.getShuxue()));//数学满分
                examCoversionTotalSingleDTO.setEnglishScore(Math.toIntExact(subjectFullScore.getYingyu()));//英语满分
                examCoversionTotalSingleDTO.setPhysicalScore(Math.toIntExact(subjectFullScore.getWuli()));// 物理满分
                examCoversionTotalSingleDTO.setChemistryScore(Math.toIntExact(subjectFullScore.getHuaxue()));//化学满分
                examCoversionTotalSingleDTO.setBiologicalScore(Math.toIntExact(subjectFullScore.getShengwu()));//生物满分
                examCoversionTotalSingleDTO.setPoliticalScore(Math.toIntExact(subjectFullScore.getZhengzhi()));// 政治满分
                examCoversionTotalSingleDTO.setHistoryScore(Math.toIntExact(subjectFullScore.getLishi())); //历史满分
                examCoversionTotalSingleDTO.setGeographyScore(Math.toIntExact(subjectFullScore.getDili()));//地理满分
                examCoversionTotalSingleDTO.setScore(String.valueOf(subjectScore.get(0)));

                examCoversionTotalSingleDTOList.add(examCoversionTotalSingleDTO);
                return examCoversionTotalSingleDTOList;
            } else  if (examType.equals(examInfoList.get(i).getExamName())) {
                //获取上次试卷的名称
                oldExamType = examInfoList.get(i - 1).getExamName();
            }
        }

        //再次使用原生SQL语句查询，来获取班级年级的排名
        EntityManager entityManager = ntityManagerFactory.createEntityManager();
        //上次班级排名
        String oldClassQuerysql = "SELECT * FROM exam_coversion_total WHERE class_id='"+examCoversionTotal.getClassId()+ "' AND exam_type='"+oldExamType+"' ORDER BY "+ subject + " DESC";
        Query oldClass =  entityManager.createNativeQuery(oldClassQuerysql, ExamCoversionTotal.class);
        @SuppressWarnings("unchecked")
        List<ExamCoversionTotal> oldClassExamCoversionTotal =  oldClass.getResultList();
        //上次年级排名
        String oldGradeQuerysql = "select * from exam_coversion_total where exam_type='"+oldExamType+"' order by "+ subject + " desc";
        Query oldgGradeQuery = entityManager.createNativeQuery(oldGradeQuerysql, ExamCoversionTotal.class);
        @SuppressWarnings("unchecked")
        List<ExamCoversionTotal> oldGradeExamCoversionTotal = oldgGradeQuery.getResultList();

        entityManager.close();

        //上次试卷的班级排名map
        Map<String, Integer> oldMapClass = new HashMap<>();
        for(int i = 1; i < oldClassExamCoversionTotal.size(); i++){
            if (subject.equals("yuwen_score")){
                oldMapClass.put(oldClassExamCoversionTotal.get(0).getStudentNumber(),1);
                if (oldClassExamCoversionTotal.get(i-1).getYuwenScore().equals(oldClassExamCoversionTotal.get(i).getYuwenScore())){
                    oldMapClass.put(oldClassExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    oldMapClass.put(oldClassExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("shuxue_score")){
                oldMapClass.put(oldClassExamCoversionTotal.get(0).getStudentNumber(),1);
                if (oldClassExamCoversionTotal.get(i-1).getShuxueScore().equals(oldClassExamCoversionTotal.get(i).getShuxueScore())){
                    oldMapClass.put(oldClassExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    oldMapClass.put(oldClassExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("yingyu_score")){
                oldMapClass.put(oldClassExamCoversionTotal.get(0).getStudentNumber(),1);
                if (oldClassExamCoversionTotal.get(i-1).getYingyuScore().equals(oldClassExamCoversionTotal.get(i).getYingyuScore())){
                    oldMapClass.put(oldClassExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    oldMapClass.put(oldClassExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("wuli_coversion")){
                oldMapClass.put(oldClassExamCoversionTotal.get(0).getStudentNumber(),1);
                if (oldClassExamCoversionTotal.get(i-1).getWuliCoversion().equals(oldClassExamCoversionTotal.get(i).getWuliCoversion())){
                    oldMapClass.put(oldClassExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    oldMapClass.put(oldClassExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("huaxue_coversion")){
                oldMapClass.put(oldClassExamCoversionTotal.get(0).getStudentNumber(),1);
                if (oldClassExamCoversionTotal.get(i-1).getHuaxueCoversion().equals(oldClassExamCoversionTotal.get(i).getHuaxueCoversion())){
                    oldMapClass.put(oldClassExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    oldMapClass.put(oldClassExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("shengwu_coversion")){
                oldMapClass.put(oldClassExamCoversionTotal.get(0).getStudentNumber(),1);
                if (oldClassExamCoversionTotal.get(i-1).getShengwuCoversion().equals(oldClassExamCoversionTotal.get(i).getShengwuCoversion())){
                    oldMapClass.put(oldClassExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    oldMapClass.put(oldClassExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("lishi_coversion")){
                oldMapClass.put(oldClassExamCoversionTotal.get(0).getStudentNumber(),1);
                if (oldClassExamCoversionTotal.get(i-1).getLishiCoversion().equals(oldClassExamCoversionTotal.get(i).getLishiCoversion())){
                    oldMapClass.put(oldClassExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    oldMapClass.put(oldClassExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("dili_coversion")){
                oldMapClass.put(oldClassExamCoversionTotal.get(0).getStudentNumber(),1);
                if (oldClassExamCoversionTotal.get(i-1).getDiliCoversion().equals(oldClassExamCoversionTotal.get(i).getDiliCoversion())){
                    oldMapClass.put(oldClassExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    oldMapClass.put(oldClassExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("zhengzhi_coversion")){
                oldMapClass.put(oldClassExamCoversionTotal.get(0).getStudentNumber(),1);
                if (oldClassExamCoversionTotal.get(i-1).getZhengzhiCoversion().equals(oldClassExamCoversionTotal.get(i).getZhengzhiCoversion())){
                    oldMapClass.put(oldClassExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    oldMapClass.put(oldClassExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }

        }

        //当前试卷的年级排名map
        Map<String, Integer> oldMapGrade = new HashMap<>();
        for (int i = 1; i < oldGradeExamCoversionTotal.size(); i++ ){
            if (subject.equals("yuwen_score")){
                oldMapGrade.put(oldGradeExamCoversionTotal.get(0).getStudentNumber(), 1);
                if (oldGradeExamCoversionTotal.get(i-1).getYuwenScore().equals(oldGradeExamCoversionTotal.get(i).getYuwenScore())){
                    oldMapGrade.put(oldGradeExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    oldMapGrade.put(oldGradeExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("shuxue_score")){
                oldMapGrade.put(oldGradeExamCoversionTotal.get(0).getStudentNumber(),1);
                if (oldGradeExamCoversionTotal.get(i-1).getShuxueScore().equals(oldGradeExamCoversionTotal.get(i).getShuxueScore())){
                    oldMapGrade.put(oldGradeExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    oldMapGrade.put(oldGradeExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("yingyu_score")){
                oldMapGrade.put(oldGradeExamCoversionTotal.get(0).getStudentNumber(),1);
                if (oldGradeExamCoversionTotal.get(i-1).getYingyuScore().equals(oldGradeExamCoversionTotal.get(i).getYingyuScore())){
                    oldMapGrade.put(oldGradeExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    oldMapGrade.put(oldGradeExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("wuli_coversion")){
                oldMapGrade.put(oldGradeExamCoversionTotal.get(0).getStudentNumber(),1);
                if (oldGradeExamCoversionTotal.get(i-1).getWuliCoversion().equals(oldGradeExamCoversionTotal.get(i).getWuliCoversion())){
                    oldMapGrade.put(oldGradeExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    oldMapGrade.put(oldGradeExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("huaxue_coversion")){
                oldMapGrade.put(oldGradeExamCoversionTotal.get(0).getStudentNumber(),1);
                if (oldGradeExamCoversionTotal.get(i-1).getHuaxueCoversion().equals(oldGradeExamCoversionTotal.get(i).getHuaxueCoversion())){
                    oldMapGrade.put(oldGradeExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    oldMapGrade.put(oldGradeExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("shengwu_coversion")){
                oldMapGrade.put(oldGradeExamCoversionTotal.get(0).getStudentNumber(),1);
                if (oldGradeExamCoversionTotal.get(i-1).getShengwuCoversion().equals(oldGradeExamCoversionTotal.get(i).getShengwuCoversion())){
                    oldMapGrade.put(oldGradeExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    oldMapGrade.put(oldGradeExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("lishi_coversion")){
                oldMapGrade.put(oldGradeExamCoversionTotal.get(0).getStudentNumber(),1);
                if (oldGradeExamCoversionTotal.get(i-1).getLishiCoversion().equals(oldGradeExamCoversionTotal.get(i).getLishiCoversion())){
                    oldMapGrade.put(oldGradeExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    oldMapGrade.put(oldGradeExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("dili_coversion")){
                oldMapGrade.put(oldGradeExamCoversionTotal.get(0).getStudentNumber(),1);
                if (oldGradeExamCoversionTotal.get(i-1).getDiliCoversion().equals(oldGradeExamCoversionTotal.get(i).getDiliCoversion())){
                    oldMapGrade.put(oldGradeExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    oldMapGrade.put(oldGradeExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("zhengzhi_coversion")){
                oldMapGrade.put(oldGradeExamCoversionTotal.get(0).getStudentNumber(),1);
                if (oldGradeExamCoversionTotal.get(i-1).getZhengzhiCoversion().equals(oldGradeExamCoversionTotal.get(i).getZhengzhiCoversion())){
                    oldMapGrade.put(oldGradeExamCoversionTotal.get(i).getStudentNumber(), i - 1);
                }else {
                    oldMapGrade.put(oldGradeExamCoversionTotal.get(i).getStudentNumber(), i + 1);
                }
            }
        }

        // 班级波动名次，进退名次
        int waveClass = mapClass.get(stuNumber) - oldMapClass.get(stuNumber);
        // 年级波动名称，进退名次
        int waveGrade = mapGrade.get(stuNumber) - oldMapGrade.get(stuNumber);



        List<ExamCoversionTotalSingleDTO> examCoversionTotalSingleDTOList = new ArrayList<>();
        ExamCoversionTotalSingleDTO examCoversionTotalSingleDTO = new ExamCoversionTotalSingleDTO();
        examCoversionTotalSingleDTO.setExamCoversionTotal(examCoversionTotal);
        examCoversionTotalSingleDTO.setClassRank(mapClass.get(stuNumber));//班排名
        examCoversionTotalSingleDTO.setGradeRank(mapGrade.get(stuNumber));//年排名
        examCoversionTotalSingleDTO.setWaveGrade(waveGrade);//年级进退名次
        examCoversionTotalSingleDTO.setWaveClass(waveClass);//班级进退名次

        examCoversionTotalSingleDTO.setClassNumber(examCoversionTotalSubject.size());//班级人数
        examCoversionTotalSingleDTO.setGradeNumber(gradeExamCoversionTotal.size());// 年级人数
        examCoversionTotalSingleDTO.setSumScore(sum);//总分标准
        examCoversionTotalSingleDTO.setLanguageScore(Math.toIntExact(subjectFullScore.getYuwen()));//语文满分
        examCoversionTotalSingleDTO.setMathScore(Math.toIntExact(subjectFullScore.getShuxue()));//数学满分
        examCoversionTotalSingleDTO.setEnglishScore(Math.toIntExact(subjectFullScore.getYingyu()));//英语满分
        examCoversionTotalSingleDTO.setPhysicalScore(Math.toIntExact(subjectFullScore.getWuli()));// 物理满分
        examCoversionTotalSingleDTO.setChemistryScore(Math.toIntExact(subjectFullScore.getHuaxue()));//化学满分
        examCoversionTotalSingleDTO.setBiologicalScore(Math.toIntExact(subjectFullScore.getShengwu()));//生物满分
        examCoversionTotalSingleDTO.setPoliticalScore(Math.toIntExact(subjectFullScore.getZhengzhi()));// 政治满分
        examCoversionTotalSingleDTO.setHistoryScore(Math.toIntExact(subjectFullScore.getLishi())); //历史满分
        examCoversionTotalSingleDTO.setGeographyScore(Math.toIntExact(subjectFullScore.getDili()));//地理满分
        examCoversionTotalSingleDTO.setScore(String.valueOf(subjectScore.get(0)));

        examCoversionTotalSingleDTOList.add(examCoversionTotalSingleDTO);

        //打印出哪个接口，参数值是什么，当前时间，以便记录下当前访问哪个接口等信息，如有有openid则也记录下
        logger.info("getExamCoversionTotalSingleInfo--->"+"stuNumber :"+stuNumber+"  "+"examType:"+examType+"  subject: "+subject+"  time:"+getNowTime());
        return examCoversionTotalSingleDTOList;

    }


    @Override
    public List<ExamCoversionTotalSectionDTO> getExamCoversionTotalSectionInfo(String stuNumber, String examType) {
        ExamCoversionTotal examCoversionTotal = examCoversionTotalDao.findByStudentNumberAndExamType(stuNumber, examType);
        if (null == examCoversionTotal) {
            info = "查询此学生的所有信息失败";
            logger.error(info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        // 语数英三科总分
        float threeSubject = 0;
        //剩余6选3的总分
        float comprehensive = 0;
        threeSubject = (float) (examCoversionTotal.getYuwenScore() + examCoversionTotal.getShuxueScore() + examCoversionTotal.getYingyuScore());
        comprehensive = (float) (examCoversionTotal.getWuliCoversion() + examCoversionTotal.getHuaxueCoversion() + examCoversionTotal.getShengwuCoversion() + examCoversionTotal.getLishiCoversion() + examCoversionTotal.getDiliCoversion() + examCoversionTotal.getZhengzhiCoversion());

        //三科的班级排名
        List<String[]> classRank = examCoversionTotalDao.findByClassIdAndExamType(examCoversionTotal.getClassId(), examType);
        Map<Object, Object> map = new HashMap<>();
        for(int i = 0; i < classRank.size(); i++) {
            for (Object classRankObject[] : classRank) {
                map.put(classRankObject[0], classRankObject[1]);
            }
        }
            //将map中的值放到list中，进行排序
            List<String> mapValueList = new ArrayList<>();
            for (Object vaule : map.values()){
                mapValueList.add(String.valueOf(vaule));
            }
            //对mapValueList进行降序排序
            Collections.sort(mapValueList, Collections.reverseOrder());

//三科 班排的第一种方法，第一种方法无需在进行排名，只需要排好序即可
            // mapValueRank存放的是 分值和排名，这个是第二种方法，不用此方法，而且有点问题，mapValueRank有32，而mapValueList有33个排名
            Map<String, Integer> mapValueRank = new HashMap<>();
            for (int j = 1; j < mapValueList.size(); j++){
                mapValueRank.put(mapValueList.get(0), 1);
                if (mapValueList.get(j - 1 ).equals(mapValueList.get(j))){
                    mapValueRank.put(mapValueList.get(j), j - 1);
                }else {
                    mapValueRank.put(mapValueList.get(j), j + 1);
                }
            }

//三科年级排名
        List<String[]> threeSubjectGradeRank = examCoversionTotalDao.findByClassIdAndExamTypeGrade(examType);
        Map<Object, Object> threeSubjectGradeMap = new HashMap<>();
        for (int k = 0; k < threeSubjectGradeRank.size(); k++){
            for (Object gradeRankObject[] : threeSubjectGradeRank){
                threeSubjectGradeMap.put(gradeRankObject[0], gradeRankObject[1]);
            }
        }
        List<String> threeSubjectGradeList = new ArrayList<>();
        for (Object threeSubjectValue : threeSubjectGradeMap.values()){
            threeSubjectGradeList.add(String.valueOf(threeSubjectValue));
        }
        Collections.sort(threeSubjectGradeList, Collections.reverseOrder());

        //综合的班排名次
        List<String[]> complexClassRank = examCoversionTotalDao.findByClassIdAndExamTypeComplex(examCoversionTotal.getClassId(), examType);
        Map<Object, Object> complexClassMap= new HashMap<>();
        for(int i = 0; i < complexClassRank.size(); i++) {
            for (Object classRankObject[] : complexClassRank) {
                complexClassMap.put(classRankObject[0], classRankObject[1]);
            }
        }
        List<String> mapValueListComplex = new ArrayList<>();
        for (Object vaule : complexClassMap.values()){
            mapValueListComplex.add(String.valueOf(vaule));
        }
        Collections.sort(mapValueListComplex, Collections.reverseOrder());

        //综合的年排名次
        List<String[]> complexGradeRank = examCoversionTotalDao.findByClassIdAndExamTypeComplexGrade(examType);
        Map<Object, Object> complexGradeMap= new HashMap<>();
        for(int i = 0; i < complexGradeRank.size(); i++) {
            for (Object classRankObject[] : complexGradeRank) {
                complexGradeMap.put(classRankObject[0], classRankObject[1]);
            }
        }
        List<String> mapValueListComplexGrade = new ArrayList<>();
        for (Object vaule : complexGradeMap.values()){
            mapValueListComplexGrade.add(String.valueOf(vaule));
        }
        Collections.sort(mapValueListComplexGrade, Collections.reverseOrder());

        //本次班级、年级排名情况
        //三科本次年级排名
        int gradeRank  = threeSubjectGradeList.indexOf(String.valueOf(threeSubjectGradeMap.get(stuNumber))) + 1;
        // 当前的班级排名
        int classrank = mapValueList.indexOf(String.valueOf(map.get(stuNumber))) + 1;
        // 综合本次班级排名
        int complexClassrank = mapValueListComplex.indexOf(String.valueOf(complexClassMap.get(stuNumber))) + 1;
        // 综合本次年级排名
        int complexGraderank = mapValueListComplexGrade.indexOf(String.valueOf(complexGradeMap.get(stuNumber))) + 1;

        List<String> list = new ArrayList<>();

        if (!examCoversionTotal.getWuliCoversion().toString().equals("0.0")){
            list.add("物理");
        }
        if (!examCoversionTotal.getHuaxueCoversion().toString().equals("0.0")){
            list.add("化学");
        }
        if (!examCoversionTotal.getShengwuCoversion().toString().equals("0.0")){
            list.add("生物");
        }
        if (!examCoversionTotal.getLishiCoversion().toString().equals("0.0") ){
            list.add("历史");
        }
        if (!examCoversionTotal.getDiliCoversion().toString().equals("0.0")){
            list.add("地理");
        }
        if (!examCoversionTotal.getZhengzhiCoversion().toString().equals("0.0")){
            list.add("政治");
        }

        //显示上次考试的所有信息
        // 获取所有考试列表
        List<ExamInfo> examInfoList = examInfoDao.findAll();
        if (examInfoList == null || examInfoList.size() == 0) {
            info = "查无结果";
            logger.error(info);
            throw new ScoreException(ResultEnum.DATABASE_OP_EXCEPTION, info);
        }
        String oldExamType = null;
        for (int i = 0; i < examInfoList.size(); i++) {
            if (examType.equals(examInfoList.get(0).getExamName())){
                info = "本次为首次考试，暂无排名波动情况";
                logger.info(info);
                //三科的班级进退步
                int threewaveClass =0;
                //三科年级进退名次
                int threewaveGrade = 0;
                //综合班级进退名次
                int complexwaveClass = 0;
                //综合的年级进退名次
                int complexwaveGrade = 0;
                List<ExamCoversionTotalSectionDTO> examCoversionTotalSectionDTOList = new ArrayList<>();
                ExamCoversionTotalSectionDTO examCoversionTotalSectionDTO = new ExamCoversionTotalSectionDTO();
                examCoversionTotalSectionDTO.setExamCoversionTotal(examCoversionTotal);
                examCoversionTotalSectionDTO.setThreeSubject(threeSubject);
                examCoversionTotalSectionDTO.setComprehensive(comprehensive);
                // 求班排的第二中方法，即用排好序的list，取下标法，indexOf:如果元素相同取第一次出现的下标，
                examCoversionTotalSectionDTO.setClassRank(classrank);
                examCoversionTotalSectionDTO.setGradeRank(gradeRank);
                examCoversionTotalSectionDTO.setComplexClassRank(complexClassrank);
                examCoversionTotalSectionDTO.setComplexGradeRank(complexGraderank);
                //三科和综合的班级、年级进退名次
                examCoversionTotalSectionDTO.setThreeWaveClass(threewaveClass);
                examCoversionTotalSectionDTO.setThreeWaveGrade(threewaveGrade);
                examCoversionTotalSectionDTO.setComplexWaveClass(complexwaveClass);
                examCoversionTotalSectionDTO.setComplexWaveGrade(complexwaveGrade);
                examCoversionTotalSectionDTO.setList(list);
                examCoversionTotalSectionDTOList.add(examCoversionTotalSectionDTO);
                return examCoversionTotalSectionDTOList;

            } else  if (examType.equals(examInfoList.get(i).getExamName())) {
                //获取上次试卷的名称
                oldExamType = examInfoList.get(i - 1).getExamName();
            }
        }
        //三科的上次班级排名
        List<String[]> oldClassRank = examCoversionTotalDao.findByClassIdAndExamType(examCoversionTotal.getClassId(), oldExamType);
        Map<Object, Object> oldMap = new HashMap<>();
        for(int i = 0; i < oldClassRank.size(); i++) {
            for (Object classRankObject[] : oldClassRank) {
                oldMap.put(classRankObject[0], classRankObject[1]);
            }
        }
        //将map中的值放到list中，进行排序
        List<String> oldMapValueList = new ArrayList<>();
        for (Object vaule : oldMap.values()){
            oldMapValueList.add(String.valueOf(vaule));
        }
        //对mapValueList进行降序排序
        Collections.sort(oldMapValueList, Collections.reverseOrder());

        //三科的班级进退步
        int threewaveClass = classrank - (oldMapValueList.indexOf(String.valueOf(oldMap.get(stuNumber))) + 1);
        //三科上次年级排名
        List<String[]> oldThreeSubjectGradeRank = examCoversionTotalDao.findByClassIdAndExamTypeGrade(oldExamType);
        Map<Object, Object> oldThreeSubjectGradeMap = new HashMap<>();
        for (int k = 0; k < oldThreeSubjectGradeRank.size(); k++){
            for (Object gradeRankObject[] : oldThreeSubjectGradeRank){
                oldThreeSubjectGradeMap.put(gradeRankObject[0], gradeRankObject[1]);
            }
        }
        List<String> oldThreeSubjectGradeList = new ArrayList<>();
        for (Object threeSubjectValue : oldThreeSubjectGradeMap.values()){
            oldThreeSubjectGradeList.add(String.valueOf(threeSubjectValue));
        }
        Collections.sort(oldThreeSubjectGradeList, Collections.reverseOrder());

        //三科年级进退名次
        int threewaveGrade = gradeRank - (oldThreeSubjectGradeList.indexOf(String.valueOf(oldThreeSubjectGradeMap.get(stuNumber))) + 1);

        //综合的上次班排名次
        List<String[]> oldComplexClassRank = examCoversionTotalDao.findByClassIdAndExamTypeComplex(examCoversionTotal.getClassId(), oldExamType);
        Map<Object, Object> oldComplexClassMap= new HashMap<>();
        for(int i = 0; i < oldComplexClassRank.size(); i++) {
            for (Object classRankObject[] : oldComplexClassRank) {
                oldComplexClassMap.put(classRankObject[0], classRankObject[1]);
            }
        }
        List<String> oldMapValueListComplex = new ArrayList<>();
        for (Object vaule : oldComplexClassMap.values()){
            oldMapValueListComplex.add(String.valueOf(vaule));
        }
        Collections.sort(oldMapValueListComplex, Collections.reverseOrder());
       //综合班级进退名次
        int complexwaveClass = complexClassrank - (oldMapValueListComplex.indexOf(String.valueOf(oldComplexClassMap.get(stuNumber))) + 1);
        //综合的上次年排名次
        List<String[]> oldcomplexGradeRank = examCoversionTotalDao.findByClassIdAndExamTypeComplexGrade(oldExamType);
        Map<Object, Object> oldcomplexGradeMap= new HashMap<>();
        for(int i = 0; i < oldcomplexGradeRank.size(); i++) {
            for (Object classRankObject[] : oldcomplexGradeRank) {
                oldcomplexGradeMap.put(classRankObject[0], classRankObject[1]);
            }
        }
        List<String> oldmapValueListComplexGrade = new ArrayList<>();
        for (Object vaule : oldcomplexGradeMap.values()){
            oldmapValueListComplexGrade.add(String.valueOf(vaule));
        }
        Collections.sort(oldmapValueListComplexGrade, Collections.reverseOrder());

        //综合的年级进退名次
        int complexwaveGrade = complexGraderank - (oldmapValueListComplexGrade.indexOf(String.valueOf(oldcomplexGradeMap.get(stuNumber))) + 1);

        List<ExamCoversionTotalSectionDTO> examCoversionTotalSectionDTOList = new ArrayList<>();
        ExamCoversionTotalSectionDTO examCoversionTotalSectionDTO = new ExamCoversionTotalSectionDTO();
        examCoversionTotalSectionDTO.setExamCoversionTotal(examCoversionTotal);
        examCoversionTotalSectionDTO.setThreeSubject(threeSubject);
        examCoversionTotalSectionDTO.setComprehensive(comprehensive);
        // 求班排的第二中方法，即用排好序的list，取下标法，indexOf:如果元素相同取第一次出现的下标，
        examCoversionTotalSectionDTO.setClassRank(classrank);

        //第一种方法，此方法
//        String key = String.valueOf(map.get(stuNumber));//强转有问题，这样的也有问题，如果小数多(科学计数法)会出问题
//        examCoversionTotalSectionDTO.setClassRank(mapValueRank.get(key));

        examCoversionTotalSectionDTO.setGradeRank(gradeRank);

        examCoversionTotalSectionDTO.setComplexClassRank(complexClassrank);
        examCoversionTotalSectionDTO.setComplexGradeRank(complexGraderank);

        //三科和综合的班级、年级进退名次
        examCoversionTotalSectionDTO.setThreeWaveClass(threewaveClass);
        examCoversionTotalSectionDTO.setThreeWaveGrade(threewaveGrade);
        examCoversionTotalSectionDTO.setComplexWaveClass(complexwaveClass);
        examCoversionTotalSectionDTO.setComplexWaveGrade(complexwaveGrade);

        examCoversionTotalSectionDTO.setList(list);
        examCoversionTotalSectionDTOList.add(examCoversionTotalSectionDTO);

        return examCoversionTotalSectionDTOList;
    }

    @Override
    public List<SixRateDTO> getSixRateInfo(String stuNumber, String examType) {
        ExamCoversionTotal examCoversionTotal = examCoversionTotalDao.findByStudentNumberAndExamType(stuNumber, examType);
        if (null == examCoversionTotal) {
            info = "查询此学生的所有信息失败";
            logger.error(info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
       long star = System.currentTimeMillis();
        // 此班级的所有的总分数据
        List<Double> coversionTotalList= examCoversionTotalDao.getCoversionTotalByClassIdAndExamType(examCoversionTotal.getClassId(), examType);
        int examTnfoId = examInfoDao.findByExamName(examType);
//        ExamFullScoreSet examFullScoreSet=  examFullScoreSetDao.findByExaminfoId((int) examTnfoId);
//        SubjectFullScore sbujectFullScore = subjectFullScoreDao.findOne((int) examFullScoreSet.getId());
//        int totalScore = (int) (sbujectFullScore.getYingyu() + sbujectFullScore.getShuxue()+sbujectFullScore.getYingyu()+sbujectFullScore.getWuli()+sbujectFullScore.getHuaxue()+sbujectFullScore.getShengwu()+sbujectFullScore.getDili()+sbujectFullScore.getLishi()+sbujectFullScore.getZhengzhi() - 300);
        BigInteger tatolscore = examCoversionTotalDao.findSchametotal(examTnfoId);
        long end = System.currentTimeMillis();
        System.out.println("sql耗费时间--->  "+ String.valueOf(end - star) + "ms");
        int score = Integer.parseInt(tatolscore.toString().trim()) - 300;
        double a = 0, avg = 0, personsum = 0, classtotalscore = 0;
        double highnumradio;
        double excellentratio;
        double goodratio;
        double mediumratio;
        double passratio;
        double failratio;
        double beyondradio;
        int highnum = 0,            //高分人数
                excellentnum = 0,       //优秀人数
                goodnum = 0,   //良好人数
                mediumnum = 0,    //中等人数
                passnum = 0,    //及格人数
                failnum = 0,   //低分人数
                beyondnum = 0;   //超平均人数
        personsum = coversionTotalList.size();
        for (int i = 0; i < coversionTotalList.size(); i++) {

            classtotalscore = classtotalscore + coversionTotalList.get(i);
            if (coversionTotalList.get(i) >= score * 0.9) {
                highnum++;
            } else if (coversionTotalList.get(i) >= score * 0.85 && coversionTotalList.get(i) < score * 0.90) {
                excellentnum++;
            } else if (coversionTotalList.get(i) >= score * 0.75 && coversionTotalList.get(i) < score * 0.85) {
                goodnum++;
            } else if (coversionTotalList.get(i) >= score * 0.60 && coversionTotalList.get(i) < score * 0.75) {
                passnum++;
            } else {
                failnum++;
            }
        }
        // 班级平均分
        avg = classtotalscore / personsum;
        for (int j = 0; j < coversionTotalList.size(); j++) {
            if (coversionTotalList.get(j) > avg) {
                beyondnum++;
            }
        }
        long forTime = System.currentTimeMillis();
        System.out.println("for循环耗费时间--->" + String.valueOf(forTime - end) + "ms");
        String location = "";
        if (examCoversionTotal.getCoversionTotal() >= score * 0.9) {
            location = "高分区域";
        } else if (examCoversionTotal.getCoversionTotal() >= score * 0.85 && examCoversionTotal.getCoversionTotal() < score * 0.90) {
            location = "优秀区域";
        } else if (examCoversionTotal.getCoversionTotal() >= score * 0.75 && examCoversionTotal.getCoversionTotal() < score * 0.85) {
            location = "良好区域";
        } else if (examCoversionTotal.getCoversionTotal() >= score * 0.60 && examCoversionTotal.getCoversionTotal() < score * 0.75) {
            location = "及格区域";
        } else {
            location = "低分区域";
        }
        highnumradio = highnum / personsum;
        excellentratio = excellentnum / personsum;
        goodratio = goodnum / personsum;
        passratio = passnum / personsum;
        failratio = failnum / personsum;
        beyondradio = beyondnum / personsum;

        //保留两位小数
        DecimalFormat df = new DecimalFormat("#.00");

        List<SixRateDTO> sixRateDTOList = new ArrayList<>();
        SixRateDTO sixRateDTO = new SixRateDTO();
        sixRateDTO.setHighNumRate(Double.parseDouble(df.format(highnumradio)));
        sixRateDTO.setExcellentRate(Double.parseDouble(df.format(excellentratio)));
        sixRateDTO.setGoodRate(Double.parseDouble(df.format(goodratio)));
        sixRateDTO.setPassRate(Double.parseDouble(df.format(passratio)));
        sixRateDTO.setFailRate(Double.parseDouble(df.format(failratio)));
        sixRateDTO.setBeyondRate(Double.parseDouble(df.format(beyondradio)));
        sixRateDTO.setLocationRate(location);
        sixRateDTOList.add(sixRateDTO);
long entTime = System.currentTimeMillis();
        System.out.println("结束时间-->" + String.valueOf(entTime - star) + "ms");

       // logger.info("getSixRateInfo--->"+"openid:"+openid+"  "+"artId:"+artId+"  "+"time:"+getNowTime());
        //打印出哪个接口，参数值是什么，当前时间，以便记录下当前访问哪个接口等信息，如有有openid则也记录下
        logger.info("getSixRateInfo--->stuNumber :{}, examType: {}, time: {}",stuNumber,examType,getNowTime());
        return sixRateDTOList;
    }

    @Override
    public List<SubjectAnalysisDTO> getSubjectAnalysisInfo(String stuNumber, String examType) {
        ExamCoversionTotal examCoversionTotal = examCoversionTotalDao.findByStudentNumberAndExamType(stuNumber, examType);
        if (null == examCoversionTotal) {
            info = "查询此学生的所有信息失败";
            logger.error(info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        List<String> totalScoreList = examCoversionTotalDao.findByTotalScore(examType);
        int totalScoreRank = totalScoreList.indexOf(Float.parseFloat(examCoversionTotal.getCoversionTotal().toString())) + 1 ;
        logger.info("总分的年级排名：{}", totalScoreRank);
        //LinkedHashMap将map中的顺序按照添加顺序排列
        Map<String, Map<String, String>> map = new LinkedHashMap<>();
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

        // 本次 各单科与总分的比值，即本次考试的学科贡献率
        Map<String, String> contributionRate = new HashMap<>();
        double language, // 语文
                math, // 数学
                english, // 英语
                physical, // 物理
                chemistry, //化学
                biological, // 生物
                political, //政治
                history, // 历史
                geography; //地理
        //保留两位小数
        DecimalFormat df = new DecimalFormat("#0.00");
        language = examCoversionTotal.getYuwenScore()/ examCoversionTotal.getCoversionTotal();
        math =examCoversionTotal.getShuxueScore() /examCoversionTotal.getCoversionTotal();
        english = examCoversionTotal.getYingyuScore() /examCoversionTotal.getCoversionTotal();

        // 单科和总分的年级差值
        Map<String , String> equilibriumDifferenceMap = new HashMap<>();

        //获取年级总人数
        int gradeSum = examCoversionTotalDao.countByExamType(examType);
        //归一化操作，用各科的 排名/各科总人数 得到的率值，来判断降退
        //对比标准为  年级的率值
        float gradeRate =  (float) examCoversionTotal.getSchoolIndex() / (float) gradeSum;

        contributionRate.put("语文", df.format(language) + "%");
        List<String> yuwenGradeExamCoversionTotal =  examCoversionTotalDao.findByYuwenScore(examType);
        int yuwenGradeRank = yuwenGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getYuwenScore().toString())) + 1;
//        equilibriumDifferenceMap.put("语文差值", (int) (examCoversionTotal.getSchoolIndex() - yuwenGradeRank));
        // 语文归一化后的率值
        float yuwenRate = (float) yuwenGradeRank / (float)yuwenGradeExamCoversionTotal.size();
        equilibriumDifferenceMap.put("语文差值", df.format(gradeRate - yuwenRate));
        yuwenMap.put("currentRate",  df.format(language) + "%");  // 本次率值
        yuwenMap.put("title", "语文");


        contributionRate.put("数学", df.format(math) + "%");
        List<String> shuxueGradeExamCoversionTotal =  examCoversionTotalDao.findByShuxueScore(examType);
        int shuxueGradeRank = shuxueGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getShuxueScore().toString())) + 1;
//        equilibriumDifferenceMap.put("数学差值", (int) (examCoversionTotal.getSchoolIndex() - shuxueGradeRank ));
        //数学归一化后台率值
        float shuxueRate = (float) shuxueGradeRank / (float) shuxueGradeExamCoversionTotal.size();
        equilibriumDifferenceMap.put("数学差值", df.format(gradeRate - shuxueRate));
        shuxueMap.put("currentRate",  df.format(math) + "%");  // 本次率值
        shuxueMap.put("title", "数学");


        contributionRate.put("英语", df.format(english) + "%");
        List<String> yingyuGradeExamCoversionTotal =  examCoversionTotalDao.findByYingyuScore(examType);
        int yingyuGradeRank = yingyuGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getYingyuScore().toString())) + 1;
//        equilibriumDifferenceMap.put("英语差值", (int) (examCoversionTotal.getSchoolIndex() - yingyuGradeRank ));
        //英语率值
        float yingyuRate = (float) yingyuGradeRank / (float) yingyuGradeExamCoversionTotal.size();
        equilibriumDifferenceMap.put("英语差值", df.format(gradeRate - yingyuRate));
        yingyuMap.put("currentRate",  df.format(english) + "%");  // 本次率值
        yingyuMap.put("title", "英语");


        if (!examCoversionTotal.getWuliCoversion().toString().equals("0.0")){
            contributionRate.put("物理", df.format(examCoversionTotal.getWuliCoversion() / examCoversionTotal.getCoversionTotal()) + "%");
            wuliMap.put("currentRate",  df.format(examCoversionTotal.getWuliCoversion() / examCoversionTotal.getCoversionTotal()) + "%");
            wuliMap.put("title", "物理");
            List<String> wuliGradeExamCoversionTotal =  examCoversionTotalDao.findByWuliCoversion(examType);
            int wuliGradeRank = wuliGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getWuliCoversion().toString())) + 1;
//            equilibriumDifferenceMap.put("物理差值", (int) (examCoversionTotal.getSchoolIndex() - wuliGradeRank ));
            //选考物理的年级总人数
            int wuliSum = examCoversionTotalDao.countByExamTypeAndWuli(examType);
            //物理率值
            float wuliRate = (float) wuliGradeRank /  (float) wuliSum;
            equilibriumDifferenceMap.put("物理差值", df.format(gradeRate - wuliRate));
            logger.info("物理年级排名：{}",wuliGradeRank);
        }
        if (!examCoversionTotal.getHuaxueCoversion().toString().equals("0.0")){
            contributionRate.put("化学", df.format(examCoversionTotal.getHuaxueCoversion() /examCoversionTotal.getCoversionTotal()) + "%");
            huaxueMap.put("currentRate",  df.format(examCoversionTotal.getHuaxueCoversion() /examCoversionTotal.getCoversionTotal()) + "%");//本次率值
            huaxueMap.put("title", "化学");
            List<String> huaxueGradeExamCoversionTotal =  examCoversionTotalDao.findByHuaxueCoversion(examType);
            int huaxueGradeRank = huaxueGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getHuaxueCoversion().toString())) + 1;
//            equilibriumDifferenceMap.put("化学差值", (int) (examCoversionTotal.getSchoolIndex()) - huaxueGradeRank );
            //化学选考年级总人数
            int huaxueSum = examCoversionTotalDao.countByExamTypeAndHuaxue(examType);
            //化学率值
            float huaxueRate = (float) huaxueGradeRank / (float) huaxueSum;
            equilibriumDifferenceMap.put("化学差值", df.format(gradeRate - huaxueRate) );
            logger.info("化学年级排名：{}",huaxueGradeRank);
        }
        if (!examCoversionTotal.getShengwuCoversion().toString().equals("0.0")){
            contributionRate.put("生物", df.format(examCoversionTotal.getShengwuCoversion() / examCoversionTotal.getCoversionTotal()) + "%");
            shengwuMap.put("currentRate",  df.format(examCoversionTotal.getShengwuCoversion() / examCoversionTotal.getCoversionTotal()) + "%");//本次率值
            shengwuMap.put("title", "生物");
            List<String> shengwuGradeExamCoversionTotal =  examCoversionTotalDao.findByShengwuCoversion(examType);
            int shengwuGradeRank = shengwuGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getShengwuCoversion().toString())) + 1;
//            equilibriumDifferenceMap.put("生物差值", (int) (examCoversionTotal.getSchoolIndex() - shengwuGradeRank ));
            //生物选考总人数
            int shengwuSum = examCoversionTotalDao.countByExamTypeAndShengwu(examType);
            // 生物率值
            float shengwuRate = (float) shengwuGradeRank / (float) shengwuSum;
            equilibriumDifferenceMap.put("生物差值", df.format(gradeRate - shengwuRate));
            logger.info("生物年级排名：{}",shengwuGradeRank);
        }
        if (!examCoversionTotal.getLishiCoversion().toString().equals("0.0") ){
            contributionRate.put("历史",df.format(examCoversionTotal.getLishiCoversion() / examCoversionTotal.getCoversionTotal()) + "%");
            lishiMap.put("currentRate", df.format(examCoversionTotal.getLishiCoversion() / examCoversionTotal.getCoversionTotal()) + "%");//本次率值
            lishiMap.put("title", "历史");
            List<String> lishiGradeExamCoversionTotal =  examCoversionTotalDao.findByLishiCoversion(examType);
            int lishiGradeRank = lishiGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getLishiCoversion().toString())) + 1;
//            equilibriumDifferenceMap.put("历史差值", (int) (examCoversionTotal.getSchoolIndex()) - lishiGradeRank );
            //历史选考总人数
            int lishiSum = examCoversionTotalDao.countByExamTypeAndLishi(examType);
            //历史率值
            float lishiRate = (float) lishiGradeRank / (float) lishiSum;
            equilibriumDifferenceMap.put("历史差值", df.format(gradeRate - lishiRate) );
            logger.info("历史年级排名：{}",lishiGradeRank);
        }
        if (!examCoversionTotal.getDiliCoversion().toString().equals("0.0")){
            contributionRate.put("地理", df.format(examCoversionTotal.getDiliCoversion() / examCoversionTotal.getCoversionTotal()) + "%");
            diliMap.put("currentRate",df.format(examCoversionTotal.getDiliCoversion() / examCoversionTotal.getCoversionTotal()) + "%");//本次率值
            diliMap.put("title", "地理");
            List<String> diliGradeExamCoversionTotal =  examCoversionTotalDao.findByDiliCoversion(examType);
            int diliGradeRank = diliGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getDiliCoversion().toString())) + 1;
//            equilibriumDifferenceMap.put("地理差值", (int) (examCoversionTotal.getSchoolIndex()) - diliGradeRank );
            //地理选考总人数
            int diliSum = examCoversionTotalDao.countByExamTypeAndDili(examType);
            //地理率值
            float diliRate = (float) diliGradeRank / (float) diliSum;
            equilibriumDifferenceMap.put("地理差值", df.format(gradeRate - diliRate));
            logger.info("地理年级排名：{}",diliGradeRank);
        }
        if (!examCoversionTotal.getZhengzhiCoversion().toString().equals("0.0")){
            contributionRate.put("政治", df.format(examCoversionTotal.getZhengzhiCoversion() / examCoversionTotal.getCoversionTotal()) + "%");
            zhengzhiMap.put("currentRate",df.format(examCoversionTotal.getZhengzhiCoversion() / examCoversionTotal.getCoversionTotal()) + "%");//本次率值
            zhengzhiMap.put("title", "政治");
            List<String> zhengzhiGradeExamCoversionTotal =  examCoversionTotalDao.findByZhengzhiCoversion(examType);
            int zhengzhiGradeRank = zhengzhiGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getZhengzhiCoversion().toString())) + 1;
//            equilibriumDifferenceMap.put("政治差值", (int) (examCoversionTotal.getSchoolIndex() - zhengzhiGradeRank ));
            //政治选考总人数
            int zhengzhiSum = examCoversionTotalDao.countByExamTypeAndZhegnzhi(examType);
            //政治率值
            float zhengzhiRate = (float) zhengzhiGradeRank / (float) zhengzhiSum;
            equilibriumDifferenceMap.put("政治差值", df.format(gradeRate - zhengzhiRate));
            logger.info("政治年级排名：{}",zhengzhiGradeRank);
        }
        logger.info("语文年级排名：{}",yuwenGradeRank);
        logger.info("数学年级排名：{}",shuxueGradeRank);
        logger.info("英语年级排名：{}",yingyuGradeRank);

        // 获取所有考试名称
        List<ExamInfo> examInfoList = examInfoDao.findAll();
        if (examInfoList == null || examInfoList.size() == 0) {
            info = "查无结果";
            logger.error(info);
            throw new ScoreException(ResultEnum.DATABASE_OP_EXCEPTION, info);
        }

        List<String> allExamName = examInfoDao.getAllExamName();
        String oldExamType = null;
        for (int i = 0; i < examInfoList.size(); i++) {
            if (examType.equals(examInfoList.get(0).getExamName())) {
                info = "本次为首次考试，暂无上次学科贡献率";
                logger.info(info);
                List<SubjectAnalysisDTO> list = new ArrayList<>();
                SubjectAnalysisDTO subjectAnalysisDTO = new SubjectAnalysisDTO();
                subjectAnalysisDTO.setExamCoversionTotal(examCoversionTotal);
                subjectAnalysisDTO.setContributionRate(contributionRate);
                subjectAnalysisDTO.setEquilibriumDifference(equilibriumDifferenceMap);
                subjectAnalysisDTO.setGradeRate(df.format(gradeRate));
                // 本次为首次考试
                map.put("yuwenMap", yuwenMap);
                map.put("shuxueMap",shuxueMap);
                map.put("yingyuMap",yingyuMap);
                if (wuliMap.size() != 0){
                    map.put("wuliMap",wuliMap);
                }
                if (zhengzhiMap.size() != 0){
                    map.put("zhengzhiMap",zhengzhiMap);
                }

                if (huaxueMap.size() != 0){
                    map.put("huaxueMap",huaxueMap);
                }
                if (lishiMap.size() != 0){
                    map.put("lishiMap",lishiMap);
                }

                if (shengwuMap.size() != 0){
                    map.put("shengwuMap",shengwuMap);
                }
                if (diliMap.size() != 0){
                    map.put("diliMap",diliMap);
                }
                subjectAnalysisDTO.setMap(map);

                list.add(subjectAnalysisDTO);
                return list;
            }else  if (examType.equals(examInfoList.get(i).getExamName())) {
                oldExamType = examInfoList.get(i - 1).getExamName();
                logger.info("求平均贡献率的第三次考试名称： {}", oldExamType);
            }
        }
        //上次考试的成绩
        ExamCoversionTotal oldexamCoversionTotal = examCoversionTotalDao.findByStudentNumberAndExamType(stuNumber, oldExamType);
        if (null == oldexamCoversionTotal) {
            info = "查询上次考试此学生的所有信息失败";
            logger.error(info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        // 上次 各单科与总分的比值，即本次考试的学科贡献率
        Map<String, String> oldcontributionRate = new HashMap<>();
        double oldlanguage, // 语文
                oldmath, // 数学
                oldenglish, // 英语
                oldphysical = 0, // 物理
                oldchemistry = 0, //化学
                oldbiological = 0, // 生物
                oldpolitical = 0, //政治
                oldhistory = 0, // 历史
                oldgeography = 0; //地理
        oldlanguage = oldexamCoversionTotal.getYuwenScore()/ oldexamCoversionTotal.getCoversionTotal();
        oldmath =oldexamCoversionTotal.getShuxueScore() /oldexamCoversionTotal.getCoversionTotal();
        oldenglish = oldexamCoversionTotal.getYingyuScore() /oldexamCoversionTotal.getCoversionTotal();
        oldcontributionRate.put("语文", df.format(oldlanguage) + "%");
        yuwenMap.put("lastRate", df.format(oldlanguage) + "%"); //上次率值

        oldcontributionRate.put("数学", df.format(oldmath) + "%");
        shuxueMap.put("lastRate", df.format(oldmath) + "%"); // 上次率值

        oldcontributionRate.put("英语", df.format(oldenglish) + "%");
        yingyuMap.put("lastRate", df.format(oldenglish) + "%");// 上次率值
        if (!oldexamCoversionTotal.getWuliCoversion().toString().equals("0.0")) {
            oldphysical = oldexamCoversionTotal.getWuliCoversion() / oldexamCoversionTotal.getCoversionTotal();
            oldcontributionRate.put("物理", df.format(oldphysical) + "%");
            wuliMap.put("lastRate", df.format(oldphysical) + "%"); //上次率值
        }
        if (!oldexamCoversionTotal.getHuaxueCoversion().toString().equals("0.0")) {
            oldchemistry = oldexamCoversionTotal.getHuaxueCoversion() / oldexamCoversionTotal.getCoversionTotal();
            oldcontributionRate.put("化学", df.format(oldchemistry) + "%");
            huaxueMap.put("lastRate", df.format(oldchemistry) + "%"); //上次率值
        }
        if (!oldexamCoversionTotal.getShengwuCoversion().toString().equals("0.0")) {
            oldbiological = oldexamCoversionTotal.getShengwuCoversion() / oldexamCoversionTotal.getCoversionTotal();
            oldcontributionRate.put("生物", df.format(oldbiological) + "%");
            shengwuMap.put("lastRate", df.format(oldbiological) + "%"); //上次率值
        }
        if (!oldexamCoversionTotal.getZhengzhiCoversion().toString().equals("0.0")) {
            oldpolitical = oldexamCoversionTotal.getZhengzhiCoversion() / oldexamCoversionTotal.getCoversionTotal();
            oldcontributionRate.put("政治", df.format(oldpolitical) + "%");
            zhengzhiMap.put("lastRate", df.format(oldpolitical) + "%"); //上次率值
        }
        if (!oldexamCoversionTotal.getLishiCoversion().toString().equals("0.0")) {
            oldhistory = oldexamCoversionTotal.getLishiCoversion() / oldexamCoversionTotal.getCoversionTotal();
            oldcontributionRate.put("历史", df.format(oldhistory) + "%");
            lishiMap.put("lastRate", df.format(oldhistory) + "%"); //上次率值
        }
        if (!oldexamCoversionTotal.getDiliCoversion().toString().equals("0.0")) {
            oldgeography = oldexamCoversionTotal.getDiliCoversion() / oldexamCoversionTotal.getCoversionTotal();
            oldcontributionRate.put("地理", df.format(oldgeography) + "%");
            diliMap.put("lastRate", df.format(oldgeography) + "%"); //上次率值
        }
        // 判断当前的考试下标在数组中是否是 属于第四次考试，和所有考试次数是否大于 4次
        if ( allExamName.indexOf(examType) < 3 || examInfoList.size() <= 4){
            info = "本次不是首次考试，但考试次数不够四次，暂无前三次的学科贡献率的均值情况";
            logger.info(info);
            List<SubjectAnalysisDTO> list = new ArrayList<>();
            SubjectAnalysisDTO subjectAnalysisDTO = new SubjectAnalysisDTO();
            subjectAnalysisDTO.setExamCoversionTotal(examCoversionTotal);
            subjectAnalysisDTO.setContributionRate(contributionRate);
            subjectAnalysisDTO.setEquilibriumDifference(equilibriumDifferenceMap);
            subjectAnalysisDTO.setGradeRate(df.format(gradeRate));
//            // 上次的考试的学科贡献率
//            subjectAnalysisDTO.setOldcontributionRate(oldcontributionRate);
            map.put("yuwenMap", yuwenMap);
            map.put("shuxueMap",shuxueMap);
            map.put("yingyuMap",yingyuMap);
            if (wuliMap.size() != 0){
                map.put("wuliMap",wuliMap);
            }
            if (zhengzhiMap.size() != 0){
                map.put("zhengzhiMap",zhengzhiMap);
            }

            if (huaxueMap.size() != 0){
                map.put("huaxueMap",huaxueMap);
            }
            if (lishiMap.size() != 0){
                map.put("lishiMap",lishiMap);
            }

            if (shengwuMap.size() != 0){
                map.put("shengwuMap",shengwuMap);
            }
            if (diliMap.size() != 0){
                map.put("diliMap",diliMap);
            }
            subjectAnalysisDTO.setMap(map);
            list.add(subjectAnalysisDTO);
            return list;
        }

        //求前三次各科贡献率平均值的，第二次考试
        String avgTwoExamType = null;
        //求前三次各科贡献率平均值的，第一次考试
        String avgFirstExamType = null;
        for (int i = 0; i < examInfoList.size(); i++) {
          if (examType.equals(examInfoList.get(i).getExamName())) {
              avgTwoExamType = examInfoList.get(i-2).getExamName();
              avgFirstExamType = examInfoList.get(i - 3).getExamName();
              logger.info("求平均贡献率的第一次考试名称： {}", avgFirstExamType);
              logger.info("求平均贡献率的第二次考试名称： {}", avgTwoExamType);
            }
        }
        //求前三次各科贡献率平均值的，第二次考试
        ExamCoversionTotal avgTwoexamCoversionTotal = examCoversionTotalDao.findByStudentNumberAndExamType(stuNumber, avgTwoExamType);
        if (null == avgTwoexamCoversionTotal) {
            info = "前三次各科贡献率平均值的，第二次考试,此学生的所有信息失败";
            logger.error(info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        // 前三次 学科贡献率的平均值
        Map<String, String> avgcontributionRate = new HashMap<>();
        double avgTwolanguage, // 语文
                avgTwomath, // 数学
                avgTwoenglish, // 英语
                avgTwophysical = 0, // 物理
                avgTwochemistry = 0, //化学
                avgTwobiological = 0, // 生物
                avgTwopolitical = 0, //政治
                avgTwohistory = 0, // 历史
                avgTwogeography = 0; //地理
        avgTwolanguage = avgTwoexamCoversionTotal.getYuwenScore()/ avgTwoexamCoversionTotal.getCoversionTotal();
        avgTwomath =avgTwoexamCoversionTotal.getShuxueScore() /avgTwoexamCoversionTotal.getCoversionTotal();
        avgTwoenglish = avgTwoexamCoversionTotal.getYingyuScore() /avgTwoexamCoversionTotal.getCoversionTotal();

        if (!avgTwoexamCoversionTotal.getWuliCoversion().toString().equals("0.0")) {
            avgTwophysical = avgTwoexamCoversionTotal.getWuliCoversion() / avgTwoexamCoversionTotal.getCoversionTotal();

        }
        if (!avgTwoexamCoversionTotal.getHuaxueCoversion().toString().equals("0.0")) {
            avgTwochemistry = avgTwoexamCoversionTotal.getHuaxueCoversion() / avgTwoexamCoversionTotal.getCoversionTotal();

        }
        if (!avgTwoexamCoversionTotal.getShengwuCoversion().toString().equals("0.0")) {
            avgTwobiological = avgTwoexamCoversionTotal.getShengwuCoversion() / avgTwoexamCoversionTotal.getCoversionTotal();

        }
        if (!avgTwoexamCoversionTotal.getZhengzhiCoversion().toString().equals("0.0")) {
            avgTwopolitical = avgTwoexamCoversionTotal.getZhengzhiCoversion() / avgTwoexamCoversionTotal.getCoversionTotal();

        }
        if (!avgTwoexamCoversionTotal.getLishiCoversion().toString().equals("0.0")) {
            avgTwohistory = avgTwoexamCoversionTotal.getLishiCoversion() / avgTwoexamCoversionTotal.getCoversionTotal();

        }
        if (!avgTwoexamCoversionTotal.getDiliCoversion().toString().equals("0.0")) {
            avgTwogeography = avgTwoexamCoversionTotal.getDiliCoversion() / avgTwoexamCoversionTotal.getCoversionTotal();

        }
        ExamCoversionTotal avgFirstexamCoversionTotal = examCoversionTotalDao.findByStudentNumberAndExamType(stuNumber, avgFirstExamType);
        if (null == avgFirstexamCoversionTotal) {
            info = "前三次各科贡献率平均值的，第一次考试,此学生的所有信息失败";
            logger.error(info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        double avgFirstlanguage, // 语文
                avgFirstmath, // 数学
                avgFirstenglish, // 英语
                avgFirstphysical = 0, // 物理
                avgFirstchemistry = 0, //化学
                avgFirstbiological = 0, // 生物
                avgFirstpolitical = 0, //政治
                avgFirsthistory = 0, // 历史
                avgFirstgeography = 0; //地理
        avgFirstlanguage = avgFirstexamCoversionTotal.getYuwenScore()/ avgFirstexamCoversionTotal.getCoversionTotal();
        avgFirstmath =avgFirstexamCoversionTotal.getShuxueScore() /avgFirstexamCoversionTotal.getCoversionTotal();
        avgFirstenglish = avgFirstexamCoversionTotal.getYingyuScore() /avgFirstexamCoversionTotal.getCoversionTotal();
        if (!avgFirstexamCoversionTotal.getWuliCoversion().toString().equals("0.0")) {
            avgFirstphysical = avgFirstexamCoversionTotal.getWuliCoversion() / avgFirstexamCoversionTotal.getCoversionTotal();
        }
        if (!avgFirstexamCoversionTotal.getHuaxueCoversion().toString().equals("0.0")) {
            avgFirstchemistry = avgFirstexamCoversionTotal.getHuaxueCoversion() / avgFirstexamCoversionTotal.getCoversionTotal();
        }
        if (!avgFirstexamCoversionTotal.getShengwuCoversion().toString().equals("0.0")) {
            avgFirstbiological = avgFirstexamCoversionTotal.getShengwuCoversion() / avgFirstexamCoversionTotal.getCoversionTotal();
        }
        if (!avgFirstexamCoversionTotal.getZhengzhiCoversion().toString().equals("0.0")) {
            avgFirstpolitical = avgFirstexamCoversionTotal.getZhengzhiCoversion() / avgFirstexamCoversionTotal.getCoversionTotal();
        }
        if (!avgFirstexamCoversionTotal.getLishiCoversion().toString().equals("0.0")) {
            avgFirsthistory = avgFirstexamCoversionTotal.getLishiCoversion() / avgFirstexamCoversionTotal.getCoversionTotal();
        }
        if (!avgFirstexamCoversionTotal.getDiliCoversion().toString().equals("0.0")) {
            avgFirstgeography = avgFirstexamCoversionTotal.getDiliCoversion() / avgFirstexamCoversionTotal.getCoversionTotal();
        }
        double avgyuwen = (oldlanguage+avgFirstlanguage+avgTwolanguage) / 3 ; // 语文平均贡献率
        double avgshuxue = (oldmath+avgFirstmath+avgTwomath) / 3;  //数学平均贡献率
        double avgyingyu = (oldenglish+avgFirstenglish+avgTwoenglish) / 3;//英语平均贡献率
        // 前三次考试的 各单科的学科贡献率的平均值
        avgcontributionRate.put("语文平均贡献率", df.format(avgyuwen) + "%");
        avgcontributionRate.put("数学平均贡献率", df.format(avgshuxue) + "%");
        avgcontributionRate.put("英语平均贡献率", df.format(avgyingyu ) + "%");
        yuwenMap.put("averageRate",df.format(avgyuwen) + "%"); //语文平均率值
        yuwenMap.put("rateDifference",df.format(language - avgyuwen ));// 率值差： 本次 - 平均率值
        yuwenMap.put("title", "语文");
        shuxueMap.put("averageRate",df.format(avgshuxue) + "%"); //数学平均率值
        shuxueMap.put("rateDifference",df.format(math - avgshuxue ));// 率值差： 本次 - 平均率值
        shuxueMap.put("title", "数学");
        yingyuMap.put("averageRate", df.format(avgyingyu ) + "%");//英语平均贡献率
        yingyuMap.put("rateDifference",df.format(english - avgyingyu));// 率值差： 本次 - 平均率值
        yingyuMap.put("title", "英语");

        double avgwuli = (oldphysical + avgFirstphysical + avgTwophysical) /3 ;
        double avghuaxue = (oldchemistry + avgFirstchemistry + avgTwochemistry) / 3;
        double avgshengwu = (oldbiological + avgFirstbiological + avgTwobiological) /3;
        double avgzhengzhi = (oldpolitical + avgFirstpolitical +avgTwopolitical) / 3;
        double avglishi = ( oldhistory + avgFirsthistory + avgTwohistory) / 3;
        double avgdili = (oldgeography + avgFirstgeography + avgTwogeography) / 3;
        if (avgwuli != 0){
            avgcontributionRate.put("物理平均贡献率", df.format( avgwuli ) + "%");
            wuliMap.put("title", "物理");
            wuliMap.put("averageRate", df.format(avgwuli ) + "%"); //物理平均率值
            wuliMap.put("rateDifference",df.format(examCoversionTotal.getWuliCoversion() / examCoversionTotal.getCoversionTotal() - avgwuli));// 率值差： 本次 - 平均率值
        }
        if (avghuaxue != 0){
            avgcontributionRate.put("化学平均贡献率", df.format( avghuaxue ) + "%");
            huaxueMap.put("title", "化学");
            huaxueMap.put("averageRate", df.format(avghuaxue ) + "%");
            huaxueMap.put("rateDifference",df.format(examCoversionTotal.getHuaxueCoversion() /examCoversionTotal.getCoversionTotal() - avghuaxue));// 率值差： 本次 - 平均率值
        }
        if (avgshengwu != 0){
            avgcontributionRate.put("生物平均贡献率", df.format( avgshengwu ) + "%");
            shengwuMap.put("title", "生物");
            shengwuMap.put("averageRate", df.format(avgshengwu ) + "%");
            shengwuMap.put("rateDifference",df.format(examCoversionTotal.getShengwuCoversion() / examCoversionTotal.getCoversionTotal() - avgshengwu));// 率值差： 本次 - 平均率值
        }
        if (avgzhengzhi != 0){
            avgcontributionRate.put("政治平均贡献率", df.format( avgzhengzhi ) + "%");
            zhengzhiMap.put("title", "政治");
            zhengzhiMap.put("averageRate", df.format(avgzhengzhi ) + "%");
            zhengzhiMap.put("rateDifference",df.format(examCoversionTotal.getZhengzhiCoversion() / examCoversionTotal.getCoversionTotal() - avgzhengzhi));// 率值差： 本次 - 平均率值
        }
        if (avglishi != 0 ){
            avgcontributionRate.put("历史平均贡献率", df.format( avglishi ) + "%");
            lishiMap.put("title", "历史");
            lishiMap.put("averageRate", df.format(avglishi ) + "%");
            lishiMap.put("rateDifference",df.format(examCoversionTotal.getLishiCoversion() / examCoversionTotal.getCoversionTotal() - avglishi));// 率值差： 本次 - 平均率值
        }
        if (avgdili != 0 ){
            avgcontributionRate.put("地理平均贡献率", df.format( avgdili ) + "%");
            diliMap.put("title", "地理");
            diliMap.put("averageRate", df.format(avgdili ) + "%");
            diliMap.put("rateDifference",df.format(examCoversionTotal.getDiliCoversion() / examCoversionTotal.getCoversionTotal() - avgdili));// 率值差： 本次 - 平均率值
        }
        List<SubjectAnalysisDTO> list = new ArrayList<>();
        SubjectAnalysisDTO subjectAnalysisDTO = new SubjectAnalysisDTO();
        subjectAnalysisDTO.setExamCoversionTotal(examCoversionTotal);
        subjectAnalysisDTO.setContributionRate(contributionRate);
        subjectAnalysisDTO.setEquilibriumDifference(equilibriumDifferenceMap);
        subjectAnalysisDTO.setGradeRate(df.format(gradeRate));
//        // 上次的考试的学科贡献率
////        subjectAnalysisDTO.setOldcontributionRate(oldcontributionRate);
////        //前三次考试的 各科学科贡献率的平均值
////        subjectAnalysisDTO.setAvgcontributionRate(avgcontributionRate);
        map.put("yuwenMap", yuwenMap);
        map.put("shuxueMap",shuxueMap);
        map.put("yingyuMap",yingyuMap);
        if (wuliMap.size() != 0){
            map.put("wuliMap",wuliMap);
        }
        if (zhengzhiMap.size() != 0){
            map.put("zhengzhiMap",zhengzhiMap);
        }

        if (huaxueMap.size() != 0){
            map.put("huaxueMap",huaxueMap);
        }
        if (lishiMap.size() != 0){
            map.put("lishiMap",lishiMap);
        }

        if (shengwuMap.size() != 0){
            map.put("shengwuMap",shengwuMap);
        }
        if (diliMap.size() != 0){
            map.put("diliMap",diliMap);
        }
        subjectAnalysisDTO.setMap(map);
        list.add(subjectAnalysisDTO);
        return list;
    }


    @Override
    public List<HistoricalAnalysisTotalDTO> getHistoricalAnalysisTotalInfo(String stuNumber, String examType) {
        ExamCoversionTotal examCoversionTotal = examCoversionTotalDao.findByStudentNumberAndExamType(stuNumber, examType);
        if (null == examCoversionTotal) {
            info = "查询此学生的所有信息失败";
            logger.error(info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        // 年级总人数
        int gradeNumber = examCoversionTotalDao.countByExamType(examType);
        // 班级总人数
        int classNumber = examCoversionTotalDao.countByClassIdAndExamType(examCoversionTotal.getClassId(), examType);
        float gradeAveragePercentage = Float.parseFloat(examCoversionTotal.getSchoolIndex().toString()) / gradeNumber;
        float classAveragePercentage = Float.parseFloat(examCoversionTotal.getClassIndex().toString())  / classNumber;

        float classSum = examCoversionTotalDao.sumCoversionTotalByClassIdAndExamType(examCoversionTotal.getClassId(), examType);
        float classAverage = classSum / classNumber; // 班級平均分
        float gradeSum = examCoversionTotalDao.sumCoversionTotalByExamType(examType);
        float gradeAverage = gradeSum / gradeNumber;

        int examTnfoId = examInfoDao.findByExamName(examType);
        SubjectFullScore subjectFullScore = subjectFullScoreDao.findById(examTnfoId);
        // 本次考试的全科总分
        int sum = Math.toIntExact(subjectFullScore.getYuwen() + subjectFullScore.getShuxue() + subjectFullScore.getYingyu() + subjectFullScore.getWuli() + subjectFullScore.getHuaxue()
                + subjectFullScore.getShengwu() + subjectFullScore.getZhengzhi() + subjectFullScore.getDili() + subjectFullScore.getLishi()) - 300;



        //保留两位小数
        DecimalFormat df = new DecimalFormat("#0.00");
        // 封装dto，传输给controller并显示给前台渲染
        List<HistoricalAnalysisTotalDTO> list = new ArrayList<>();
        HistoricalAnalysisTotalDTO historicalAnalysisTotalDTO = new HistoricalAnalysisTotalDTO();

        historicalAnalysisTotalDTO.setExamCoversionTotal(examCoversionTotal);
        historicalAnalysisTotalDTO.setGradePercentage(df.format(gradeAveragePercentage));// 年级排名百分率
        historicalAnalysisTotalDTO.setClassPercentage(df.format(classAveragePercentage)); //班级排名百分率
        historicalAnalysisTotalDTO.setClassAverage(df.format(classAverage));// 班级平均分
        historicalAnalysisTotalDTO.setGradeAverage(df.format(gradeAverage));// 年级平均分
        historicalAnalysisTotalDTO.setClassAveragePercentage(df.format(classAverage / sum));// 班级平均分百分率
        historicalAnalysisTotalDTO.setGradeAveragePercentage(df.format(gradeAverage / sum)); // 年级平均分百分率
        historicalAnalysisTotalDTO.setTotalScorePercentage(df.format(examCoversionTotal.getCoversionTotal() / sum));// 总分百分率

        list.add(historicalAnalysisTotalDTO);
        return list;
    }

    @Override
    public List<HistoricalAnalysisSingleDTO> getHistoricalAnalysisSingleInfo(String stuNumber, String examType, String subject) {
        ExamCoversionTotal examCoversionTotal = examCoversionTotalDao.findByStudentNumberAndExamType(stuNumber, examType);
        if (null == examCoversionTotal) {
            info = "查询此学生的所有信息失败";
            logger.error(info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        //使用原生SQL
        EntityManager em = ntityManagerFactory.createEntityManager();
        // 本次班级排名
        String querysql = "SELECT * FROM exam_coversion_total WHERE class_id='"+examCoversionTotal.getClassId()+ "' AND exam_type='"+examType+"' ORDER BY "+ subject + " DESC"; //这个是直接拼接
        logger.info("查询本次班级排名-->" + querysql);
        Query query = em.createNativeQuery(querysql, ExamCoversionTotal.class);
        @SuppressWarnings("unchecked")
        List<ExamCoversionTotal> examCoversionTotalSubject = query.getResultList();
        //单科班级总分
        String singleClassQuerysql = "SELECT SUM("+subject+") FROM exam_coversion_total WHERE class_id='"+examCoversionTotal.getClassId()+"' AND exam_type='"+examType+"'";
        logger.info("查询本次单科班级总分-->" + singleClassQuerysql);
        Query singleClassQuery = em.createNativeQuery(singleClassQuerysql);
        List<Double> singleClassList = singleClassQuery.getResultList();// 单科班级总分

        //单科年级总分
        String singleGradeQuerysql = "SELECT SUM("+subject+") FROM exam_coversion_total WHERE exam_type='"+examType+"'";
        logger.info("查询本次单科班级总分-->" + singleGradeQuerysql);
        Query singleGradeQuery = em.createNativeQuery(singleGradeQuerysql);
        List<Double> singleGradeList = singleGradeQuery.getResultList();// 单科班级总分
        // 单科年级总分
        em.close();

        Map<String, Integer> mapClass = new HashMap<>();
        for(int i = 1; i < examCoversionTotalSubject.size(); i++){
            if (subject.equals("yuwen_score")){
                mapClass.put(examCoversionTotalSubject.get(0).getStudentNumber(),1);
                if (examCoversionTotalSubject.get(i-1).getYuwenScore().equals(examCoversionTotalSubject.get(i).getYuwenScore())){
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i - 1);
                }else {
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("shuxue_score")){
                mapClass.put(examCoversionTotalSubject.get(0).getStudentNumber(),1);
                if (examCoversionTotalSubject.get(i-1).getShuxueScore().equals(examCoversionTotalSubject.get(i).getShuxueScore())){
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i - 1);
                }else {
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("yingyu_score")){
                mapClass.put(examCoversionTotalSubject.get(0).getStudentNumber(),1);
                if (examCoversionTotalSubject.get(i-1).getYingyuScore().equals(examCoversionTotalSubject.get(i).getYingyuScore())){
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i - 1);
                }else {
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("wuli_coversion")){
                mapClass.put(examCoversionTotalSubject.get(0).getStudentNumber(),1);
                if (examCoversionTotalSubject.get(i-1).getWuliCoversion().equals(examCoversionTotalSubject.get(i).getWuliCoversion())){
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i - 1);
                }else {
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("huaxue_coversion")){
                mapClass.put(examCoversionTotalSubject.get(0).getStudentNumber(),1);
                if (examCoversionTotalSubject.get(i-1).getHuaxueCoversion().equals(examCoversionTotalSubject.get(i).getHuaxueCoversion())){
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i - 1);
                }else {
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("shengwu_coversion")){
                mapClass.put(examCoversionTotalSubject.get(0).getStudentNumber(),1);
                if (examCoversionTotalSubject.get(i-1).getShengwuCoversion().equals(examCoversionTotalSubject.get(i).getShengwuCoversion())){
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i - 1);
                }else {
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("lishi_coversion")){
                mapClass.put(examCoversionTotalSubject.get(0).getStudentNumber(),1);
                if (examCoversionTotalSubject.get(i-1).getLishiCoversion().equals(examCoversionTotalSubject.get(i).getLishiCoversion())){
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i - 1);
                }else {
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("dili_coversion")){
                mapClass.put(examCoversionTotalSubject.get(0).getStudentNumber(),1);
                if (examCoversionTotalSubject.get(i-1).getDiliCoversion().equals(examCoversionTotalSubject.get(i).getDiliCoversion())){
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i - 1);
                }else {
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i + 1);
                }
            }else if (subject.equals("zhengzhi_coversion")){
                mapClass.put(examCoversionTotalSubject.get(0).getStudentNumber(),1);
                if (examCoversionTotalSubject.get(i-1).getZhengzhiCoversion().equals(examCoversionTotalSubject.get(i).getZhengzhiCoversion())){
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i - 1);
                }else {
                    mapClass.put(examCoversionTotalSubject.get(i).getStudentNumber(), i + 1);
                }
            }
        }

        //保留两位小数
        DecimalFormat df = new DecimalFormat("#0.00");
        // 年级总人数
        int gradeNumber = examCoversionTotalDao.countByExamType(examType);
        // 班级总人数
        int classNumber = examCoversionTotalDao.countByClassIdAndExamType(examCoversionTotal.getClassId(), examType);
        double classAverage =Double.parseDouble(singleClassList.get(0).toString()) / classNumber; // 班級平均分
        double gradeAverage =Double.parseDouble(singleGradeList.get(0).toString()) / gradeNumber; // 年级平均分

        int examTnfoId = examInfoDao.findByExamName(examType);
        SubjectFullScore subjectFullScore = subjectFullScoreDao.findById(examTnfoId);
//        // 本次考试的全科总分
//        int sum = Math.toIntExact(subjectFullScore.getYuwen() + subjectFullScore.getShuxue() + subjectFullScore.getYingyu() + subjectFullScore.getWuli() + subjectFullScore.getHuaxue()
//                + subjectFullScore.getShengwu() + subjectFullScore.getZhengzhi() + subjectFullScore.getDili() + subjectFullScore.getLishi()) - 300;
        int gradeRank = 0;
        double singleScorePercentage = 0; // 单科平均分百分率
        double classAveragePercentage = 0; //班级平均分百分率
        double gradeAveragePercentage = 0; // 年级平均分百分率
        if (subject.equals("yuwen_score")){
            List<String> yuwenGradeExamCoversionTotal =  examCoversionTotalDao.findByYuwenScore(examType);
            // 年级排名
            gradeRank = yuwenGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getYuwenScore().toString())) + 1;
            singleScorePercentage = examCoversionTotal.getYuwenScore() / subjectFullScore.getYuwen();
            classAveragePercentage = (float) classAverage/ subjectFullScore.getYuwen();
            gradeAveragePercentage = (float) gradeAverage / subjectFullScore.getYuwen();
        }else if (subject.equals("shuxue_score")){
            List<String> shuxueGradeExamCoversionTotal =  examCoversionTotalDao.findByShuxueScore(examType);
            // 年级排名
            gradeRank = shuxueGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getShuxueScore().toString())) + 1;
            singleScorePercentage = examCoversionTotal.getShuxueScore() / subjectFullScore.getShuxue();
            classAveragePercentage = (float) classAverage/ subjectFullScore.getShuxue();
            gradeAveragePercentage = (float) gradeAverage / subjectFullScore.getShuxue();
        }else if (subject.equals("yingyu_score")){
            List<String> yingyuGradeExamCoversionTotal =  examCoversionTotalDao.findByYingyuScore(examType);
            // 年级排名
            gradeRank = yingyuGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getYingyuScore().toString())) + 1;
            singleScorePercentage = examCoversionTotal.getYingyuScore() / subjectFullScore.getYingyu();
            classAveragePercentage = (float) classAverage/ subjectFullScore.getYingyu();
            gradeAveragePercentage = (float) gradeAverage / subjectFullScore.getYingyu();
        }else if (subject.equals("wuli_coversion")){
            List<String> wuliGradeExamCoversionTotal =  examCoversionTotalDao.findByWuliCoversion(examType);
            gradeRank = wuliGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getWuliCoversion().toString())) + 1;
            singleScorePercentage = examCoversionTotal.getWuliCoversion() / subjectFullScore.getWuli();
            classAveragePercentage = (float) classAverage/ subjectFullScore.getWuli();
            gradeAveragePercentage = (float) gradeAverage / subjectFullScore.getWuli();
        }else if (subject.equals("huaxue_coversion")){
            List<String> huaxueGradeExamCoversionTotal =  examCoversionTotalDao.findByHuaxueCoversion(examType);
            gradeRank = huaxueGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getHuaxueCoversion().toString())) + 1;
            singleScorePercentage = examCoversionTotal.getHuaxueCoversion() / subjectFullScore.getHuaxue();
            classAveragePercentage = (float) classAverage/ subjectFullScore.getHuaxue();
            gradeAveragePercentage = (float) gradeAverage / subjectFullScore.getHuaxue();
        }else if (subject.equals("shengwu_coversion")){
            List<String> shengwuGradeExamCoversionTotal =  examCoversionTotalDao.findByShengwuCoversion(examType);
            gradeRank = shengwuGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getShengwuCoversion().toString())) + 1;
            singleScorePercentage = examCoversionTotal.getShengwuCoversion() / subjectFullScore.getShengwu();
            classAveragePercentage = (float) classAverage/ subjectFullScore.getShengwu();
            gradeAveragePercentage = (float) gradeAverage / subjectFullScore.getShengwu();
        }else if (subject.equals("lishi_coversion")){
            List<String> lishiGradeExamCoversionTotal =  examCoversionTotalDao.findByLishiCoversion(examType);
            gradeRank = lishiGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getLishiCoversion().toString())) + 1;
            singleScorePercentage = examCoversionTotal.getLishiCoversion() / subjectFullScore.getLishi();
            classAveragePercentage = (float) classAverage/ subjectFullScore.getLishi();
            gradeAveragePercentage = (float) gradeAverage / subjectFullScore.getLishi();
        }else if (subject.equals("dili_coversion")){
            List<String> diliGradeExamCoversionTotal =  examCoversionTotalDao.findByDiliCoversion(examType);
            gradeRank = diliGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getDiliCoversion().toString())) + 1;
            singleScorePercentage = examCoversionTotal.getDiliCoversion() / subjectFullScore.getDili();
            classAveragePercentage = (float) classAverage/ subjectFullScore.getDili();
            gradeAveragePercentage = (float) gradeAverage / subjectFullScore.getDili();
        }else if (subject.equals("zhengzhi_coversion")){
            List<String> zhengzhiGradeExamCoversionTotal =  examCoversionTotalDao.findByZhengzhiCoversion(examType);
            gradeRank = zhengzhiGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getZhengzhiCoversion().toString())) + 1;
            singleScorePercentage = examCoversionTotal.getZhengzhiCoversion() / subjectFullScore.getZhengzhi();
            classAveragePercentage = (float) classAverage/ subjectFullScore.getZhengzhi();
            gradeAveragePercentage = (float) gradeAverage / subjectFullScore.getZhengzhi();
        }



        // 封装dto，传输给controller并显示给前台渲染
        List<HistoricalAnalysisSingleDTO> list = new ArrayList<>();
        HistoricalAnalysisSingleDTO historicalAnalysisSingleDTO = new HistoricalAnalysisSingleDTO();
        historicalAnalysisSingleDTO.setExamCoversionTotal(examCoversionTotal);
        historicalAnalysisSingleDTO.setClassRank(mapClass.get(stuNumber));//班排名
        historicalAnalysisSingleDTO.setGradeRank(gradeRank);// 年级排名
        historicalAnalysisSingleDTO.setClassPercentage(df.format(Float.parseFloat(mapClass.get(stuNumber).toString()) / classNumber)); // 班级排名百分率
        historicalAnalysisSingleDTO.setGradePercentage(df.format((float)gradeRank / gradeNumber)); // 年级排名百分率
        historicalAnalysisSingleDTO.setClassAverage(df.format(classAverage));// 班级平均分
        historicalAnalysisSingleDTO.setGradeAverage(df.format(gradeAverage));// 年级平均分
        historicalAnalysisSingleDTO.setClassAveragePercentage(df.format(classAveragePercentage)); //班级平均分百分率
        historicalAnalysisSingleDTO.setGradeAveragePercentage(df.format(gradeAveragePercentage)); // 年级平均分百分率
        historicalAnalysisSingleDTO.setSingleScorePercentage(df.format(singleScorePercentage));   // 单科分数的百分率

        list.add(historicalAnalysisSingleDTO);
        return list;
    }


    @Override
    public List<AsahiChartAllRateDTO> getAsahiChartAllRate(String stuNumber, String examType) {
        ExamCoversionTotal examCoversionTotal = examCoversionTotalDao.findByStudentNumberAndExamType(stuNumber, examType);
        if (null == examCoversionTotal) {
            info = "查询此学生的所有信息失败";
            logger.error(info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        int examTnfoId = examInfoDao.findByExamName(examType);
        SubjectFullScore subjectFullScore = subjectFullScoreDao.findById(examTnfoId);
        // 本次考试的全科总分
        int sum = Math.toIntExact(subjectFullScore.getYuwen() + subjectFullScore.getShuxue() + subjectFullScore.getYingyu() + subjectFullScore.getWuli() + subjectFullScore.getHuaxue()
                + subjectFullScore.getShengwu() + subjectFullScore.getZhengzhi() + subjectFullScore.getDili() + subjectFullScore.getLishi()) - 300;
        // 三科比率值
        double threeSubject = (examCoversionTotal.getYuwenScore()+examCoversionTotal.getShuxueScore()+examCoversionTotal.getYingyuScore()) /(subjectFullScore.getYingyu()+subjectFullScore.getShuxue()+subjectFullScore.getYingyu());

//        ImportConversionScore importConversionScore = importConversionScoreDao.findByStudentMachineCardAndExamType(examCoversionTotal.getStudentMachineCard(), examCoversionTotal.getExamType());
//        if (importConversionScore == null){
//            info = "查无此数据";
//            logger.error(info);
//            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
//        }

        // 综合分数、真实所选的科目分数之和
        double comprehensiveScore = 0.00;
        // 综合的标准满分， 真实所选的科目分数之和
        int comprehensiveStandardScore = 0;

        //保留两位小数
        DecimalFormat df = new DecimalFormat("#0.00");

        // 所有真实科目的率值，k: 科目名称；v：所对应的率值
        Map<String, String> allSubjectRateMap = new HashMap<>();
        double languageScoreRate = examCoversionTotal.getYuwenScore() / subjectFullScore.getYuwen();
        allSubjectRateMap.put("language", df.format(languageScoreRate));//语文
        double mathScoreRate = examCoversionTotal.getShuxueScore() / subjectFullScore.getShuxue();
        allSubjectRateMap.put("math",df.format(mathScoreRate));
        double englishScoreRate = examCoversionTotal.getYingyuScore() / subjectFullScore.getYingyu();
        allSubjectRateMap.put("english",df.format(englishScoreRate));

        if (!examCoversionTotal.getWuliCoversion().toString().equals("0.0")){
            comprehensiveScore += examCoversionTotal.getWuliCoversion();
            comprehensiveStandardScore += subjectFullScore.getWuli();
            double physicalScoreRate =  examCoversionTotal.getWuliCoversion() / subjectFullScore.getWuli();
            allSubjectRateMap.put("physical",df.format(physicalScoreRate));

        }
        if (!examCoversionTotal.getHuaxueCoversion().toString().equals("0.0")){
            comprehensiveScore =+ examCoversionTotal.getHuaxueCoversion();
            comprehensiveStandardScore += subjectFullScore.getHuaxue();
            double chemistryScoreRate =  examCoversionTotal.getHuaxueCoversion() / subjectFullScore.getHuaxue();
            allSubjectRateMap.put("chemistry",df.format(chemistryScoreRate));
        }
        if (!examCoversionTotal.getShengwuCoversion().toString().equals("0.0")){
            comprehensiveScore =+ examCoversionTotal.getShengwuCoversion();
            comprehensiveStandardScore += subjectFullScore.getShengwu();
            double biologicalScoreRate =  examCoversionTotal.getShengwuCoversion() / subjectFullScore.getShengwu();
            allSubjectRateMap.put("biological",df.format(biologicalScoreRate));
        }
        if (!examCoversionTotal.getLishiCoversion().toString().equals("0.0") ){
            comprehensiveScore =+ examCoversionTotal.getLishiCoversion();
            comprehensiveStandardScore += subjectFullScore.getLishi();
            double historyScoreRate =  examCoversionTotal.getLishiCoversion() / subjectFullScore.getLishi();
            allSubjectRateMap.put("history",df.format(historyScoreRate));
        }
        if (!examCoversionTotal.getDiliCoversion().toString().equals("0.0")){
            comprehensiveScore =+ examCoversionTotal.getDiliCoversion();
            comprehensiveStandardScore += subjectFullScore.getDili();
            double geographyScoreRate =  examCoversionTotal.getDiliCoversion() / subjectFullScore.getDili();
            allSubjectRateMap.put("geography",df.format(geographyScoreRate));
        }
        if (!examCoversionTotal.getZhengzhiCoversion().toString().equals("0.0")){
            comprehensiveScore =+ examCoversionTotal.getZhengzhiCoversion();
            comprehensiveStandardScore += subjectFullScore.getZhengzhi();
            double biologicalScoreRate =  examCoversionTotal.getZhengzhiCoversion() / subjectFullScore.getZhengzhi();
            allSubjectRateMap.put("biological",df.format(biologicalScoreRate));
        }

        List<AsahiChartAllRateDTO> list = new ArrayList<>();
        AsahiChartAllRateDTO asahiChartAllRateDTO = new AsahiChartAllRateDTO();
        asahiChartAllRateDTO.setTotalScoreRate(df.format(examCoversionTotal.getCoversionTotal() / sum));
        asahiChartAllRateDTO.setThreeSubjectsRate(df.format(threeSubject));
        asahiChartAllRateDTO.setComprehensiveRate(df.format(comprehensiveScore / comprehensiveStandardScore));
        asahiChartAllRateDTO.setAllSubjectRateMap(allSubjectRateMap);

        list.add(asahiChartAllRateDTO);
        return list;
    }

    @Override
    public List<ScoreReportDTO> getScoreReport(String stuNumber, String examType) {
        ExamCoversionTotal examCoversionTotal = examCoversionTotalDao.findByStudentNumberAndExamType(stuNumber, examType);
        if (null == examCoversionTotal) {
            info = "查询此学生的所有信息失败";
            logger.error(info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        //保留两位小数
        DecimalFormat df = new DecimalFormat("#0.00");
        int examTnfoId = examInfoDao.findByExamName(examType);
        SubjectFullScore subjectFullScore = subjectFullScoreDao.findById(examTnfoId);
        // 本次考试的全科总分
        int sum = Math.toIntExact(subjectFullScore.getYuwen() + subjectFullScore.getShuxue() + subjectFullScore.getYingyu() + subjectFullScore.getWuli() + subjectFullScore.getHuaxue()
                + subjectFullScore.getShengwu() + subjectFullScore.getZhengzhi() + subjectFullScore.getDili() + subjectFullScore.getLishi()) - 300;
        //获取年级平均分
        String totalAverage = examCoversionTotalDao.totalAverageByExamType(examType);
        //查询单科的所有年级平均分
        List<SubjectDTO> subjectDTOList = subjectDTODao.avgSubject(examType);
        SubjectDTO subjectDTO = subjectDTOList.get(0);
        logger.info(String.valueOf(subjectDTO));
        // Map<String, Map<String, String>> map = new HashMap<>();
//        Map<String, Map<String, String>> map = new TreeMap<>();
        //LinkedHashMap将map中的顺序按照添加顺序排列
        Map<String, Map<String, String>> map = new LinkedHashMap<>();
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

        List<String> yuwenScoreGrade = examCoversionTotalDao.findByYuwenScore(examType);
        yuwenMap.put("title", "语文");
        yuwenMap.put("score", String.valueOf(examCoversionTotal.getYuwenScore()));
        yuwenMap.put("gradeRank", String.valueOf(yuwenScoreGrade.indexOf(Float.parseFloat(examCoversionTotal.getYuwenScore().toString())) + 1));
        List<String> yuwenScoreClass = examCoversionTotalDao.findByClassIdAndYuwenScore(examCoversionTotal.getClassId(), examType);
        yuwenMap.put("classRank", String.valueOf(yuwenScoreClass.indexOf(Float.parseFloat(examCoversionTotal.getYuwenScore().toString())) + 1));
        yuwenMap.put("fullscoreStandard", subjectFullScore.getYuwen().toString());
        yuwenMap.put("subjectAvg", df.format(Double.parseDouble(subjectDTO.getYuwen())));
        // 数学
        List<String> shuxueScoreGrade = examCoversionTotalDao.findByShuxueScore(examType);
        shuxueMap.put("title", "数学");
        shuxueMap.put("score", String.valueOf(examCoversionTotal.getShuxueScore()));
        shuxueMap.put("gradeRank", String.valueOf(shuxueScoreGrade.indexOf(Float.parseFloat(examCoversionTotal.getShuxueScore().toString())) + 1));
        List<String> shuxueScoreClass = examCoversionTotalDao.findByClassIdAndShuxueScore(examCoversionTotal.getClassId(), examType);
        shuxueMap.put("classRank", String.valueOf(shuxueScoreClass.indexOf(Float.parseFloat(examCoversionTotal.getShuxueScore().toString())) + 1));
        shuxueMap.put("fullscoreStandard", subjectFullScore.getShuxue().toString());
        shuxueMap.put("subjectAvg", df.format(Double.parseDouble(subjectDTO.getShengwu())));
        // 英语
        List<String> yingyuScoreGrade = examCoversionTotalDao.findByYingyuScore(examType);
        yingyuMap.put("title", "英语");
        yingyuMap.put("score", String.valueOf(examCoversionTotal.getYingyuScore()));
        yingyuMap.put("gradeRank", String.valueOf(yingyuScoreGrade.indexOf(Float.parseFloat(examCoversionTotal.getYingyuScore().toString())) + 1));
        List<String> yingyuScoreClass = examCoversionTotalDao.findByClassIdAndYingyuScore(examCoversionTotal.getClassId(), examType);
        yingyuMap.put("classRank", String.valueOf(yingyuScoreClass.indexOf(Float.parseFloat(examCoversionTotal.getYingyuScore().toString())) + 1));
        yingyuMap.put("fullscoreStandard", subjectFullScore.getYingyu().toString());
        yingyuMap.put("subjectAvg", df.format(Double.parseDouble(subjectDTO.getYingyu())));
        // 学生具体选择的科目
        if (!examCoversionTotal.getWuliCoversion().toString().equals("0.0")){
            wuliMap.put("title", "物理");
            List<String> wuliScoreGrade = examCoversionTotalDao.findByWuliCoversion(examType);
            wuliMap.put("score", String.valueOf(examCoversionTotal.getWuliCoversion()));
            wuliMap.put("gradeRank", String.valueOf(wuliScoreGrade.indexOf(Float.parseFloat(examCoversionTotal.getWuliCoversion().toString())) + 1));
            List<String> wuliScoreClass = examCoversionTotalDao.findByClassIdAndWuliCoversion(examCoversionTotal.getClassId(), examType);
            wuliMap.put("classRank", String.valueOf(wuliScoreClass.indexOf(Float.parseFloat(examCoversionTotal.getWuliCoversion().toString())) + 1));
            wuliMap.put("fullscoreStandard", subjectFullScore.getWuli().toString());
            wuliMap.put("subjectAvg", df.format(Double.parseDouble(subjectDTO.getWuli())));
        }
        if (!examCoversionTotal.getHuaxueCoversion().toString().equals("0.0")){
            huaxueMap.put("title", "化学");
            List<String> huaxueScoreGrade = examCoversionTotalDao.findByHuaxueCoversion(examType);
            huaxueMap.put("score", String.valueOf(examCoversionTotal.getHuaxueCoversion()));
            huaxueMap.put("gradeRank", String.valueOf(huaxueScoreGrade.indexOf(Float.parseFloat(examCoversionTotal.getHuaxueCoversion().toString())) + 1));
            List<String> huaxueScoreClass = examCoversionTotalDao.findByClassIdAndHuaxueCoversion(examCoversionTotal.getClassId(), examType);
            huaxueMap.put("classRank", String.valueOf(huaxueScoreClass.indexOf(Float.parseFloat(examCoversionTotal.getHuaxueCoversion().toString())) + 1));
            huaxueMap.put("fullscoreStandard", subjectFullScore.getHuaxue().toString());
            huaxueMap.put("subjectAvg", df.format(Double.parseDouble(subjectDTO.getHuaxue())));
        }
        if (!examCoversionTotal.getShengwuCoversion().toString().equals("0.0")){
            shengwuMap.put("title", "生物");
            List<String> shengwuScoreGrade = examCoversionTotalDao.findByShengwuCoversion(examType);
            shengwuMap.put("score", String.valueOf(examCoversionTotal.getShengwuCoversion()));
            shengwuMap.put("gradeRank", String.valueOf(shengwuScoreGrade.indexOf(Float.parseFloat(examCoversionTotal.getShengwuCoversion().toString())) + 1));
            List<String> shengwuScoreClass = examCoversionTotalDao.findByClassIdAndShengwuCoversion(examCoversionTotal.getClassId(), examType);
            shengwuMap.put("classRank", String.valueOf(shengwuScoreClass.indexOf(Float.parseFloat(examCoversionTotal.getShengwuCoversion().toString())) + 1));
            shengwuMap.put("fullscoreStandard", subjectFullScore.getShengwu().toString());
            shengwuMap.put("subjectAvg", df.format(Double.parseDouble(subjectDTO.getShengwu())));
        }
        if (!examCoversionTotal.getLishiCoversion().toString().equals("0.0") ){
            lishiMap.put("title", "历史");
            List<String> lishiScoreGrade = examCoversionTotalDao.findByLishiCoversion(examType);
            lishiMap.put("score", String.valueOf(examCoversionTotal.getLishiCoversion()));
            lishiMap.put("gradeRank", String.valueOf(lishiScoreGrade.indexOf(Float.parseFloat(examCoversionTotal.getLishiCoversion().toString())) + 1));
            List<String> lishiScoreClass = examCoversionTotalDao.findByClassIdAndLishiCoversion(examCoversionTotal.getClassId(), examType);
            lishiMap.put("classRank", String.valueOf(lishiScoreClass.indexOf(Float.parseFloat(examCoversionTotal.getLishiCoversion().toString())) + 1));
            lishiMap.put("fullscoreStandard", subjectFullScore.getLishi().toString());
            lishiMap.put("subjectAvg", df.format(Double.parseDouble(subjectDTO.getLishi())));
        }
        if (!examCoversionTotal.getDiliCoversion().toString().equals("0.0")){
            diliMap.put("title", "地理");
            List<String> diliScoreGrade = examCoversionTotalDao.findByDiliCoversion(examType);
            diliMap.put("score", String.valueOf(examCoversionTotal.getDiliCoversion()));
            diliMap.put("gradeRank", String.valueOf(diliScoreGrade.indexOf(Float.parseFloat(examCoversionTotal.getDiliCoversion().toString())) + 1));
            List<String> lishiScoreClass = examCoversionTotalDao.findByClassIdAndDiliCoversion(examCoversionTotal.getClassId(), examType);
            diliMap.put("classRank", String.valueOf(lishiScoreClass.indexOf(Float.parseFloat(examCoversionTotal.getDiliCoversion().toString())) + 1));
            diliMap.put("fullscoreStandard", subjectFullScore.getDili().toString());
            diliMap.put("subjectAvg", df.format(Double.parseDouble(subjectDTO.getDili())));
        }
        if (!examCoversionTotal.getZhengzhiCoversion().toString().equals("0.0")){
            List<String> zhengzhiScoreGrade = examCoversionTotalDao.findByZhengzhiCoversion(examType);
            zhengzhiMap.put("title", "政治");
            zhengzhiMap.put("score", String.valueOf(examCoversionTotal.getZhengzhiCoversion()));
            zhengzhiMap.put("gradeRank", String.valueOf(zhengzhiScoreGrade.indexOf(Float.parseFloat(examCoversionTotal.getZhengzhiCoversion().toString())) + 1));
            List<String> zhengzhiScoreClass = examCoversionTotalDao.findByClassIdAndZhengzhiCoversion(examCoversionTotal.getClassId(), examType);
            zhengzhiMap.put("classRank", String.valueOf(zhengzhiScoreClass.indexOf(Float.parseFloat(examCoversionTotal.getZhengzhiCoversion().toString())) + 1));
            zhengzhiMap.put("fullscoreStandard", subjectFullScore.getZhengzhi().toString());
            zhengzhiMap.put("subjectAvg", df.format(Double.parseDouble(subjectDTO.getZhengzhi())));
        }

        //班级总人数
        int totalClassNumber = yuwenScoreClass.size();
        //年级总人数
        int totalGradeNumber = yuwenScoreGrade.size();


        List<ScoreReportDTO> list = new ArrayList<>();
        ScoreReportDTO scoreReportDTO = new ScoreReportDTO();
        scoreReportDTO.setTotalScore(df.format(examCoversionTotal.getCoversionTotal()));
        scoreReportDTO.setTotalScoreGradeRank(examCoversionTotal.getSchoolIndex());
        scoreReportDTO.setTotalScoreClassRank(examCoversionTotal.getClassIndex());
        //总分满分标准
        scoreReportDTO.setTotalScoreStandard(df.format(sum));
        scoreReportDTO.setTotalClassNumber(totalClassNumber);
        scoreReportDTO.setTotalGradeNumber(totalGradeNumber);
        map.put("yuwenMap", yuwenMap);
        map.put("shuxueMap",shuxueMap);
        map.put("yingyuMap",yingyuMap);
        if (wuliMap.size() != 0){
            map.put("wuliMap",wuliMap);
        }
        if (zhengzhiMap.size() != 0){
            map.put("zhengzhiMap",zhengzhiMap);
        }

        if (huaxueMap.size() != 0){
            map.put("huaxueMap",huaxueMap);
        }
        if (lishiMap.size() != 0){
            map.put("lishiMap",lishiMap);
        }

        if (shengwuMap.size() != 0){
           map.put("shengwuMap",shengwuMap);
        }
        if (diliMap.size() != 0){
            map.put("diliMap",diliMap);
        }

        //总分的年级平均分
       // scoreReportDTO.setTotalAverage(df.format(totalAverage));
        scoreReportDTO.setTotalAverage(df.format(Double.parseDouble(totalAverage)));
        scoreReportDTO.setMap(map);

        list.add(scoreReportDTO);
        //打印出哪个接口，参数值是什么，当前时间，以便记录下当前访问哪个接口等信息，如有有openid则也记录下, 使用占位符，logger支持占位符
        logger.info("getScoreReport---> stuNumber:{}, examType:{}, time:{}", stuNumber,examType,getNowTime());
        return list;
    }
}
