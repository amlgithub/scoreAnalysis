package com.zgczx.service.exam;


import com.zgczx.repository.mysql1.exam.dto.QuestionDTO;
import com.zgczx.repository.mysql1.exam.model.Question;
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

    List<QuestionDTO> findExamQuestionInfo(String examName, String subject);

    UserQuestionRecord judgeQuestionRight(int id, String studentNumber, String openid, String commitString);
}
