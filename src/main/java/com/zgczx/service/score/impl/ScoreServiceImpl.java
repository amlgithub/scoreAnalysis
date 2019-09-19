package com.zgczx.service.score.impl;

import com.zgczx.dataobject.score.ExamCoversionTotal;
import com.zgczx.dataobject.score.ExamFullScoreSet;
import com.zgczx.dataobject.score.ExamInfo;
import com.zgczx.dataobject.score.SubjectFullScore;
import com.zgczx.dataobject.user.SysLogin;
import com.zgczx.dto.ExamCoversionTotalDTO;
import com.zgczx.dto.ExamCoversionTotalSectionDTO;
import com.zgczx.dto.ExamCoversionTotalSingleDTO;
import com.zgczx.dto.SixRateDTO;
import com.zgczx.enums.ResultEnum;
import com.zgczx.enums.UserEnum;
import com.zgczx.exception.ScoreException;
import com.zgczx.repository.score.ExamCoversionTotalDao;
import com.zgczx.repository.score.ExamFullScoreSetDao;
import com.zgczx.repository.score.ExamInfoDao;
import com.zgczx.repository.score.SubjectFullScoreDao;
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

        List<ExamCoversionTotalSingleDTO> examCoversionTotalSingleDTOList = new ArrayList<>();
        ExamCoversionTotalSingleDTO examCoversionTotalSingleDTO = new ExamCoversionTotalSingleDTO();
        examCoversionTotalSingleDTO.setExamCoversionTotal(examCoversionTotal);
        examCoversionTotalSingleDTO.setClassRank(mapClass.get(stuNumber));//班排名
        examCoversionTotalSingleDTO.setGradeRank(mapGrade.get(stuNumber));//年排名
        examCoversionTotalSingleDTO.setWaveGrade(waveGrade);//年级进退名次
        examCoversionTotalSingleDTO.setWaveClass(waveClass);//班级进退名次
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


        List<ExamCoversionTotalSectionDTO> examCoversionTotalSectionDTOList = new ArrayList<>();
        ExamCoversionTotalSectionDTO examCoversionTotalSectionDTO = new ExamCoversionTotalSectionDTO();
        examCoversionTotalSectionDTO.setExamCoversionTotal(examCoversionTotal);
        examCoversionTotalSectionDTO.setThreeSubject(threeSubject);
        examCoversionTotalSectionDTO.setComprehensive(comprehensive);
        // 求班排的第二中方法，即用排好序的list，取下标法，indexOf:如果元素相同取第一次出现的下标，
        examCoversionTotalSectionDTO.setClassRank(mapValueList.indexOf(String.valueOf(map.get(stuNumber))) + 1);

        //第一种方法，此方法
//        String key = String.valueOf(map.get(stuNumber));//强转有问题，这样的也有问题，如果小数多会出问题
//        examCoversionTotalSectionDTO.setClassRank(mapValueRank.get(key));

        examCoversionTotalSectionDTO.setGradeRank(threeSubjectGradeList.indexOf(String.valueOf(threeSubjectGradeMap.get(stuNumber))) + 1);

        examCoversionTotalSectionDTO.setComplexClassRank(mapValueListComplex.indexOf(String.valueOf(complexClassMap.get(stuNumber))) + 1);
        examCoversionTotalSectionDTO.setComplexGradeRank(mapValueListComplexGrade.indexOf(String.valueOf(complexGradeMap.get(stuNumber))) + 1);
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

        List<SixRateDTO> sixRateDTOList = new ArrayList<>();
        SixRateDTO sixRateDTO = new SixRateDTO();
        sixRateDTO.setHighNumRate(highnumradio);
        sixRateDTO.setExcellentRate(excellentratio);
        sixRateDTO.setGoodRate(goodratio);
        sixRateDTO.setPassRate(passratio);
        sixRateDTO.setFailRate(failratio);
        sixRateDTO.setBeyondRate(beyondradio);
        sixRateDTO.setLocationRate(location);
        sixRateDTOList.add(sixRateDTO);

        return sixRateDTOList;
    }
}
