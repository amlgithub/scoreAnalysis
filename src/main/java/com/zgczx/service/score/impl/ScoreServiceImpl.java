package com.zgczx.service.score.impl;

import com.zgczx.dataobject.score.*;
import com.zgczx.dataobject.user.SysLogin;
import com.zgczx.dto.*;
import com.zgczx.enums.ResultEnum;
import com.zgczx.enums.UserEnum;
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
import java.util.*;

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
    EntityManagerFactory ntityManagerFactory;

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
//        ExamCoversionTotal examCoversionTotal = examCoversionTotalDao.findByStudentNumberAndExamType(stuNumber,examType);
//        return examCoversionTotal;
        return examCoversionTotalDao.findByStudentNumberAndExamType(stuNumber, examType);
    }

    @Override
    public List<ExamInfo> getListExamInfols() {
        List<ExamInfo> examInfoList = examInfoDao.findAll();
        if (examInfoList == null || examInfoList.size() == 0) {
            info = "查无结果";
            logger.error(info);
            throw new ScoreException(ResultEnum.DATABASE_OP_EXCEPTION, info);
        }
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

        int examTnfoId = examInfoDao.findByExamName(examType);
        SubjectFullScore subjectFullScore = subjectFullScoreDao.findById(examTnfoId);
        // 本次考试的全科总分
        int sum = Math.toIntExact(subjectFullScore.getYuwen() + subjectFullScore.getShuxue() + subjectFullScore.getYingyu() + subjectFullScore.getWuli() + subjectFullScore.getHuaxue()
                + subjectFullScore.getShengwu() + subjectFullScore.getZhengzhi() + subjectFullScore.getDili() + subjectFullScore.getLishi()) - 300;


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
        examCoversionTotalSingleDTOList.add(examCoversionTotalSingleDTO);
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
            // mapValueRank存放的是 分值和排名
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

        List<String> list = new ArrayList<>();
        ImportConversionScore importConversionScore = importConversionScoreDao.findByStudentMachineCardAndExamType(examCoversionTotal.getStudentMachineCard(), examCoversionTotal.getExamType());
        if (importConversionScore == null){
            info = "查无此数据";
            logger.error(info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        if (!importConversionScore.getWuliConverscore().toString().equals("")){
            list.add("物理");
        }
        if (!importConversionScore.getHuaxueConverscore().toString().equals("")){
            list.add("化学");
        }
        if (!importConversionScore.getShengwuConverscore().toString().equals("")){
            list.add("生物");
        }
        if (!importConversionScore.getLishiConverscore().toString().equals("") ){
            list.add("历史");
        }
        if (!importConversionScore.getDiliConverscore().toString().equals("")){
            list.add("地理");
        }
        if (!importConversionScore.getZhengzhiConverscore().toString().equals("")){
            list.add("政治");
        }

        List<ExamCoversionTotalSectionDTO> examCoversionTotalSectionDTOList = new ArrayList<>();
        ExamCoversionTotalSectionDTO examCoversionTotalSectionDTO = new ExamCoversionTotalSectionDTO();
        examCoversionTotalSectionDTO.setExamCoversionTotal(examCoversionTotal);
        examCoversionTotalSectionDTO.setThreeSubject(threeSubject);
        examCoversionTotalSectionDTO.setComprehensive(comprehensive);
        // 求班排的第二中方法，即用排好序的list，取下标法，indexOf:如果元素相同取第一次出现的下标，
        examCoversionTotalSectionDTO.setClassRank(mapValueList.indexOf(String.valueOf(map.get(stuNumber))) + 1);

        //第一种方法，此方法
//        String key = String.valueOf(map.get(stuNumber));//强转有问题，这样的也有问题，如果小数多(科学计数法)会出问题
//        examCoversionTotalSectionDTO.setClassRank(mapValueRank.get(key));

        examCoversionTotalSectionDTO.setGradeRank(threeSubjectGradeList.indexOf(String.valueOf(threeSubjectGradeMap.get(stuNumber))) + 1);

        examCoversionTotalSectionDTO.setComplexClassRank(mapValueListComplex.indexOf(String.valueOf(complexClassMap.get(stuNumber))) + 1);
        examCoversionTotalSectionDTO.setComplexGradeRank(mapValueListComplexGrade.indexOf(String.valueOf(complexGradeMap.get(stuNumber))) + 1);

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
        // 此班级的所有的总分数据
        List<Double> coversionTotalList= examCoversionTotalDao.getCoversionTotalByClassIdAndExamType(examCoversionTotal.getClassId(), examType);
        int examTnfoId = examInfoDao.findByExamName(examType);
//        ExamFullScoreSet examFullScoreSet=  examFullScoreSetDao.findByExaminfoId((int) examTnfoId);
//        SubjectFullScore sbujectFullScore = subjectFullScoreDao.findOne((int) examFullScoreSet.getId());
//        int totalScore = (int) (sbujectFullScore.getYingyu() + sbujectFullScore.getShuxue()+sbujectFullScore.getYingyu()+sbujectFullScore.getWuli()+sbujectFullScore.getHuaxue()+sbujectFullScore.getShengwu()+sbujectFullScore.getDili()+sbujectFullScore.getLishi()+sbujectFullScore.getZhengzhi() - 300);
        BigInteger tatolscore = examCoversionTotalDao.findSchametotal(examTnfoId);
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
        ImportConversionScore importConversionScore = importConversionScoreDao.findByStudentMachineCardAndExamType(examCoversionTotal.getStudentMachineCard(), examCoversionTotal.getExamType());
        if (importConversionScore == null){
            info = "查无此数据";
            logger.error(info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        // 各单科与总分的比值
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
        language = Double.parseDouble(importConversionScore.getYuwenScore()) / Double.parseDouble(importConversionScore.getTotalScore());
        math = Double.parseDouble(importConversionScore.getShuxueScore()) / Double.parseDouble(importConversionScore.getTotalScore());
        english = Double.parseDouble(importConversionScore.getYingyuScore()) / Double.parseDouble(importConversionScore.getTotalScore());

        //再次使用原生SQL语句查询，来获取班级年级的排名
        EntityManager entityManager = ntityManagerFactory.createEntityManager();

        // 单科和总分的年级差值
        Map<String , Integer> equilibriumDifferenceMap = new HashMap<>();

        contributionRate.put("语文", df.format(language) + "%");
        List<String> yuwenGradeExamCoversionTotal =  examCoversionTotalDao.findByYuwenScore(examType);
        equilibriumDifferenceMap.put("语文差值", (int) (yuwenGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getYuwenScore().toString())) + 1 - examCoversionTotal.getCoversionTotal()));

        contributionRate.put("数学", df.format(math) + "%");
        List<String> shuxueGradeExamCoversionTotal =  examCoversionTotalDao.findByShuxueScore(examType);
        equilibriumDifferenceMap.put("数学差值", (int) (shuxueGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getShuxueScore().toString())) + 1 - examCoversionTotal.getCoversionTotal()));

        contributionRate.put("英语", df.format(english) + "%");
        List<String> yingyuGradeExamCoversionTotal =  examCoversionTotalDao.findByYingyuScore(examType);
        equilibriumDifferenceMap.put("英语差值", (int) (yingyuGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getYingyuScore().toString())) + 1 - examCoversionTotal.getCoversionTotal()));


        if (!importConversionScore.getWuliConverscore().toString().equals("")){
            contributionRate.put("物理", df.format(Double.parseDouble(importConversionScore.getWuliConverscore()) / Double.parseDouble(importConversionScore.getTotalScore())) + "%");
            List<String> wuliGradeExamCoversionTotal =  examCoversionTotalDao.findByWuliCoversion(examType);
            equilibriumDifferenceMap.put("物理差值", (int) (wuliGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getWuliCoversion().toString())) + 1 - examCoversionTotal.getCoversionTotal()));

        }
        if (!importConversionScore.getHuaxueConverscore().toString().equals("")){
            contributionRate.put("化学", df.format(Double.parseDouble(importConversionScore.getHuaxueConverscore()) / Double.parseDouble(importConversionScore.getTotalScore())) + "%");
            List<String> huaxueGradeExamCoversionTotal =  examCoversionTotalDao.findByHuaxueCoversion(examType);
            equilibriumDifferenceMap.put("化学差值", (int) (huaxueGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getHuaxueCoversion().toString())) + 1 - examCoversionTotal.getCoversionTotal()));

        }
        if (!importConversionScore.getShengwuConverscore().toString().equals("")){
            contributionRate.put("生物", df.format(Double.parseDouble(importConversionScore.getShengwuConverscore()) / Double.parseDouble(importConversionScore.getTotalScore())) + "%");
            List<String> shengwuGradeExamCoversionTotal =  examCoversionTotalDao.findByShengwuCoversion(examType);
            equilibriumDifferenceMap.put("生物差值", (int) (shengwuGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getShengwuCoversion().toString())) + 1 - examCoversionTotal.getCoversionTotal()));

        }
        if (!importConversionScore.getLishiConverscore().toString().equals("") ){
            contributionRate.put("历史",df.format(Double.parseDouble(importConversionScore.getLishiConverscore()) / Double.parseDouble(importConversionScore.getTotalScore())) + "%");
            List<String> lishiGradeExamCoversionTotal =  examCoversionTotalDao.findByLishiCoversion(examType);
            equilibriumDifferenceMap.put("历史差值", (int) (lishiGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getLishiCoversion().toString())) + 1 - examCoversionTotal.getCoversionTotal()));

        }
        if (!importConversionScore.getDiliConverscore().toString().equals("")){
            contributionRate.put("地理", df.format(Double.parseDouble(importConversionScore.getDiliConverscore()) / Double.parseDouble(importConversionScore.getTotalScore())) + "%");
            List<String> diliGradeExamCoversionTotal =  examCoversionTotalDao.findByDiliCoversion(examType);
            equilibriumDifferenceMap.put("地理差值", (int) (diliGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getDiliCoversion().toString())) + 1 - examCoversionTotal.getCoversionTotal()));

        }
        if (!importConversionScore.getZhengzhiConverscore().toString().equals("")){
            contributionRate.put("政治", df.format(Double.parseDouble(importConversionScore.getZhengzhiConverscore()) / Double.parseDouble(importConversionScore.getTotalScore())) + "%");
            List<String> zhengzhiGradeExamCoversionTotal =  examCoversionTotalDao.findByZhengzhiCoversion(examType);
            equilibriumDifferenceMap.put("政治差值", (int) (zhengzhiGradeExamCoversionTotal.indexOf(Float.parseFloat(examCoversionTotal.getZhengzhiCoversion().toString())) + 1 - examCoversionTotal.getCoversionTotal()));

        }

        List<SubjectAnalysisDTO> list = new ArrayList<>();
        SubjectAnalysisDTO subjectAnalysisDTO = new SubjectAnalysisDTO();
        subjectAnalysisDTO.setExamCoversionTotal(examCoversionTotal);
        subjectAnalysisDTO.setContributionRate(contributionRate);
        subjectAnalysisDTO.setEquilibriumDifference(equilibriumDifferenceMap);
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

        ImportConversionScore importConversionScore = importConversionScoreDao.findByStudentMachineCardAndExamType(examCoversionTotal.getStudentMachineCard(), examCoversionTotal.getExamType());
        if (importConversionScore == null){
            info = "查无此数据";
            logger.error(info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }

        // 综合分数、真实所选的科目分数之和
        double comprehensiveScore = 0.00;
        // 综合的标准满分， 真实所选的科目分数之和
        int comprehensiveStandardScore = 0;

        //保留两位小数
        DecimalFormat df = new DecimalFormat("#0.00");

        // 所有真实科目的率值，k: 科目名称；v：所对应的率值
        Map<String, String> allSubjectRateMap = new HashMap<>();
        double languageScoreRate = Double.parseDouble(importConversionScore.getYuwenScore()) / subjectFullScore.getYuwen();
        allSubjectRateMap.put("language", df.format(languageScoreRate));//语文
        double mathScoreRate = Double.parseDouble(importConversionScore.getShuxueScore()) / subjectFullScore.getShuxue();
        allSubjectRateMap.put("math",df.format(mathScoreRate));
        double englishScoreRate = Double.parseDouble(importConversionScore.getYingyuScore()) / subjectFullScore.getYingyu();
        allSubjectRateMap.put("english",df.format(englishScoreRate));

        if (!importConversionScore.getWuliConverscore().toString().equals("")){
            comprehensiveScore += Double.parseDouble(importConversionScore.getWuliConverscore());
            comprehensiveStandardScore += subjectFullScore.getWuli();
            double physicalScoreRate =  Double.parseDouble(importConversionScore.getWuliConverscore()) / subjectFullScore.getWuli();
            allSubjectRateMap.put("physical",df.format(physicalScoreRate));

        }
        if (!importConversionScore.getHuaxueConverscore().toString().equals("")){
            comprehensiveScore =+ Double.parseDouble(importConversionScore.getHuaxueConverscore());
            comprehensiveStandardScore += subjectFullScore.getHuaxue();
            double chemistryScoreRate =  Double.parseDouble(importConversionScore.getHuaxueConverscore()) / subjectFullScore.getHuaxue();
            allSubjectRateMap.put("chemistry",df.format(chemistryScoreRate));
        }
        if (!importConversionScore.getShengwuConverscore().toString().equals("")){
            comprehensiveScore =+ Double.parseDouble(importConversionScore.getShengwuConverscore());
            comprehensiveStandardScore += subjectFullScore.getShengwu();
            double biologicalScoreRate =  Double.parseDouble(importConversionScore.getShengwuConverscore()) / subjectFullScore.getShengwu();
            allSubjectRateMap.put("biological",df.format(biologicalScoreRate));
        }
        if (!importConversionScore.getLishiConverscore().toString().equals("") ){
            comprehensiveScore =+ Double.parseDouble(importConversionScore.getLishiConverscore());
            comprehensiveStandardScore += subjectFullScore.getLishi();
            double historyScoreRate =  Double.parseDouble(importConversionScore.getLishiConverscore()) / subjectFullScore.getLishi();
            allSubjectRateMap.put("history",df.format(historyScoreRate));
        }
        if (!importConversionScore.getDiliConverscore().toString().equals("")){
            comprehensiveScore =+ Double.parseDouble(importConversionScore.getDiliConverscore());
            comprehensiveStandardScore += subjectFullScore.getDili();
            double geographyScoreRate =  Double.parseDouble(importConversionScore.getDiliConverscore()) / subjectFullScore.getDili();
            allSubjectRateMap.put("geography",df.format(geographyScoreRate));
        }
        if (!importConversionScore.getZhengzhiConverscore().toString().equals("")){
            comprehensiveScore =+ Double.parseDouble(importConversionScore.getZhengzhiConverscore());
            comprehensiveStandardScore += subjectFullScore.getZhengzhi();
            double biologicalScoreRate =  Double.parseDouble(importConversionScore.getZhengzhiConverscore()) / subjectFullScore.getZhengzhi();
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
        ImportConversionScore importConversionScore = importConversionScoreDao.findByStudentMachineCardAndExamType(examCoversionTotal.getStudentMachineCard(), examCoversionTotal.getExamType());
        if (importConversionScore == null){
            info = "查无此数据";
            logger.error(info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
//        //使用原生SQL
//        EntityManager em = ntityManagerFactory.createEntityManager();
        // 具体科目的分数map，k: 科目名称，V：对应的分数
        Map<String, String> subjectScoreMap = new HashMap<>();
        // 具体科目的年级排名，K:科目名称，V：对应的年级排名
        Map<String, Integer> subjectGradeRankMap = new HashMap<>();
        // 具体科目的班级排名，K:科目名称，V：对应的班级排名
        Map<String, Integer> subjectClassRankMap = new HashMap<>();

        List<String> yuwenScoreGrade = examCoversionTotalDao.findByYuwenScore(examType);
        subjectScoreMap.put("language", String.valueOf(examCoversionTotal.getYuwenScore()));
        subjectGradeRankMap.put("languageGradeRank", yuwenScoreGrade.indexOf(Float.parseFloat(examCoversionTotal.getYuwenScore().toString())) + 1);
        List<String> yuwenScoreClass = examCoversionTotalDao.findByClassIdAndYuwenScore(examCoversionTotal.getClassId(), examType);
        subjectClassRankMap.put("languageClassRank", yuwenScoreClass.indexOf(Float.parseFloat(examCoversionTotal.getYuwenScore().toString())) + 1);
        // 数学
        List<String> shuxueScoreGrade = examCoversionTotalDao.findByShuxueScore(examType);
        subjectScoreMap.put("math", String.valueOf(examCoversionTotal.getShuxueScore()));
        subjectGradeRankMap.put("mathGradeRank", shuxueScoreGrade.indexOf(Float.parseFloat(examCoversionTotal.getShuxueScore().toString())) + 1);
        List<String> shuxueScoreClass = examCoversionTotalDao.findByClassIdAndShuxueScore(examCoversionTotal.getClassId(), examType);
        subjectClassRankMap.put("mathClassRank", shuxueScoreClass.indexOf(Float.parseFloat(examCoversionTotal.getShuxueScore().toString())) + 1);
        // 英语
        List<String> yingyuScoreGrade = examCoversionTotalDao.findByYingyuScore(examType);
        subjectScoreMap.put("english", String.valueOf(examCoversionTotal.getYingyuScore()));
        subjectGradeRankMap.put("englishGradeRank", yingyuScoreGrade.indexOf(Float.parseFloat(examCoversionTotal.getYingyuScore().toString())) + 1);
        List<String> yingyuScoreClass = examCoversionTotalDao.findByClassIdAndYingyuScore(examCoversionTotal.getClassId(), examType);
        subjectClassRankMap.put("englishClassRank", yingyuScoreClass.indexOf(Float.parseFloat(examCoversionTotal.getYingyuScore().toString())) + 1);

        // 学生具体选择的科目
        if (!importConversionScore.getWuliConverscore().toString().equals("")){
            List<String> wuliScoreGrade = examCoversionTotalDao.findByWuliCoversion(examType);
            subjectScoreMap.put("physical", String.valueOf(examCoversionTotal.getWuliCoversion()));
            subjectGradeRankMap.put("physicalGradeRank", wuliScoreGrade.indexOf(Float.parseFloat(examCoversionTotal.getWuliCoversion().toString())) + 1);
            List<String> wuliScoreClass = examCoversionTotalDao.findByClassIdAndWuliCoversion(examCoversionTotal.getClassId(), examType);
            subjectClassRankMap.put("physicalClassRank", wuliScoreClass.indexOf(Float.parseFloat(examCoversionTotal.getWuliCoversion().toString())) + 1);

        }
        if (!importConversionScore.getHuaxueConverscore().toString().equals("")){
            List<String> huaxueScoreGrade = examCoversionTotalDao.findByHuaxueCoversion(examType);
            subjectScoreMap.put("chemistry", String.valueOf(examCoversionTotal.getHuaxueCoversion()));
            subjectGradeRankMap.put("chemistryGradeRank", huaxueScoreGrade.indexOf(Float.parseFloat(examCoversionTotal.getHuaxueCoversion().toString())) + 1);
            List<String> huaxueScoreClass = examCoversionTotalDao.findByClassIdAndHuaxueCoversion(examCoversionTotal.getClassId(), examType);
            subjectClassRankMap.put("chemistryClassRank", huaxueScoreClass.indexOf(Float.parseFloat(examCoversionTotal.getHuaxueCoversion().toString())) + 1);

        }
        if (!importConversionScore.getShengwuConverscore().toString().equals("")){
            List<String> shengwuScoreGrade = examCoversionTotalDao.findByShengwuCoversion(examType);
            subjectScoreMap.put("biological", String.valueOf(examCoversionTotal.getShengwuCoversion()));
            subjectGradeRankMap.put("biologicalGradeRank", shengwuScoreGrade.indexOf(Float.parseFloat(examCoversionTotal.getShengwuCoversion().toString())) + 1);
            List<String> shengwuScoreClass = examCoversionTotalDao.findByClassIdAndShengwuCoversion(examCoversionTotal.getClassId(), examType);
            subjectClassRankMap.put("biologicalClassRank", shengwuScoreClass.indexOf(Float.parseFloat(examCoversionTotal.getShengwuCoversion().toString())) + 1);

        }
        if (!importConversionScore.getLishiConverscore().toString().equals("") ){
            List<String> lishiScoreGrade = examCoversionTotalDao.findByLishiCoversion(examType);
            subjectScoreMap.put("history", String.valueOf(examCoversionTotal.getLishiCoversion()));
            subjectGradeRankMap.put("historyGradeRank", lishiScoreGrade.indexOf(Float.parseFloat(examCoversionTotal.getLishiCoversion().toString())) + 1);
            List<String> lishiScoreClass = examCoversionTotalDao.findByClassIdAndLishiCoversion(examCoversionTotal.getClassId(), examType);
            subjectClassRankMap.put("historyClassRank", lishiScoreClass.indexOf(Float.parseFloat(examCoversionTotal.getLishiCoversion().toString())) + 1);

        }
        if (!importConversionScore.getDiliConverscore().toString().equals("")){
            List<String> diliScoreGrade = examCoversionTotalDao.findByDiliCoversion(examType);
            subjectScoreMap.put("geography", String.valueOf(examCoversionTotal.getDiliCoversion()));
            subjectGradeRankMap.put("geographyGradeRank", diliScoreGrade.indexOf(Float.parseFloat(examCoversionTotal.getDiliCoversion().toString())) + 1);
            List<String> lishiScoreClass = examCoversionTotalDao.findByClassIdAndDiliCoversion(examCoversionTotal.getClassId(), examType);
            subjectClassRankMap.put("geographyClassRank", lishiScoreClass.indexOf(Float.parseFloat(examCoversionTotal.getDiliCoversion().toString())) + 1);

        }
        if (!importConversionScore.getZhengzhiConverscore().toString().equals("")){
            List<String> zhengzhiScoreGrade = examCoversionTotalDao.findByZhengzhiCoversion(examType);
            subjectScoreMap.put("biological", String.valueOf(examCoversionTotal.getZhengzhiCoversion()));
            subjectGradeRankMap.put("biologicalGradeRank", zhengzhiScoreGrade.indexOf(Float.parseFloat(examCoversionTotal.getZhengzhiCoversion().toString())) + 1);
            List<String> zhengzhiScoreClass = examCoversionTotalDao.findByClassIdAndZhengzhiCoversion(examCoversionTotal.getClassId(), examType);
            subjectClassRankMap.put("biologicalClassRank", zhengzhiScoreClass.indexOf(Float.parseFloat(examCoversionTotal.getZhengzhiCoversion().toString())) + 1);

        }
        //保留两位小数
        DecimalFormat df = new DecimalFormat("#0.00");
        List<ScoreReportDTO> list = new ArrayList<>();
        ScoreReportDTO scoreReportDTO = new ScoreReportDTO();
        scoreReportDTO.setTotalScore(df.format(examCoversionTotal.getCoversionTotal()));
        scoreReportDTO.setTotalScoreGradeRank(examCoversionTotal.getSchoolIndex());
        scoreReportDTO.setTotalScoreClassRank(examCoversionTotal.getClassIndex());
        scoreReportDTO.setSubjectScoreMap(subjectScoreMap);
        scoreReportDTO.setSubjectClassRankMap(subjectClassRankMap);
        scoreReportDTO.setSubjectGradeRankMap(subjectGradeRankMap);



        list.add(scoreReportDTO);
        return list;
    }
}
