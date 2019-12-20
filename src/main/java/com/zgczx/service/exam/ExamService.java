package com.zgczx.service.exam;


import com.zgczx.repository.mysql1.exam.dto.DoQuestionInfoDTO;
import com.zgczx.repository.mysql1.exam.dto.EchoDoQuestionDTO;
import com.zgczx.repository.mysql1.exam.dto.QuestionDTO;
import com.zgczx.repository.mysql1.exam.model.Question;
import com.zgczx.repository.mysql1.exam.model.UserCollect;
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
}
