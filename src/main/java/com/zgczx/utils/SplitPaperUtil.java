package com.zgczx.utils;

import com.zgczx.enums.ResultEnum;
import com.zgczx.exception.ScoreException;
import com.zgczx.repository.mysql1.exam.dao.ChapterDao;
import com.zgczx.repository.mysql1.exam.dao.ExamPaperDao;
import com.zgczx.repository.mysql1.exam.dao.QuestionDao;
import com.zgczx.repository.mysql1.exam.model.ExamPaper;
import com.zgczx.repository.mysql1.exam.model.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.zgczx.utils.FilterStringUtil.filterAlphabetCapital;

/**
 * 切分试卷工具类
 *
 * @author aml
 * @date 2019/12/12 15:24
 */
@Slf4j
public class SplitPaperUtil {

    @Autowired
    private ChapterDao chapterDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private ExamPaperDao examPaperDao;

    private String info;

    @Transactional
    public List<Question> splitExam(String examName, String subject) {
        ExamPaper examPaper = examPaperDao.findByExamNameAndSubjectAndValid(examName, subject, 1);
        if (examPaper == null) {
            info = "暂时没有此科目的此试卷";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        String examContent = examPaper.getExamContent();
        List<String> stringList = new ArrayList<>();
        int i1 = examContent.indexOf("1．");
        int i2 = examContent.indexOf("2．");
        int i3 = examContent.indexOf("3．");
        int i4 = examContent.indexOf("4．");
        int i5 = examContent.indexOf("5．");
        int i6 = examContent.indexOf("6．");
        int i7 = examContent.indexOf("7．");
        int i8 = examContent.indexOf("8．");
        int i9 = examContent.indexOf("9．");
        int i10 = examContent.indexOf("10．");
        String str1 = examContent.substring(i1, i2);
        String str2 = examContent.substring(i2, i3);
        String str3 = examContent.substring(i3, i4);
        String str4 = examContent.substring(i4, i5);
        String str5 = examContent.substring(i5, i6);
        String str6 = examContent.substring(i6, i7);
        String str7 = examContent.substring(i7, i8);
        String str8 = examContent.substring(i8, i9);
        String str9 = examContent.substring(i9, i10);
        String str10 = examContent.substring(i10, examContent.length());
        stringList.add(str1);
        stringList.add(str2);
        stringList.add(str3);
        stringList.add(str4);
        stringList.add(str5);
        stringList.add(str6);
        stringList.add(str7);
        stringList.add(str8);
        stringList.add(str9);
        stringList.add(str10);

        for (int i = 0; i < stringList.size(); i++) {
            Question question = new Question();
            String s = stringList.get(i);
            question.setExamId(examPaper.getId());
            question.setQuestionSource("模拟考试");
            question.setExamName("3.1 细胞膜的结构和功能");
            question.setExamLocation("北京");
            question.setQuestionId(i + 1);
            question.setQuestionType("单选");
            question.setQuestionDifficult("一般");
            question.setQuestionContext(s);
            question.setQuestionAttribute("细胞膜的结构和功能");
            int a = s.indexOf("A");
            int b = s.indexOf("【答案】");
            int c = s.indexOf("【解析】");
            String option = s.substring(a, b);
            String correctOption1 = s.substring(b, c);//此题的正确答案选项；
            String correctOption = filterAlphabetCapital(correctOption1);
            //切分所有选项 A-D，判断答案是哪个选项，然后存哪个选项的文本；
            int b1 = s.indexOf("B");
            int c1 = s.indexOf("C");
            int d1 = s.indexOf("D");
            String contentA = s.substring(a, b1);
            String contentB = s.substring(b1, c1);
            String contentC = s.substring(c1, d1);
            String contentD = s.substring(d1, b);
            question.setQuestionOption(option);//题的选项
            question.setCorrectOption(correctOption);//题的正确答案选项
            question.setCorrectAnalysis(s.substring(c, s.length()));//答案的正确内容
            if (correctOption.trim().equals("A")) {
                question.setCorrectText(contentA);//正确答案的文本
            } else if (correctOption.trim().equals("B")) {
                question.setCorrectText(contentB);
            } else if (correctOption.trim().equals("C")) {
                question.setCorrectText(contentC);
            } else {
                question.setCorrectText(contentD);
            }
            question.setValid(1);//1：此数据有效
            Question save = questionDao.save(question);
        }
        List<Question> list = questionDao.findByExamName("3.1 细胞膜的结构和功能");
        List<Integer> list1 = new ArrayList<>();
        for (Question question : list) {
            list1.add(question.getId());
        }
        examPaper.setQuestionList(String.valueOf(list1));
        ExamPaper save = examPaperDao.save(examPaper);
        log.info("【试卷表的详细信息:】 {}", save);
        return list;

    }
}
