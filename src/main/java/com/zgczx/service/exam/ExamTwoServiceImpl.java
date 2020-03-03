package com.zgczx.service.exam;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.zgczx.enums.ResultEnum;
import com.zgczx.exception.ScoreException;
import com.zgczx.repository.mysql1.exam.dao.*;
import com.zgczx.repository.mysql1.exam.model.ExamPaper;
import com.zgczx.repository.mysql1.exam.model.Question;
import com.zgczx.repository.mysql1.exam.model.UserQuestionRecord;
import com.zgczx.repository.mysql1.exam.model.UserWrongQustion;
import com.zgczx.repository.mysql3.unifiedlogin.dao.UserLoginDao;
import com.zgczx.repository.mysql3.unifiedlogin.model.UserLogin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.zgczx.utils.FilterStringUtil.filterspecial;
import static com.zgczx.utils.FilterStringUtil.optionLetter;
import static com.zgczx.utils.RecursionTreeUtil.randomSort;

/**
 * @author lxj
 * @date 2019/12/30
 */
@Service
@Slf4j
public class ExamTwoServiceImpl implements ExamTwoService {

    @Autowired
    UserLoginDao userLoginDao;

    @Autowired
    UserCollectDao userCollectDao;

    @Autowired
    ChapterDao chapterDao;

    @Autowired
    ExamPaperDao examPaperDao;

    @Autowired
    QuestionDao questionDao;

    @Autowired
    UserWrongQustionDao userWrongQustionDao;

    @Autowired
    UserQuestionRecordDao userQuestionRecordDao;

    private String info;

    // 1. 我的错题中：总错题数、章节错题数、考试错题数  lxj
    @Override
    public Map<String, Integer> getErrorProblemsNum(String stuNumber, String openid, String subject) {

        UserLogin userInfo = userLoginDao.findByDiyid(stuNumber);// 获取此用户的所有基本信息
        if (userInfo == null) {
            info = "暂时没有学号所对应的信息，请认真核对您的学号";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }

        Map<String, Integer> errorProblemsNumMap = new LinkedHashMap<>();

//        // 统计章节练习错题数(未掌握的)
//        int chapterNotMasteredErrorProblemsNum=userWrongQustionDao.getChapterErrProblemsNum(stuNumber,subject);
//        errorProblemsNumMap.put("chapterNotMasteredErrorProblemsNum",chapterNotMasteredErrorProblemsNum);
//
//        // 统计考试错题数(未掌握的)
//        int examNotMasteredErrorProblemsNum=userWrongQustionDao.getExamErrorProblemsNum(stuNumber,subject);
//        errorProblemsNumMap.put("examNotMasteredErrorProblemsNum",examNotMasteredErrorProblemsNum);

        // 统计未掌握错题总数
        int notMasteredErrorProblemsNum = userWrongQustionDao.getNotMasteredErrorProblemsNum(stuNumber, subject);
        errorProblemsNumMap.put("notMasteredErrorProblemsNum", notMasteredErrorProblemsNum);

//        // 练习错题数(已掌握的)
//        int chapterMasteredErrorProblemsNum=userWrongQustionDao.getChapterErrProblemsNum2(stuNumber,subject);
//        errorProblemsNumMap.put("chapterMasteredErrorProblemsNum",chapterMasteredErrorProblemsNum);
//
//        // 考试错题数(已掌握的)
//        int examMasteredErrorProblemsNum=userWrongQustionDao.getExamErrorProblemsNum2(stuNumber,subject);
//        errorProblemsNumMap.put("examMasteredErrorProblemsNum",examMasteredErrorProblemsNum);

        // 统计已掌握错题总数
        int masteredErrorProblemsNum = userWrongQustionDao.getMasteredErrorProblemsNum(stuNumber, subject);
        errorProblemsNumMap.put("masteredErrorProblemsNum", masteredErrorProblemsNum);

        // 统计练习错题总数
        int practiceErrorNum = userWrongQustionDao.getPracticeErrorNum(stuNumber, subject);
        errorProblemsNumMap.put("practiceErrorNum", practiceErrorNum);

        // 统计考试错题总数
        int examErrorNum = userWrongQustionDao.getExamErrorNum(stuNumber, subject);
        errorProblemsNumMap.put("examErrorNum", examErrorNum);

        // 统计我的错题数(总数)
        int errorProblemsNum = userWrongQustionDao.getErrorProblemsNumber(stuNumber, subject);
        errorProblemsNumMap.put("myErrorProblemsNum", errorProblemsNum);

        return errorProblemsNumMap;

    }

    // 2. 我的收藏：收藏总题数、收藏练习题数、收藏考试题数  lxj
    @Override
    public Map<String, Integer> getCollectProblemsNum(String stuNumber, String openid, String subject) {

        UserLogin userInfo = userLoginDao.findByDiyid(stuNumber);// 获取此用户的所有基本信息
        if (userInfo == null) {
            info = "暂时没有学号所对应的信息，请认真核对您的学号";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }

        Map<String, Integer> collectProblemsNumMap = new LinkedHashMap<>();
        // 统计我的错题数
        int errorProblemsNum = userCollectDao.getCollectProblemsNum(stuNumber, subject);
        collectProblemsNumMap.put("myCollectProblemsNum", errorProblemsNum);

        // 统计章节练习错题数
        int chapterAndSectionsErrorProblemsNum = userCollectDao.getCollectChapterProblemsNum(stuNumber, subject);
        collectProblemsNumMap.put("chapterCollectProblemsNum", chapterAndSectionsErrorProblemsNum);

        // 统计考试错题数
        int examErrorProblemsNum = userCollectDao.getCollectExamProblemsNum(stuNumber, subject);
        collectProblemsNumMap.put("examCollectProblemsNum", examErrorProblemsNum);

        return collectProblemsNumMap;
    }

    // 3. 我的收藏：默认选择练习题收藏(每一章及收藏的题数)，选择考试题收藏时，显示对应的考试名称和收藏题数   lxj
    @Override
    public Map<String, Integer> getCollectProblemsNum(String stuNumber, String openid, String subject, String category) {
        UserLogin userInfo = userLoginDao.findByDiyid(stuNumber);// 获取此用户的所有基本信息
        if (userInfo == null) {
            info = "暂时没有学号所对应的信息，请认真核对您的学号";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }

        Map<String, Integer> getCollectNum = new LinkedHashMap<>();
        if (category.equals("练习题收藏")) {

            String levelName = "高1";

            // 得到该学科所有的章节
            List<String> getChaptersBySubject = chapterDao.findChapterBySubject(subject, levelName);
            if (getChaptersBySubject == null || getChaptersBySubject.size() == 0) {
                info = "暂时没有该学科对应的章节信息";
                log.error("【错误信息】: {}", info);
                throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
            } else {
                for (int i = 0; i < getChaptersBySubject.size(); i++) {
                    String chapter = getChaptersBySubject.get(i);
                    // 练习题收藏
                    int getChapterAndProblemCollectNums = userCollectDao.getCollectProblemsByChapter(stuNumber, subject, chapter);
                    if (getChapterAndProblemCollectNums != 0) {
                        getCollectNum.put(chapter, getChapterAndProblemCollectNums);
                    }
                }
            }
        } else if (category.equals("考试题收藏")) {
            // 得到该学科所有的章节
            List<String> getExamName = examPaperDao.getExamName();
            if (getExamName == null || getExamName.size() == 0) {
                info = "暂时没有考试题";
                log.error("【错误信息】: {}", info);
                throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
            } else {
                for (int i = 0; i < getExamName.size(); i++) {
                    String examName = getExamName.get(i);
                    // 练习题收藏
                    int getExamAndProblemCollectNums = userCollectDao.getCollectProblemsByExam(stuNumber, subject, examName);
                    if (getExamAndProblemCollectNums != 0) {
                        getCollectNum.put(examName, getExamAndProblemCollectNums);
                    }
                }
            }
        }
        return getCollectNum;
    }

    // 4. 点开章，根据章的名称获取  节的名称和对应收藏的题数  lxj
    @Override
    public Map<String, Integer> getCollectProblemsNumByChapter(String studentNumber, String openid, String subject, String chapter) {
        UserLogin userInfo = userLoginDao.findByDiyid(studentNumber);// 获取此用户的所有基本信息
        if (userInfo == null) {
            info = "暂时没有学号所对应的信息，请认真核对您的学号";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }

        Map<String, Integer> getCollectProblemsNumBySectionMap = new LinkedHashMap<>();
        // 根据章的名称获取节的名称
        List<String> sections = chapterDao.findByChapterAndSubject(chapter, subject);
        if (sections == null || sections.size() == 0) {
            info = "该章暂无小节信息";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        } else {
            for (int i = 0; i < sections.size(); i++) {
                String section = sections.get(i);
                int getCollectProblemsNumBySection = userCollectDao.getCollectProblemsBySection(studentNumber, subject, section);
                if (getCollectProblemsNumBySection != 0) {
                    getCollectProblemsNumBySectionMap.put(section, getCollectProblemsNumBySection);
                }
            }
        }

        return getCollectProblemsNumBySectionMap;
    }

    // 5.  根据章节名称查询收藏的题的详细信息 lxj
    @Override
    public JSONArray getSectionCollectProblems(String stuNumber, String openid, String subject, String chapter, String section) {
        UserLogin userInfo = userLoginDao.findByDiyid(stuNumber);// 获取此用户的所有基本信息
        if (userInfo == null) {
            info = "暂时没有学号所对应的信息，请认真核对您的学号";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }

//        List<Question> sectionCollectQuestions=new ArrayList<>();
        // JSONObject sectionCollectQuestionsJson=new JSONObject();
        JSONArray sectionCollectQuestionsArr = new JSONArray();
        List<Question> sectionCollectProblems = questionDao.getSectionCollectProblems(stuNumber, subject, chapter, section);
        if (sectionCollectProblems == null || sectionCollectProblems.size() == 0) {
            info = "暂时没有收藏该章节的题";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        } else {
            for (int i = 0; i < sectionCollectProblems.size(); i++) {
                JSONObject jo = new JSONObject();
//                Question questions=new Question();
                int id = sectionCollectProblems.get(i).getId();
                jo.put("id", id);
                int exam_id = sectionCollectProblems.get(i).getExamId();
                jo.put("exam_id", exam_id);
                String subjects = sectionCollectProblems.get(i).getSubject();
                jo.put("subject", subjects);
                String question_source = sectionCollectProblems.get(i).getQuestionSource();
                jo.put("question_source", question_source);
                String exam_name = sectionCollectProblems.get(i).getExamName();
                jo.put("exam_name", exam_name);
                String exam_type = sectionCollectProblems.get(i).getExamType();
                jo.put("exam_type", exam_type);
                String exam_location = sectionCollectProblems.get(i).getExamLocation();
                jo.put("exam_location", exam_location);
                int question_id = sectionCollectProblems.get(i).getQuestionId();
                jo.put("question_id", question_id);
                String question_type = sectionCollectProblems.get(i).getQuestionType();
                jo.put("question_type", question_type);
                String question_difficult = sectionCollectProblems.get(i).getQuestionDifficult();
                jo.put("question_difficult", question_difficult);
                String question_context = sectionCollectProblems.get(i).getQuestionContext();
                question_context = filterspecial(question_context);
                jo.put("question_context", question_context);
                String question_image = sectionCollectProblems.get(i).getQuestionImgs();
                jo.put("question_imgs", question_image);
                String question_option = sectionCollectProblems.get(i).getQuestionOption();
                question_option = filterspecial(question_option);
                question_option = question_option.replaceAll(" ", "");
                log.info("【去除t,n等字符】： {}", question_option);
                int i1 = -1;
                if (question_option.indexOf("A．") != -1) {
                    i1 = question_option.indexOf("A．");
                } else {
                    i1 = question_option.indexOf("A.");
                }
                int i2 = -1;
                if (question_option.indexOf("B．") != -1) {
                    i2 = question_option.indexOf("B．");
                } else {
                    i2 = question_option.indexOf("B.");
                }
                int i3 = -1;
                if (question_option.indexOf("C．") != -1) {
                    i3 = question_option.indexOf("C．");
                } else {
                    i3 = question_option.indexOf("C.");
                }
                int i4 = -1;
                if (question_option.indexOf("D．") != -1) {
                    i4 = question_option.indexOf("D．");
                } else {
                    i4 = question_option.indexOf("D.");
                }

                String str1 = question_option.substring(i1 + 2, i2);//A选项
                String str2 = question_option.substring(i2 + 2, i3);//B选项
                String str3 = question_option.substring(i3 + 2, i4);//C选项
                String str4 = question_option.substring(i4 + 2, question_option.length());//D选项
                List<String> options = new ArrayList<>();
                options.add("A." + str1);
                options.add("B." + str2);
                options.add("C." + str3);
                options.add("D." + str4);
                jo.put("question_option", options);
                String question_score = String.valueOf(sectionCollectProblems.get(i).getQuestionScore());
                jo.put("question_score", question_score);
                String question_attribute = sectionCollectProblems.get(i).getQuestionAttribute();
                jo.put("question_attribute", question_attribute);
                String correct_option = sectionCollectProblems.get(i).getCorrectOption();
                jo.put("correct_option", correct_option);
                String correct_text = sectionCollectProblems.get(i).getCorrectText();
                jo.put("correct_text", correct_text);
                String correct_analysis = sectionCollectProblems.get(i).getCorrectAnalysis();
                jo.put("correct_analysis", correct_analysis);
                int chapter_id = sectionCollectProblems.get(i).getChapterId();
                jo.put("chapter_id", chapter_id);
                String level = sectionCollectProblems.get(i).getLevel();
                jo.put("level", level);
                String level_name = sectionCollectProblems.get(i).getLevelName();
                jo.put("level_name", level_name);
                String creater_user = sectionCollectProblems.get(i).getCreateUser();
                jo.put("create_user", creater_user);
                String knowledge_module = sectionCollectProblems.get(i).getKnowledgeModule();
                jo.put("knowledge_module", knowledge_module);
                String cognitive_level = sectionCollectProblems.get(i).getCognitiveLevel();
                jo.put("cognitive_level", cognitive_level);
                String core_literacy = sectionCollectProblems.get(i).getCoreLiteracy();
                jo.put("core_literacy", core_literacy);
                int valid = sectionCollectProblems.get(i).getValid();
                jo.put("valid", valid);
                Timestamp inserttime = sectionCollectProblems.get(i).getInserttime();
                jo.put("inserttime", inserttime);
                Timestamp updatetime = sectionCollectProblems.get(i).getUpdatetime();
                jo.put("updatetime", updatetime);

                jo.put("ifCollect", 1);

                sectionCollectQuestionsArr.add(jo);
            }
        }

        return sectionCollectQuestionsArr;
    }

    // 6.  根据考试名称查询收藏的题的详细信息 lxj
    @Override
    public JSONArray getExamCollectProblems(String stuNumber, String openid, String subject, String examName) {
        UserLogin userInfo = userLoginDao.findByDiyid(stuNumber);// 获取此用户的所有基本信息
        if (userInfo == null) {
            info = "暂时没有学号所对应的信息，请认真核对您的学号";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }

        //  List<Question> examCollectQuestions=new ArrayList<>();
        JSONArray examCollectQuestionsArr = new JSONArray();
        List<Question> examCollectProblems = questionDao.getExamCollectProblems(stuNumber, subject, examName);
        if (examCollectProblems == null || examCollectProblems.size() == 0) {
            info = "暂时没有收藏这次考试的题";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        } else {
            for (int i = 0; i < examCollectProblems.size(); i++) {
                JSONObject jo = new JSONObject();
                int id = examCollectProblems.get(i).getId();
                jo.put("id", id);
                int exam_id = examCollectProblems.get(i).getExamId();
                jo.put("exam_id", exam_id);
                String subjects = examCollectProblems.get(i).getSubject();
                jo.put("subject", subjects);
                String question_source = examCollectProblems.get(i).getQuestionSource();
                jo.put("question_source", question_source);
                String exam_name = examCollectProblems.get(i).getExamName();
                jo.put("exam_name", exam_name);
                String exam_type = examCollectProblems.get(i).getExamType();
                jo.put("exam_type", exam_type);
                String exam_location = examCollectProblems.get(i).getExamLocation();
                jo.put("exam_location", exam_location);
                int question_id = examCollectProblems.get(i).getQuestionId();
                jo.put("question_id", question_id);
                String question_type = examCollectProblems.get(i).getQuestionType();
                jo.put("question_type", question_type);
                String question_difficult = examCollectProblems.get(i).getQuestionDifficult();
                jo.put("question_difficult", question_difficult);
                String question_context = examCollectProblems.get(i).getQuestionContext();
                question_context = filterspecial(question_context);
                jo.put("question_context", question_context);
                String question_image = examCollectProblems.get(i).getQuestionImgs();
                jo.put("question_imgs", question_image);
                String question_option = examCollectProblems.get(i).getQuestionOption();
                question_option = filterspecial(question_option);
                question_option = question_option.replaceAll(" ", "");
                log.info("【去除t,n等字符】： {}", question_option);
                int i1 = -1;
                if (question_option.indexOf("A．") != -1) {
                    i1 = question_option.indexOf("A．");
                } else {
                    i1 = question_option.indexOf("A.");
                }
                int i2 = -1;
                if (question_option.indexOf("B．") != -1) {
                    i2 = question_option.indexOf("B．");
                } else {
                    i2 = question_option.indexOf("B.");
                }
                int i3 = -1;
                if (question_option.indexOf("C．") != -1) {
                    i3 = question_option.indexOf("C．");
                } else {
                    i3 = question_option.indexOf("C.");
                }
                int i4 = -1;
                if (question_option.indexOf("D．") != -1) {
                    i4 = question_option.indexOf("D．");
                } else {
                    i4 = question_option.indexOf("D.");
                }

                String str1 = question_option.substring(i1 + 2, i2);//A选项
                String str2 = question_option.substring(i2 + 2, i3);//B选项
                String str3 = question_option.substring(i3 + 2, i4);//C选项
                String str4 = question_option.substring(i4 + 2, question_option.length());//D选项
                List<String> options = new ArrayList<>();
                options.add("A." + str1);
                options.add("B." + str2);
                options.add("C." + str3);
                options.add("D." + str4);
                jo.put("question_option", options);
                String question_score = String.valueOf(examCollectProblems.get(i).getQuestionScore());
                jo.put("question_score", question_score);
                String question_attribute = examCollectProblems.get(i).getQuestionAttribute();
                jo.put("question_attribute", question_attribute);
                String correct_option = examCollectProblems.get(i).getCorrectOption();
                jo.put("correct_option", correct_option);
                String correct_text = examCollectProblems.get(i).getCorrectText();
                jo.put("correct_text", correct_text);
                String correct_analysis = examCollectProblems.get(i).getCorrectAnalysis();
                jo.put("correct_analysis", correct_analysis);
                int chapter_id = examCollectProblems.get(i).getChapterId();
                jo.put("chapter_id", chapter_id);
                String level = examCollectProblems.get(i).getLevel();
                jo.put("level", level);
                String level_name = examCollectProblems.get(i).getLevelName();
                jo.put("level_name", level_name);
                String creater_user = examCollectProblems.get(i).getCreateUser();
                jo.put("create_user", creater_user);
                String knowledge_module = examCollectProblems.get(i).getKnowledgeModule();
                jo.put("knowledge_module", knowledge_module);
                String cognitive_level = examCollectProblems.get(i).getCognitiveLevel();
                jo.put("cognitive_level", cognitive_level);
                String core_literacy = examCollectProblems.get(i).getCoreLiteracy();
                jo.put("core_literacy", core_literacy);
                int valid = examCollectProblems.get(i).getValid();
                jo.put("valid", valid);
                Timestamp inserttime = examCollectProblems.get(i).getInserttime();
                jo.put("inserttime", inserttime);
                Timestamp updatetime = examCollectProblems.get(i).getUpdatetime();
                jo.put("updatetime", updatetime);

                jo.put("ifCollect", 1);

                examCollectQuestionsArr.add(jo);
            }
        }

        return examCollectQuestionsArr;
    }

    // 7.  七、 练习错题：点击章、节显示 此小节错题的详细信息 lxj
    @Override
    public JSONArray getErrorProblemsByChapterAndSection(String stuNumber, String openid, String subject, String chapter, String section, String ifMastered) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        UserLogin userInfo = userLoginDao.findByDiyid(stuNumber);// 获取此用户的所有基本信息
        if (userInfo == null) {
            info = "暂时没有学号所对应的信息，请认真核对您的学号";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }

        JSONArray sectionErrorQuestionsArr = new JSONArray();
        List<UserWrongQustion> sectionErrorProblemsId = null;
        if (ifMastered.equals("未掌握")) {
            sectionErrorProblemsId = userWrongQustionDao.getErrorProblemsIdByChapterAndSection(stuNumber, subject, chapter, section, 2);
        } else if (ifMastered.equals("已掌握")) {
            sectionErrorProblemsId = userWrongQustionDao.getErrorProblemsIdByChapterAndSection(stuNumber, subject, chapter, section, 1);
        }
        if (sectionErrorProblemsId == null || sectionErrorProblemsId.size() == 0) {
            info = "本章节中暂时没有错题";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        } else {
            for (int i = 0; i < sectionErrorProblemsId.size(); i++) {
                JSONObject jo = new JSONObject();
                int question_id = sectionErrorProblemsId.get(i).getQuestionId();

                String user_answer = sectionErrorProblemsId.get(i).getUserAnswer();
                jo.put("user_answer", user_answer);
                int do_right = sectionErrorProblemsId.get(i).getDoRight();
                jo.put("do_right", do_right);
                String do_time = sectionErrorProblemsId.get(i).getDoTime();
                jo.put("do_time", do_time);

                List<Question> examQuestions = questionDao.getQuestionInfoById(question_id, subject);
                if (examQuestions == null || examQuestions.size() == 0) {
                    info = "查询的题不存在";
                    log.error("【错误信息】: {}", info);
                    throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
                } else {
                    int id = examQuestions.get(0).getId();
                    jo.put("id", id);
                    int exam_id = examQuestions.get(0).getExamId();
                    jo.put("exam_id", exam_id);
                    String subjects = examQuestions.get(0).getSubject();
                    jo.put("subject", subjects);
                    String question_source = examQuestions.get(0).getQuestionSource();
                    jo.put("question_source", question_source);
                    String exam_name = examQuestions.get(0).getExamName();
                    jo.put("exam_name", exam_name);
                    String exam_type = examQuestions.get(0).getExamType();
                    jo.put("exam_type", exam_type);
                    String exam_location = examQuestions.get(0).getExamLocation();
                    jo.put("exam_location", exam_location);
                    int question_id1 = examQuestions.get(0).getQuestionId();
                    jo.put("question_id", question_id1);
                    String question_type = examQuestions.get(0).getQuestionType();
                    jo.put("question_type", question_type);
                    String question_difficult = examQuestions.get(0).getQuestionDifficult();
                    jo.put("question_difficult", question_difficult);
                    String question_context = examQuestions.get(0).getQuestionContext();
                    question_context = filterspecial(question_context);
                    jo.put("question_context", question_context);
                    String question_image = examQuestions.get(0).getQuestionImgs();
                    jo.put("question_imgs", question_image);
                    String question_option = examQuestions.get(0).getQuestionOption();
                    question_option = filterspecial(question_option);
                    question_option = question_option.replaceAll(" ", "");
                    log.info("【去除t,n等字符】： {}", question_option);
                    int i1 = -1;
                    if (question_option.indexOf("A．") != -1) {
                        i1 = question_option.indexOf("A．");
                    } else {
                        i1 = question_option.indexOf("A.");
                    }
                    int i2 = -1;
                    if (question_option.indexOf("B．") != -1) {
                        i2 = question_option.indexOf("B．");
                    } else {
                        i2 = question_option.indexOf("B.");
                    }
                    int i3 = -1;
                    if (question_option.indexOf("C．") != -1) {
                        i3 = question_option.indexOf("C．");
                    } else {
                        i3 = question_option.indexOf("C.");
                    }
                    int i4 = -1;
                    if (question_option.indexOf("D．") != -1) {
                        i4 = question_option.indexOf("D．");
                    } else {
                        i4 = question_option.indexOf("D.");
                    }

                    String str1 = question_option.substring(i1 + 2, i2);//A选项
                    String str2 = question_option.substring(i2 + 2, i3);//B选项
                    String str3 = question_option.substring(i3 + 2, i4);//C选项
                    String str4 = question_option.substring(i4 + 2, question_option.length());//D选项
                    List<String> options = new ArrayList<>();
                    options.add("A." + str1);
                    options.add("B." + str2);
                    options.add("C." + str3);
                    options.add("D." + str4);
                    jo.put("question_option", options);
                    String question_score = String.valueOf(examQuestions.get(0).getQuestionScore());
                    jo.put("question_score", question_score);
                    String question_attribute = examQuestions.get(0).getQuestionAttribute();
                    jo.put("question_attribute", question_attribute);
                    String correct_option = examQuestions.get(0).getCorrectOption();
                    jo.put("correct_option", correct_option);
                    String correct_text = examQuestions.get(0).getCorrectText();
                    jo.put("correct_text", correct_text);
                    String correct_analysis = examQuestions.get(0).getCorrectAnalysis();
                    jo.put("correct_analysis", correct_analysis);
                    int chapter_id = examQuestions.get(0).getChapterId();
                    jo.put("chapter_id", chapter_id);
                    String level = examQuestions.get(0).getLevel();
                    jo.put("level", level);
                    String level_name = examQuestions.get(0).getLevelName();
                    jo.put("level_name", level_name);
                    String creater_user = examQuestions.get(0).getCreateUser();
                    jo.put("create_user", creater_user);
                    String knowledge_module = examQuestions.get(0).getKnowledgeModule();
                    jo.put("knowledge_module", knowledge_module);
                    String cognitive_level = examQuestions.get(0).getCognitiveLevel();
                    jo.put("cognitive_level", cognitive_level);
                    String core_literacy = examQuestions.get(0).getCoreLiteracy();
                    jo.put("core_literacy", core_literacy);
                    int valid = examQuestions.get(0).getValid();
                    jo.put("valid", valid);
                    String inserttime = format.format(examQuestions.get(0).getInserttime());
                    jo.put("inserttime", inserttime);
                    String updatetime = format.format(examQuestions.get(0).getUpdatetime());
                    jo.put("updatetime", updatetime);
                }

                // 查看该用户是否收藏了这道题
                int ifCollect = userCollectDao.getIfCollectByStuNumAndQuestionId(stuNumber, subject, question_id);
                if (ifCollect == 1) {
//                    jo.put("ifCollect","已收藏");
                    jo.put("ifCollect", 1);
                } else {
//                    jo.put("ifCollect","未收藏");
                    jo.put("ifCollect", 2);
                }


                sectionErrorQuestionsArr.add(jo);
            }
        }

        return sectionErrorQuestionsArr;
    }

    // 8.  八、 考试错题：点击某次考试，显示本次考试错题的详细信息 lxj
    @Override
    public JSONArray getErrorProblemsByExamName(String stuNumber, String openid, String subject, String examName, String ifMastered) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        UserLogin userInfo = userLoginDao.findByDiyid(stuNumber);// 获取此用户的所有基本信息
        if (userInfo == null) {
            info = "暂时没有学号所对应的信息，请认真核对您的学号";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }

        JSONArray examErrorQuestionsArr = new JSONArray();
        // 得到用户错题id等信息
        List<UserWrongQustion> examErrorProblemsId = null;
        if (ifMastered.equals("未掌握")) {
            examErrorProblemsId = userWrongQustionDao.getErrorProblemsIdByExamName(stuNumber, subject, examName, 2);
        } else if (ifMastered.equals("已掌握")) {
            examErrorProblemsId = userWrongQustionDao.getErrorProblemsIdByExamName(stuNumber, subject, examName, 1);
        }
        if (examErrorProblemsId == null || examErrorProblemsId.size() == 0) {
            info = "本章节中暂时没有错题";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        } else {
            for (int i = 0; i < examErrorProblemsId.size(); i++) {
                JSONObject jo = new JSONObject();
                int question_id = examErrorProblemsId.get(i).getQuestionId();

                String user_answer = examErrorProblemsId.get(i).getUserAnswer();
                jo.put("user_answer", user_answer);
                int do_right = examErrorProblemsId.get(i).getDoRight();
                jo.put("do_right", do_right);
                String do_time = examErrorProblemsId.get(i).getDoTime();
                jo.put("do_time", do_time);

                List<Question> examQuestions = questionDao.getQuestionInfoById(question_id, subject);
                if (examQuestions == null || examQuestions.size() == 0) {
                    info = "查询的题不存在";
                    log.error("【错误信息】: {}", info);
                    throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
                } else {
                    int id = examQuestions.get(0).getId();
                    jo.put("id", id);
                    int exam_id = examQuestions.get(0).getExamId();
                    jo.put("exam_id", exam_id);
                    String subjects = examQuestions.get(0).getSubject();
                    jo.put("subject", subjects);
                    String question_source = examQuestions.get(0).getQuestionSource();
                    jo.put("question_source", question_source);
                    String exam_name = examQuestions.get(0).getExamName();
                    jo.put("exam_name", exam_name);
                    String exam_type = examQuestions.get(0).getExamType();
                    jo.put("exam_type", exam_type);
                    String exam_location = examQuestions.get(0).getExamLocation();
                    jo.put("exam_location", exam_location);
                    int question_id1 = examQuestions.get(0).getQuestionId();
                    jo.put("question_id", question_id1);
                    String question_type = examQuestions.get(0).getQuestionType();
                    jo.put("question_type", question_type);
                    String question_difficult = examQuestions.get(0).getQuestionDifficult();
                    jo.put("question_difficult", question_difficult);
                    String question_context = examQuestions.get(0).getQuestionContext();
                    question_context = filterspecial(question_context);
                    jo.put("question_context", question_context);
                    String question_image = examQuestions.get(0).getQuestionImgs();
                    jo.put("question_imgs", question_image);
                    String question_option = examQuestions.get(0).getQuestionOption();
                    question_option = filterspecial(question_option);
                    question_option = question_option.replaceAll(" ", "");
                    log.info("【去除t,n等字符】： {}", question_option);
                    int i1 = -1;
                    if (question_option.indexOf("A．") != -1) {
                        i1 = question_option.indexOf("A．");
                    } else {
                        i1 = question_option.indexOf("A.");
                    }
                    int i2 = -1;
                    if (question_option.indexOf("B．") != -1) {
                        i2 = question_option.indexOf("B．");
                    } else {
                        i2 = question_option.indexOf("B.");
                    }
                    int i3 = -1;
                    if (question_option.indexOf("C．") != -1) {
                        i3 = question_option.indexOf("C．");
                    } else {
                        i3 = question_option.indexOf("C.");
                    }
                    int i4 = -1;
                    if (question_option.indexOf("D．") != -1) {
                        i4 = question_option.indexOf("D．");
                    } else {
                        i4 = question_option.indexOf("D.");
                    }

                    String str1 = question_option.substring(i1 + 2, i2);//A选项
                    String str2 = question_option.substring(i2 + 2, i3);//B选项
                    String str3 = question_option.substring(i3 + 2, i4);//C选项
                    String str4 = question_option.substring(i4 + 2, question_option.length());//D选项
                    List<String> options = new ArrayList<>();
                    options.add("A." + str1);
                    options.add("B." + str2);
                    options.add("C." + str3);
                    options.add("D." + str4);
                    jo.put("question_option", options);
                    String question_score = String.valueOf(examQuestions.get(0).getQuestionScore());
                    jo.put("question_score", question_score);
                    String question_attribute = examQuestions.get(0).getQuestionAttribute();
                    jo.put("question_attribute", question_attribute);
                    String correct_option = examQuestions.get(0).getCorrectOption();
                    jo.put("correct_option", correct_option);
                    String correct_text = examQuestions.get(0).getCorrectText();
                    jo.put("correct_text", correct_text);
                    String correct_analysis = examQuestions.get(0).getCorrectAnalysis();
                    jo.put("correct_analysis", correct_analysis);
                    int chapter_id = examQuestions.get(0).getChapterId();
                    jo.put("chapter_id", chapter_id);
                    String level = examQuestions.get(0).getLevel();
                    jo.put("level", level);
                    String level_name = examQuestions.get(0).getLevelName();
                    jo.put("level_name", level_name);
                    String creater_user = examQuestions.get(0).getCreateUser();
                    jo.put("create_user", creater_user);
                    String knowledge_module = examQuestions.get(0).getKnowledgeModule();
                    jo.put("knowledge_module", knowledge_module);
                    String cognitive_level = examQuestions.get(0).getCognitiveLevel();
                    jo.put("cognitive_level", cognitive_level);
                    String core_literacy = examQuestions.get(0).getCoreLiteracy();
                    jo.put("core_literacy", core_literacy);
                    int valid = examQuestions.get(0).getValid();
                    jo.put("valid", valid);
                    String inserttime = format.format(examQuestions.get(0).getInserttime());
                    jo.put("inserttime", inserttime);
                    String updatetime = format.format(examQuestions.get(0).getUpdatetime());
                    jo.put("updatetime", updatetime);
                }

                int ifCollect = userCollectDao.getIfCollectByStuNumAndQuestionId(stuNumber, subject, question_id);
                if (ifCollect == 1) {
//                    jo.put("ifCollect","已收藏");
                    jo.put("ifCollect", 1);
                } else {
//                    jo.put("ifCollect","未收藏");
                    jo.put("ifCollect", 2);
                }

                examErrorQuestionsArr.add(jo);
            }
        }
        return examErrorQuestionsArr;
    }

    // 9.  九、 删除已掌握错题中的某道题  lxj
    @Override
    public Map<String, Integer> deleteMasteredQuestions(String stuNumber, String openid, String subject, int questionId, String questionSource) {

        UserLogin userInfo = userLoginDao.findByDiyid(stuNumber);// 获取此用户的所有基本信息
        if (userInfo == null) {
            info = "暂时没有学号所对应的信息，请认真核对您的学号";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }

        Map<String, Integer> map = new LinkedHashMap<>();
        String userWrongQuestion = null;
        String examCategory1 = null;
        String examCategory2 = null;
        String examCategory = null;
        if (questionSource.equals("")){
            //错题本 删除已掌握的题
             userWrongQuestion = userWrongQustionDao.getIdBySubjectAndQuestionId(stuNumber, subject, questionId);
            if (userWrongQuestion == null) {
                info = "所删除的题在 已掌握错题 中暂时不存在";
                log.error("【错误信息】: {}", info);
                throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
            } else {
                userWrongQustionDao.deleteById(Integer.parseInt(userWrongQuestion));
                map.put("delete", 1);
            }
            return map;
        }
        else if (questionSource.equals("1")) {
            examCategory1 = "章节练习";
            examCategory2 = "专项练习";
            examCategory = "练习错题";
        } else if (questionSource.equals("2")) {
            examCategory1 = "模拟考试";
            examCategory2 = "历年真题";
            examCategory = "考试错题";
        }
        userWrongQuestion = userWrongQustionDao.getIdBySubjectAndQuestionIdAndExamCategory(stuNumber, subject, questionId, examCategory1, examCategory2);
        if (userWrongQuestion == null) {
            info = "所删除的题在" + examCategory + "的已掌握错题中暂时不存在";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        } else {
            userWrongQustionDao.deleteById(Integer.parseInt(userWrongQuestion));
            map.put("delete", 1);
        }

        return map;
    }

    // 10. 十、 做错题中未掌握的题，正确进入已掌握  lxj
    @Override
    public Map<String, Integer> doNotMasteredQuestions(String stuNumber, String openid, String subject, int questionId, String questionSource, String userAnswer, int examId, String examName) {
        UserLogin userInfo = userLoginDao.findByDiyid(stuNumber);// 获取此用户的所有基本信息
        if (userInfo == null) {
            info = "暂时没有学号所对应的信息，请认真核对您的学号";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }

        ExamPaper examPaper = examPaperDao.findByExamNameAndSubjectAndValid(examName, subject, 1);
        if (examPaper == null) {
            info = "暂时没有此科目的此试卷";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }

        List<Question> questions = questionDao.getQuestionInfoById(questionId, subject);
        if (questions == null || questions.size() == 0) {
            info = "该科目暂时没有这道题";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        Map<String, Integer> map = new LinkedHashMap<>();
        String examCategory1 = null;
        String examCategory2 = null;
        if (questionSource.equals("1")) {
            examCategory1 = "章节练习";
            examCategory2 = "专项练习";
        } else if (questionSource.equals("2")) {
            examCategory1 = "模拟考试";
            examCategory2 = "历年真题";
        }

        UserWrongQustion getWrongQuestion = userWrongQustionDao.getUserWrongQustionByUserAndSubjectAndExamCategory(stuNumber, subject, questionId, examId, examName, examCategory1, examCategory2);
        String correctText = questions.get(0).getCorrectText();
        int doRight;
        if (userAnswer.equals(correctText)) {
            doRight = 1;
        } else {
            doRight = 2;
        }
        Timestamp updateTime = new Timestamp(System.currentTimeMillis());
        getWrongQuestion.setDoRight(doRight);
        getWrongQuestion.setUserAnswer(userAnswer);
        getWrongQuestion.setUpdatetime(updateTime);
        userWrongQustionDao.save(getWrongQuestion);

        map.put("doRight", doRight);

        return map;
    }

    // 11. 十一、 专项练习：知识点中统计每章的题数  lxj
    @Override
    public Map<String, Integer> getQuestionsByChapterAndSubject(String subject, String levelName) {
        Map<String, Integer> map = new LinkedHashMap<>();

        // 根据学科获取章名称
        List<String> chapters = chapterDao.findChapterBySubject(subject, levelName);
        if (chapters == null || chapters.size() == 0) {
            info = "暂时没有此科目的此试卷";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        } else {
            for (int i = 0; i < chapters.size(); i++) {
                String chapter = chapters.get(i);
                // 根据章获取对应的题数
                int questionsNumsByChapter = questionDao.getQuestionsNumByChapter(subject, levelName, chapter);
                if (questionsNumsByChapter != 0) {
                    map.put(chapter, questionsNumsByChapter);
                }
            }
        }

        return map;
    }

    // 12. 十二、 专项练习：知识点中每章下所有知识点及对应的题数
    @Override
    public Map<String, Integer> getQuestionsNumsByAttribute(String subject, String levelName, String chapter) {
        Map<String, Integer> map = new LinkedHashMap<>();

        // 先根据年级、学科、章 获取知识点
        List<String> attributes = questionDao.getAttributesByChapter(subject, levelName, chapter);
        if (attributes == null || attributes.size() == 0) {
            info = "所查询的章下面没有相关知识点";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        } else {
            for (int i = 0; i < attributes.size(); i++) {
                String attribute = attributes.get(i);
                // 根据知识点获取对应的题数
                int getQuestionNumsByAttribute = questionDao.getQuestionsNumsByAttribute(attribute);
                if (getQuestionNumsByAttribute != 0) {
                    map.put(attribute, getQuestionNumsByAttribute);
                }
            }
        }

        return map;
    }

    // 13. 十三、 专项练习：根据章名称 和知识点查看题的详细信息
    @Override
    public JSONArray getQuestionsByQuestionsAttribute(String stuNumber, String openid, String subject, String levelName, String chapter, String attribute) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        JSONArray arr = new JSONArray();

        UserLogin userInfo = userLoginDao.findByDiyid(stuNumber);// 获取此用户的所有基本信息
        if (userInfo == null) {
            info = "暂时没有学号所对应的信息，请认真核对您的学号";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }

        List<Question> questions = questionDao.getQuestionsBySubjectAndChapterAndAttribute(subject, levelName, chapter, attribute);
        if (questions == null || questions.size() == 0) {
            info = "该科目、该章节、该知识点中暂时没有题";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        } else {
            for (int i = 0; i < questions.size(); i++) {
                JSONObject jo = new JSONObject();
                int id = questions.get(i).getId();
                jo.put("id", id);
                int exam_id = questions.get(i).getExamId();
                jo.put("exam_id", exam_id);
                String subjects = questions.get(i).getSubject();
                jo.put("subject", subjects);
                String question_source = questions.get(i).getQuestionSource();
                jo.put("question_source", question_source);
                String exam_name = questions.get(i).getExamName();
                jo.put("exam_name", exam_name);
                String exam_type = questions.get(i).getExamType();
                jo.put("exam_type", exam_type);
                String exam_location = questions.get(i).getExamLocation();
                jo.put("exam_location", exam_location);
                int question_id = questions.get(i).getQuestionId();
                jo.put("question_id", question_id);
                String question_type = questions.get(i).getQuestionType();
                jo.put("question_type", question_type);
                String question_difficult = questions.get(i).getQuestionDifficult();
                jo.put("question_difficult", question_difficult);
                String question_context = questions.get(i).getQuestionContext();
                question_context = filterspecial(question_context);
                jo.put("question_context", question_context);
                String question_image = questions.get(i).getQuestionImgs();
                jo.put("question_imgs", question_image);
                String question_option = questions.get(i).getQuestionOption();
                question_option = filterspecial(question_option);
                question_option = question_option.replaceAll(" ", "");
                log.info("【去除t,n等字符】： {}", question_option);
                int i1 = -1;
                if (question_option.indexOf("A．") != -1) {
                    i1 = question_option.indexOf("A．");
                } else {
                    i1 = question_option.indexOf("A.");
                }
                int i2 = -1;
                if (question_option.indexOf("B．") != -1) {
                    i2 = question_option.indexOf("B．");
                } else {
                    i2 = question_option.indexOf("B.");
                }
                int i3 = -1;
                if (question_option.indexOf("C．") != -1) {
                    i3 = question_option.indexOf("C．");
                } else {
                    i3 = question_option.indexOf("C.");
                }
                int i4 = -1;
                if (question_option.indexOf("D．") != -1) {
                    i4 = question_option.indexOf("D．");
                } else {
                    i4 = question_option.indexOf("D.");
                }
                List<String> optionList = new LinkedList<>();
                List<String> optionList1 = new LinkedList<>();
                List<String> optionList2 = new LinkedList<>();

                List<Integer> letterList = new ArrayList<>();
                letterList.add(i1);
                letterList.add(i2);
                letterList.add(i3);
                letterList.add(i4);

                String str1 = question_option.substring(i1 + 2, i2);//A选项
                String str2 = question_option.substring(i2 + 2, i3);//B选项
                String str3 = question_option.substring(i3 + 2, i4);//C选项
                String str4 = question_option.substring(i4 + 2, question_option.length());//D选项

                optionList.add(str1);
                optionList.add(str2);
                optionList.add(str3);
                optionList.add(str4);

                // 将选项内容做映射，请求全排列，
                Map<Integer, String> sortMap = new HashMap<>();

                sortMap.put(1, str1);
                sortMap.put(2, str2);
                sortMap.put(3, str3);
                sortMap.put(4, str4);
                optionList2.add("A.");
                optionList2.add("B.");
                optionList2.add("C.");
                optionList2.add("D.");

                int[] array = new int[]{1, 2, 3, 4};

                boolean contains = question_option.contains("E．");//判断选项中是否包含 D选项
                if (contains) {
                    int i5 = question_option.indexOf("E．");
                    letterList.add(i5);
                    String str5 = question_option.substring(i5 + 2, question_option.length());//E选项
                    optionList.add(str5);
                    sortMap.put(5, str5);
                    optionList2.add("E.");
                    array = new int[]{1, 2, 3, 4, 5};
                }

                // 这里 是 实现 随机选项的 方案 1： 使用的是random，随机性不高
                int[] ints = randomSort(array, 0);// 这个是随机函数，不是全排列函数
                for (int j = 0; j < ints.length; j++) {
                    String s = sortMap.get(ints[j]);
                    String s1 = optionList2.get(j);
                    optionList1.add(s1 + s);
                }

                // Collections.shuffle(list);//集合打乱顺序

                jo.put("question_option", optionList1);
                String question_score = String.valueOf(questions.get(i).getQuestionScore());
                jo.put("question_score", question_score);
                String question_attribute = questions.get(i).getQuestionAttribute();
                jo.put("question_attribute", question_attribute);
                String correct_option = questions.get(i).getCorrectOption();
                jo.put("correct_option", correct_option);
                String correct_text = questions.get(i).getCorrectText();
                jo.put("correct_text", correct_text);
                String correct_analysis = questions.get(i).getCorrectAnalysis();
                jo.put("correct_analysis", correct_analysis);
                int chapter_id = questions.get(i).getChapterId();
                jo.put("chapter_id", chapter_id);
                String level = questions.get(i).getLevel();
                jo.put("level", level);
                String level_name = questions.get(i).getLevelName();
                jo.put("level_name", level_name);
                String creater_user = questions.get(i).getCreateUser();
                jo.put("create_user", creater_user);
                String knowledge_module = questions.get(i).getKnowledgeModule();
                jo.put("knowledge_module", knowledge_module);
                String cognitive_level = questions.get(i).getCognitiveLevel();
                jo.put("cognitive_level", cognitive_level);
                String core_literacy = questions.get(i).getCoreLiteracy();
                jo.put("core_literacy", core_literacy);
                int valid = questions.get(i).getValid();
                jo.put("valid", valid);
                String inserttime = format.format(questions.get(i).getInserttime());
                jo.put("inserttime", inserttime);
                String updatetime = format.format(questions.get(i).getUpdatetime());
                jo.put("updatetime", updatetime);

                int ifCollect = userCollectDao.getIfCollectByStuNumAndQuestionId(stuNumber, subject, id);
                if (ifCollect == 1) {
//                    jo.put("ifCollect","已收藏");
                    jo.put("ifCollect", 1);
                } else {
//                    jo.put("ifCollect","未收藏");
                    jo.put("ifCollect", 2);
                }

                arr.add(jo);
            }
        }
        return arr;
    }

    // 14. 十四、 根据学科和年级统计用户做题记录
    @Override
    public JSONArray getDoQUestionRecord(String stuNumber, String openid, String subject, String levelName,String starTime,String endTime) {
        JSONArray arr = new JSONArray();

        // 得到试题名称和对应的题的总数
        // 根据学科和年级查询对应考试名称
        List<ExamPaper> examPapers = examPaperDao.getExamPaper(subject, levelName);
        // 查询所有知识点
        List<String> questionAttributes = questionDao.getQUestionAttribute(subject, levelName);
        if ((examPapers == null || examPapers.size() == 0) && (questionAttributes == null || questionAttributes.size() == 0)) {
            info = "该年级、该科目中暂时没有做题记录信息";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        } else {
            if ((examPapers != null) && (examPapers.size() != 0)) {
                for (int i = 0; i < examPapers.size(); i++) {
                    JSONObject jo = new JSONObject();
                    // 试卷名称
                    String examPaperName = examPapers.get(i).getExamName();
                    String examSource = examPapers.get(i).getExamSource();
                    // 试卷类型：章节练习
//                    String category="章节练习";

                    // 试卷总题数
                    int questionCount = examPapers.get(i).getQuestionCount();

                    // 根据试卷名称查询用户做题情况

                    List<UserQuestionRecord> userQuestionsRecord = null;
                    if (starTime.equals("") || endTime.equals("")){
                         userQuestionsRecord = userQuestionRecordDao.getUserQuestionRecord(stuNumber, examPaperName,examSource);
                    }else {
                        userQuestionsRecord = userQuestionRecordDao.getUserQuestionRecord2(stuNumber, examPaperName,starTime,endTime);
                    }

                    if (userQuestionsRecord != null && userQuestionsRecord.size() != 0) {
                        // 定义初始时间
                        String startTime = "00:00:00";
                        // 用户做了几道题
                        int doQuestionsNums = userQuestionsRecord.size();
                        // 完成率
                        String completeRate = "共(" + doQuestionsNums + "/" + questionCount + ")题";
                        // 统计用户正确题的个数
                        int doQuestionsRightNums = 0;
                        for (int j = 0; j < userQuestionsRecord.size(); j++) {
                            // 判断题是否做对，计算做对题数
                            int doRight = userQuestionsRecord.get(j).getDoRight();
                            if (doRight == 1) {
                                doQuestionsRightNums += 1;
                            }
                            // 得到做题时间
                            String doTime = userQuestionsRecord.get(j).getDoTime();
                            if (doTime.equals("")){
                                continue;
                            }
                            startTime = getTimeSum(startTime, doTime);

                        }

                        // 查询用户此试卷最新做题时间
                        String doTime = null;
                        if (starTime.equals("") || endTime.equals("")){
                             doTime = userQuestionRecordDao.getDoTimeByChapter(stuNumber, examPaperName, examSource);
                        }else {
                            doTime = userQuestionRecordDao.getDoTimeByChapter2(stuNumber, examPaperName, examSource,starTime,endTime);
                        }
                        if (doTime == null) {
                            info = "暂时没有做题时间信息";
                            log.error("【错误信息】: {}", info);
                            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
                        }
                        jo.put("doTime", doTime);// 做题时间
                        jo.put("examPaperName", examPaperName);// 试卷名称
                        jo.put("questionCount", questionCount);// 试卷总题数
                        jo.put("doQuestionsNums", doQuestionsNums);// 用户做题数
                        jo.put("doRightNums", doQuestionsRightNums);// 正确题数
                        jo.put("completeRate", completeRate);// 完成率
                        jo.put("examSource", examSource);// 试题来源
                        jo.put("doTimeLength", startTime);// 试题做题时长
                    }

                    if (!(jo.isEmpty() || jo.size() < 1)) {
                        arr.add(jo);
                    }
                }
            }
            if (questionAttributes != null && questionAttributes.size() != 0) {
                List<String> attributesList = new ArrayList<>();
                for (int j = 0; j < questionAttributes.size(); j++) {
                    // 得到知识点
                    String questionAttribute = questionAttributes.get(j);

                    if (questionAttribute.contains(",")) {
                        String[] attributeArr = questionAttribute.split(",");
                        for (int k = 0; k < attributeArr.length; k++) {
                            String attribute = attributeArr[k];
                            if (!(attributesList.contains(attribute))) {
                                attributesList.add(attribute);
                            }
                        }
                    } else {
                        if(!questionAttribute.equals("")){
                            attributesList.add(questionAttribute);
                        }
                    }
                }
                for (int l = 0; l < attributesList.size(); l++) {
                    JSONObject jo1 = new JSONObject();
                    String attributes = attributesList.get(l);
                    String category = "专项练习";

                    // 根据知识点查询题数
                    int questionCount = questionDao.getQuestionsNumsByAttribute(attributes);

                    // 根据知识点查询做题记录
                    List<UserQuestionRecord> getQuestionRecordByAttribute = null;
                    if (starTime.equals("") || endTime.equals("")){
                         getQuestionRecordByAttribute = userQuestionRecordDao.getQuestionsRecordByAttribute(stuNumber, attributes, category);
//                        getQuestionRecordByAttribute = userQuestionRecordDao.getUserQuestionRecordByKnowledgePoints2(stuNumber,subject,category,levelName);//3.2
                    }else {
                        getQuestionRecordByAttribute = userQuestionRecordDao.getQuestionsRecordByAttribute2(stuNumber, attributes, category,starTime,endTime);
//                        getQuestionRecordByAttribute = userQuestionRecordDao.getUserQuestionRecordByKnowledgePoints3(stuNumber,subject,category,levelName,starTime,endTime);//3.2
                    }

                    if ((getQuestionRecordByAttribute != null) && (getQuestionRecordByAttribute.size() != 0)) {
                        // 定义初始时间
                        String startTime = "00:00:00";

                        // 用户做了几道题
                        int doQuestionCount = getQuestionRecordByAttribute.size();

                        // 完成率
                        String completeRate = "共(" + doQuestionCount + "/" + questionCount + ")题";

                        // 统计用户正确题的个数
                        int doQuestionsRightNums = 0;

                        for (int m = 0; m < getQuestionRecordByAttribute.size(); m++) {
                            int doRight = getQuestionRecordByAttribute.get(m).getDoRight();
                            if (doRight == 1) {
                                doQuestionsRightNums += 1;
                            }

                            // 得到做题时间
                            String doTime = getQuestionRecordByAttribute.get(m).getDoTime();
                            startTime = getTimeSum(startTime, doTime);
                        }

                        // 查询用户此试卷最新做题时间
                        String doTimeByAttribute = null;
                        if (starTime.equals("") || endTime.equals("")){
                             doTimeByAttribute = userQuestionRecordDao.getDoTimeByAttribute(stuNumber, attributes, category);
                        }else {
                            doTimeByAttribute = userQuestionRecordDao.getDoTimeByAttribute2(stuNumber, attributes, category,starTime,endTime);
                        }

                        if (doTimeByAttribute == null) {
                            info = "暂时没有做题时间信息";
                            log.error("【错误信息】: {}", info);
                            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
                        }
                        jo1.put("doTime", doTimeByAttribute);// 做题时间
                        jo1.put("examPaperName", attributes);// 试卷名称
                        jo1.put("questionCount", questionCount);// 试卷总题数
                        jo1.put("doQuestionsNums", doQuestionCount);// 用户做题数
                        jo1.put("doRightNums", doQuestionsRightNums);// 正确题数
                        jo1.put("completeRate", completeRate);// 完成率
                        jo1.put("examSource", category);// 试题来源
                        jo1.put("doTimeLength", startTime);// 试题做题时长
                    }
                    if (!(jo1.isEmpty() || jo1.size() < 1)) {
                        arr.add(jo1);
                    }
                }
            }
        }

        JSONArray arrSorted = jsonArraySort(arr.toString());
        JSONArray arrSorted1 = new JSONArray();
        for (int k = 0; k < arrSorted.size(); k++) {
            JSONObject jsonObject = (JSONObject) arrSorted.get(k);
            String doTime = jsonObject.getString("doTime");
            Timestamp time = Timestamp.valueOf(doTime);
            doTime = new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(time);
            jsonObject.put("doTime", doTime);
            arrSorted1.add(jsonObject);
        }

        return arrSorted;
    }

    // 15. 十五、 做题记录中关于某一份试卷/章节/知识点做题详情(做题时间、题难易度等)
    @Override
    public JSONArray getDoQuestionRecordDetail(String stuNumber,String openid,String subject,String levelName,String examName,String source){
        JSONArray arr=new JSONArray();

        // 判断该学号学生是否存在
        UserLogin userInfo = userLoginDao.findByDiyid(stuNumber);// 获取此用户的所有基本信息
        if (userInfo == null) {
            info = "暂时没有学号所对应的信息，请认真核对您的学号";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }

        // 根据试卷名称查询做题记录详情
        List<UserQuestionRecord> userQuestionsRecord=null;

        if((source.equals("章节练习")) || (source.equals("模拟考试")) || (source.equals("历年真题"))){
            userQuestionsRecord=userQuestionRecordDao.getUserQuestionRecord(stuNumber,examName,source);
        }else if(source.equals("专项练习")){
            userQuestionsRecord=userQuestionRecordDao.getUserQuestionRecordByKnowledgePoints2(stuNumber,subject,source,levelName);
        }

        if (userQuestionsRecord == null || userQuestionsRecord.size() == 0) {
            info = "对于该试卷，该学生暂时没有做题记录";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        } else {
            for (int i = 0; i < userQuestionsRecord.size(); i++) {
                JSONObject jo = new JSONObject();
                // 整个题表中的id，用于去题表中查询相关信息
                int questionid = userQuestionsRecord.get(i).getQuestionId();
                // 做题是否正确
                int doRight = userQuestionsRecord.get(i).getDoRight();
                // 做题时间(时长)
                String doTime = userQuestionsRecord.get(i).getDoTime();

                // 根据题表中的id去查询该题对应的信息(包括题的难易程度和知识点)
                List<Question> questions = questionDao.getQuestionInfoById(questionid, subject);
                // 该试卷中的题号
                int questionIdByExamName = questions.get(0).getQuestionId();
                // 试题难易度
                String questionDifficult = questions.get(0).getQuestionDifficult();
                // 试题知识点
                String questionAttribute = questions.get(0).getQuestionAttribute();

                jo.put("id", questionid);// 试题id
                jo.put("questionId", questionIdByExamName);// 试题在该试卷中的id
                jo.put("doRight", doRight);// 做题是否正确
                jo.put("doTime", doTime);// 做题所用时长
                jo.put("questionDifficult", questionDifficult);// 试题难易度
                jo.put("questionAttribute", questionAttribute);// 试题知识点

                arr.add(jo);
            }
        }
        return arr;
    }

    // 16. 十六、 学习记录中：统计做题数
    @Override
    public JSONArray getDoQuestionsCount(String stuNumber, String openid, String subject, String levelName) {
        JSONArray arr = new JSONArray();

        UserLogin userInfo = userLoginDao.findByDiyid(stuNumber);// 获取此用户的所有基本信息
        if (userInfo == null) {
            info = "暂时没有学号所对应的信息，请认真核对您的学号";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }

        // 先根据用户、学科、年级得到用户做题时间(年月日)
        List<String> getDoQuestionsDate = userQuestionRecordDao.getDoQuestionsDate(stuNumber, subject, levelName);
        if (getDoQuestionsDate == null || getDoQuestionsDate.size() == 0) {
            info = "该学生暂时没有做题记录";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        } else {
            for (int i = 0; i < getDoQuestionsDate.size(); i++) {
                JSONObject jo = new JSONObject();
                // 得到做题时间
                String doDate = getDoQuestionsDate.get(i);

                // 根据做题时间和用户学号查询用户当天做题数
                int doCount = userQuestionRecordDao.getDoQUestionsNumsByDate(stuNumber, doDate);

                jo.put("stuNumber", stuNumber);// 用户学号
                jo.put("doDate", doDate);// 做题时间
                jo.put("doCount", doCount);// 当天做题数

                arr.add(jo);
            }
        }

        return arr;
    }

    // 17. 十七、 学习记录：统计错题和收藏总数
    @Override
    public Map<String, Integer> getWrongCollectQuestionsCount(String stuNumber, String openid, String subject, String levelName) {
        Map<String, Integer> map = new LinkedHashMap<>();

        UserLogin userInfo = userLoginDao.findByDiyid(stuNumber);// 获取此用户的所有基本信息
        if (userInfo == null) {
            info = "暂时没有学号所对应的信息，请认真核对您的学号";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }

        // 统计用户已掌握错题数
        int doRight = 1;
        int masteredErrorQuestionsCount = userWrongQustionDao.getMasteredErrorQuestionsCountByGradeLevel(stuNumber, levelName, subject, doRight);

        // 统计用户未掌握错题数
        doRight = 2;
        int notMasteredErrorQuestionsCount = userWrongQustionDao.getMasteredErrorQuestionsCountByGradeLevel(stuNumber, levelName, subject, doRight);

        // 统计用户收藏题总数
        int collectQuestionsCount = userCollectDao.getCollectCountByGradeLevel(stuNumber, levelName, subject);

        map.put("masteredErrorQuestionsCount", masteredErrorQuestionsCount);// 已掌握题数
        map.put("notMasteredErrorQuestionsCount", notMasteredErrorQuestionsCount);// 未掌握题数
        map.put("collectQuestionsCount", collectQuestionsCount);// 收藏题数

        return map;
    }

    // 18. 十八、 学习记录：按天统计做题正确率和做题时长
    @Override
    public JSONArray getRightRateAndClassHours(String stuNumber, String openid, String subject, String levelName) {
        JSONArray arr = new JSONArray();

        SimpleDateFormat myFormatter = new SimpleDateFormat("HH:mm:ss");

        UserLogin userInfo = userLoginDao.findByDiyid(stuNumber);// 获取此用户的所有基本信息
        if (userInfo == null) {
            info = "暂时没有学号所对应的信息，请认真核对您的学号";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }

        // 先根据用户、学科、年级得到用户做题时间(年月日)
        List<String> getDoQuestionsDate = userQuestionRecordDao.getDoQuestionsDate(stuNumber, subject, levelName);
        if (getDoQuestionsDate == null || getDoQuestionsDate.size() == 0) {
            info = "该学生暂时没有做题记录";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        } else {
            for (int i = 0; i < getDoQuestionsDate.size(); i++) {
                JSONObject jo = new JSONObject();
                // 得到做题时间
                String doDate = getDoQuestionsDate.get(i);

                // 根据做题时间和用户学号查询用户当天做题数
                int doCount = userQuestionRecordDao.getDoQUestionsNumsByDate(stuNumber, doDate);

                // 根据做题时间和用户学号查询用户当天做对题数
                int doRight = 1;
                int doRightCount = userQuestionRecordDao.getDoQuestionsRightNumsByDate(stuNumber, doDate, doRight);

                // 计算正确率
                float rightRate = (float) doRightCount / doCount;
                jo.put("rightRate1", rightRate);// 正确率
                // 保留三位小数
                DecimalFormat df = new DecimalFormat("0.000");
                String rightRateThree = df.format(rightRate);

                // 每一天，设置初始时长为00:00:00
                String startTime = "00:00:00";

                String[] startLength = startTime.split(":");
                int hour = Integer.parseInt(startLength[0]);
                int mins = Integer.parseInt(startLength[1]);
                int sec = Integer.parseInt(startLength[2]);

                List<String> doTimeList = userQuestionRecordDao.getDoQuestionsTimeList(stuNumber, doDate);
                if (doTimeList == null || doTimeList.size() == 0) {
                    info = "该学生暂时没有做题记录";
                    log.error("【错误信息】: {}", info);
                    throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
                } else {

                    for (int j = 0; j < doTimeList.size(); j++) {
                        String doTime = doTimeList.get(j);
                        if (doTime.equals("")) {
                            continue;
                        }
                        String[] doLength = doTime.split(":");
                        int doHour = Integer.parseInt(doLength[0]);
                        int doMins = Integer.parseInt(doLength[1]);
                        int doSec = Integer.parseInt(doLength[2]);

                        sec = sec + doSec;
                        mins = mins + doMins;
                        hour = hour + doHour;

                        if (sec >= 60) {
                            sec = 00;
                            if (mins >= 60) {
                                mins = 00;
                                hour = hour + 1;
                            } else {
                                mins = mins + 1;
                            }
                        }
                    }
                }

                String hours = "", minss = "", secs = "";
                if (hour < 10) {
                    hours = "0" + hour;
                } else {
                    hours = "" + hour;
                }
                if (mins < 10) {
                    minss = "0" + mins;
                } else {
                    minss = "" + mins;
                }
                if (sec < 10) {
                    secs = "0" + sec;
                } else {
                    secs = "" + sec;
                }
                String doTime = hours + ":" + minss + ":" + secs;

                jo.put("doDate", doDate);// 做题日期
                jo.put("doCount", doCount);// 做题总数
                jo.put("doRightCount", doRightCount);// 正确题数
                jo.put("rightRate", rightRateThree);// 正确率
                jo.put("doTimeLength", doTime);// 当天做题时长

                arr.add(jo);
            }
        }

        return arr;
    }

    // 19. 十九、 学习记录：上面三个数的统计
    @Override
    public JSONObject getPracticeRecord(String stuNumber,String openid,String subject,String levelName){
        JSONObject json=new JSONObject();

        UserLogin userInfo = userLoginDao.findByDiyid(stuNumber);// 获取此用户的所有基本信息
        if (userInfo == null) {
            info = "暂时没有学号所对应的信息，请认真核对您的学号";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }

        // 得到用户做题总数
        int doQuestionCount=userQuestionRecordDao.getDoQuestionCount(stuNumber,subject,levelName);

        // 查询用户每道题做题时长，计算用户做题总时间
        // 每一天，设置初始时长为00:00:00
        String startTime = "00:00:00";
        List<String> doQuestionTime=userQuestionRecordDao.getDoQUestionTime(stuNumber,subject,levelName);
        if(doQuestionTime!=null && doQuestionTime.size()!=0){
            for(int i=0;i<doQuestionTime.size();i++){
                String doTime=doQuestionTime.get(i);
                if(!(doTime.equals("") || doTime.equals(null))) {
                    startTime = getTimeSum(startTime, doTime);
                }
            }
        }

  /*      // 查询用户每道题做题时间，用户统计用户连续做题的天数
        List<UserQuestionRecord> doQuestionUpdatetime=userQuestionRecordDao.getDoQuestionUpdatetime(stuNumber,subject,levelName);
        if(doQuestionUpdatetime!=null && doQuestionUpdatetime.size()!=0){
            for(int i=0;i<doQuestionUpdatetime.size();i++){
                Timestamp updatetime=doQuestionUpdatetime.get(i).getUpdatetime();
                long time=updatetime.getTime()+(long)1000*3600*24;
                Timestamp updatetime1 =new Timestamp(time);
                
            }
        }*/
        int doQuestiondays = userQuestionRecordDao.getDoQuestionDays(stuNumber,subject,levelName);
        json.put("doQuestionCount",doQuestionCount);// 做题总数
        json.put("doTimeTotal",startTime);// 做题总时间
        json.put("doQuestiondays",doQuestiondays);// 做题天数
        return json;
    }

    // json对象根据其中的时间字段进行倒序排序 lxj
    public static JSONArray jsonArraySort(String jsonArrStr) {
        JSONArray jsonArr = JSON.parseArray(jsonArrStr);
        JSONArray sortedJsonArray = new JSONArray();
        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArr.size(); i++) {
            jsonValues.add(jsonArr.getJSONObject(i));
        }
        Collections.sort(jsonValues, new Comparator<JSONObject>() {
            // You can change "Name" with "ID" if you want to sort by ID
            private static final String KEY_NAME = "doTime";

            @Override
            public int compare(JSONObject a, JSONObject b) {
                String valA = new String();
                String valB = new String();
                try {
                    // 这里是a、b需要处理的业务，需要根据你的规则进行修改。
                    String aStr = a.getString(KEY_NAME);
                    valA = aStr.replaceAll("-", "");
                    valA = valA.replaceAll(":", "");
                    valA = valA.replaceAll(" ", "");
                    String bStr = b.getString(KEY_NAME);
                    valB = bStr.replaceAll("-", "");
                    valB = valB.replaceAll(":", "");
                    valB = valB.replaceAll(" ", "");
                } catch (JSONException e) {
                    // do something
                }
                return -valA.compareTo(valB);
                // if you want to change the sort order, simply use the following:
                // return -valA.compareTo(valB);
            }
        });
        for (int i = 0; i < jsonArr.size(); i++) {
            sortedJsonArray.add(jsonValues.get(i));
        }
        return sortedJsonArray;
    }

    // 时间相加：格式00:00:00
    public static String getTimeSum(String time, String doTime) {
        String[] length = time.split(":");
        int hour = Integer.parseInt(length[0]);
        int mins = Integer.parseInt(length[1]);
        int sec = Integer.parseInt(length[2]);

        String[] doLength = doTime.split(":");
        int doHour = Integer.parseInt(doLength[0]);
        int doMins = Integer.parseInt(doLength[1]);
        int doSec = Integer.parseInt(doLength[2]);

        sec = sec + doSec;
        mins = mins + doMins;
        hour = hour + doHour;

        if (sec >= 60) {
            sec = 00;
            if (mins >= 60) {
                mins = 00;
                hour = hour + 1;
            } else {
                mins = mins + 1;
            }
        }

        String hours = "", minss = "", secs = "";
        if (hour < 10) {
            hours = "0" + hour;
        } else {
            hours = "" + hour;
        }
        if (mins < 10) {
            minss = "0" + mins;
        } else {
            minss = "" + mins;
        }
        if (sec < 10) {
            secs = "0" + sec;
        } else {
            secs = "" + sec;
        }
        String doTimeNum = hours + ":" + minss + ":" + secs;

        return doTimeNum;
    }

}
