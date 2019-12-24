package com.zgczx.service.exam;


import com.zgczx.repository.mysql1.exam.dto.*;
import com.zgczx.repository.mysql1.exam.model.Question;
import com.zgczx.repository.mysql1.exam.model.UserCollect;
import com.zgczx.repository.mysql1.exam.model.UserPaperRecord;
import com.zgczx.repository.mysql1.exam.model.UserQuestionRecord;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author aml
 * @date 2019/12/11 15:44
 */
public interface ExamService {

    String parseWord(MultipartFile file, HttpSession session, HttpServletRequest request);

    List<String> getAllChapter(String levelName,String subject);

    List<String> getAllSection(String levelName,String chapter,String subject);

    List<Question> splitExam(String examName, String subject);

    List<QuestionDTO> findExamQuestionInfo(String examName, String subject,String studentNumber,String openid);

    DoQuestionInfoDTO judgeQuestionRight(int id, String studentNumber, String openid, String commitString, String paperName, String subject, int sourcePaperId);

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
    ChapterErrNumberDTO getChapterErrNumber(String stuNumber,String openid,String subject);

    /**
     * 十三、用户回显此时卷所有信息
     * @param stuNumber
     * @param openid
     * @param subject
     * @param examName
     * @return
     */
    List<EchoPaperCompleteDTO> echoPaperInfo(String stuNumber, String openid, String subject, String examName);
}
