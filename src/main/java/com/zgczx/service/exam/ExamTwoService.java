package com.zgczx.service.exam;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
* @author lxj
* @data 2019/12/30
* */
public interface ExamTwoService {

    // 1. 我的错题中：总错题数、章节错题数、考试错题数
    public Map<String, Integer> getErrorProblemsNum(String stuNumber, String openid, String subject);

    // 2. 我的收藏：总收藏数、章节收藏数、考试收藏数
    public Map<String, Integer> getCollectProblemsNum(String stuNumber, String openid, String subject);

    // 3. 我的收藏：默认选择练习题收藏(每一章及收藏的题数)，选择考试题收藏时，显示对应的考试名称和收藏题数
    public Map<String ,Integer> getCollectProblemsNum(String stuNumber, String openid, String subject, String category);

    // 4. 点开章，根据章的名称获取  节的名称和对应收藏的题数
    public Map<String ,Integer> getCollectProblemsNumByChapter(String studentNumber,String openid,String subject, String chapter);

    // 5.  根据章节名称查询收藏的题的详细信息 lxj
    public JSONArray getSectionCollectProblems(String stuNumber, String openid, String subject, String chapter, String section);

    // 6.  根据考试名称查询收藏的题的详细信息 lxj
    public JSONArray getExamCollectProblems(String stuNumber,String openid,String subject,String examName);

    // 7.  七、 练习错题：点击章、节显示 此小节错题的详细信息 lxj
    public JSONArray getErrorProblemsByChapterAndSection(String stuNumber, String openid, String subject, String chapter, String section,String ifMastered);

    // 8.  八、 考试错题：点击某次考试，显示本次考试错题的详细信息 lxj
    public JSONArray getErrorProblemsByExamName(String stuNumber, String openid, String subject, String examName,String ifMastered);

    // 9.  九、 删除已掌握错题中的某道题  lxj
    public Map<String ,Integer> deleteMasteredQuestions(String stuNumber,String openid,String subject,int questionId,String questionSource);

    // 10. 十、 做错题中未掌握的题，正确进入已掌握  lxj
    public Map<String ,Integer> doNotMasteredQuestions(String stuNumber,String openid,String subject,int questionId,String questionSource,String userAnswer,int examPaperId,String examPaperName);

    // 11. 十一、 专项练习：知识点中统计每章的题数  lxj
    public Map<String ,Integer> getQuestionsByChapterAndSubject(String subject,String levelName);

    // 12. 十二、 专项练习：知识点中每章下所有知识点及对应的题数  lxj
    public Map<String ,Integer> getQuestionsNumsByAttribute(String subject, String levelName, String chapter);

    // 13. 十三、 专项练习：根据章名称 和知识点查看题的详细信息  lxj
    public JSONArray getQuestionsByQuestionsAttribute(String stuNumber, String openid, String subject,String levelName, String chapter,String attribute);

    // 14. 十四、 根据学科和年级统计用户做题记录
    public JSONArray getDoQUestionRecord(String stuNumber,String openid,String subject,String levelName,String starTime,String endTime);

    // 15. 十五、 做题记录中关于某一份试卷/章节/知识点做题详情(做题时间、题难易度等)
    public JSONArray getDoQuestionRecordDetail(String stuNumber,String openid,String subject,String levelName,String examName,String source);

    // 16. 十六、 学习记录中：统计做题数
    public JSONArray getDoQuestionsCount(String stuNumber,String openid,String subject,String levelName);

    // 17. 十七、 学习记录：统计错题和收藏总数
    public Map<String ,Integer> getWrongCollectQuestionsCount(String stuNumber,String openid,String subject,String levelName);

    // 18. 十八、 学习记录：按天统计做题正确率和做题时长
    public JSONArray getRightRateAndClassHours(String stuNumber,String openid,String subject,String levelName);

    // 19. 十九、 学习记录：上面三个数的统计
    public JSONObject getPracticeRecord(String stuNumber, String openid, String subject, String levelName);
}
