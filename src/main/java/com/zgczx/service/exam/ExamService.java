package com.zgczx.service.exam;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zgczx.repository.mysql1.exam.dto.*;
import com.zgczx.repository.mysql1.exam.model.Question;
import com.zgczx.repository.mysql1.exam.model.UserCollect;
import com.zgczx.repository.mysql1.exam.model.UserPaperRecord;
import com.zgczx.repository.mysql1.exam.model.UserQuestionRecord;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * @author aml
 * @date 2019/12/11 15:44
 */
public interface ExamService {

    String parseWord(MultipartFile file, HttpSession session, HttpServletRequest request);

    List<String> getAllChapter(String levelName,String subject);

    List<String> getAllSection(String levelName,String chapter,String subject);

    List<Question> splitExam(String examName, String subject);

    List<QuestionDTO> findExamQuestionInfo(String examName, String subject,String studentNumber,String openid,String gradeLevel);

    DoQuestionInfoDTO judgeQuestionRight(int id, String studentNumber, String openid, String commitString, String paperName, String subject, int sourcePaperId,String gradeLevel,String doTime);

//    UserCollect insertCollect(int id, String studentNumber, String openid, String classification,String commitString);
    UserCollect insertCollect(int id, String studentNumber, String openid, String classification);

    DoQuestionInfoDTO getDoQuestionInfo(String studentNumber, String examName, String subject, int sourcePaperId);

    List<EchoDoQuestionDTO> echoDoQuestionInfo(String studentNumber, String examName, String subject);

    UserCollect cancelCollect(int id,String studentNumber, String openid,String examName, String subject, int cancel);

    /**
     *  十一、 用户整套试卷（也可以是一章中的一节题）的记录
     * @param studentNumber
     * @param openid
     * @param examName
     * @param subject
     * @param examPaperContent
     * @param examPaperAnwer
     * @return
     */
    UserPaperRecord fullPaperRecord(String studentNumber,String openid,String examName,String subject,String examPaperContent,String examPaperAnwer);

    /**
     * 十二、点击练习错题时，展现的章节名称和对应的错题数量
     * @param stuNumber
     * @param openid
     * @param subject
     * @return
     */
    JSONObject getChapterErrNumber(String stuNumber, String openid, String subject, String examCategory);

    /**
     * 十三、用户回显此时卷所有信息
     * @param stuNumber
     * @param openid
     * @param subject
     * @param examName
     * @return
     */
    List<EchoPaperCompleteDTO> echoPaperInfo(String stuNumber, String openid, String subject, String examName);

    /**
     * 十四、查询此题是否收藏过
     * @param id
     * @return
     */
    FindCollectDTO findCollectInfo(String stuNumber,String subject,int questionId);

    /**
     * 十五、获取此章下面的所有节的名称和对应的错题数量,根据章的名称
     * @param stuNumber
     * @param openid
     * @param subject
     * @param chapterName
     * @return
     */
    SectionErrNumberDTO getSectionErrNumber(String stuNumber,String openid,String subject,String chapterName,String ifMastered);

    // 十六、错题本：获取某类别所有未掌握题的所有情况
    JSONObject getNotMasteredInfo(String studentNumber,String openid,String subject,String examCategory,String gradeLevel, int master);

    // 十七、错题本中的 下面的分类详情
    JSONObject getClassification(String studentNumber,String openid,String subject,String examCategory,String gradeLevel, int master);

    // 十八、错题本：统计分类中 未掌握或已掌握的 各分类的数量
    JSONObject getClassificationQuantity(String studentNumber, String openid, String subject, String gradeLevel, int master);
    //十九、错题表：获取 此题的所有信息
    JSONObject getQuestionInfo(int id,String stuNumber,String openid);
    //二十、专项练习： 获取此年级、科目所有的知识点
    JSONObject getAllKnowledge(String studentNumber,String openid,String subject,String gradeLevel);
    //二十一、专项练习： 专项练习：根据知识点获取所有相关的题
    JSONArray getAllQuestionByPoint(String studentNumber,String openid,String subject,String gradeLevel,String knowledgePoint);

    //二十二、 专项练习： 记录用户做某道题 到用户记录中
    JSONObject specialRecordId(int id, String studentNumber, String openid, String commitString, String examCategory,String subject,String gradeLevel,String doTime);

    //二十三、历年真题、模拟考试：获取此年级、科目的分類的各个考试名称和题数
    JSONObject getAllExamName(String studentNumber,String openid,String subject,String gradeLevel,String examCategory);

    //二十四、首页面中继续学习-仅章节练习
    JSONObject continueLearn(String studentNumber, String openid,String subject);

}
