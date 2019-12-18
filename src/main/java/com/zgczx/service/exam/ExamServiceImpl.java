package com.zgczx.service.exam;

import com.zgczx.enums.ResultEnum;
import com.zgczx.exception.ScoreException;
import com.zgczx.repository.mysql1.exam.dao.*;
import com.zgczx.repository.mysql1.exam.dto.DoQuestionInfoDTO;
import com.zgczx.repository.mysql1.exam.dto.QuestionDTO;
import com.zgczx.repository.mysql1.exam.model.ExamPaper;
import com.zgczx.repository.mysql1.exam.model.Question;
import com.zgczx.repository.mysql1.exam.model.UserCollect;
import com.zgczx.repository.mysql1.exam.model.UserQuestionRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zgczx.utils.FilterStringUtil.*;
import static com.zgczx.utils.FilterStringUtil.filterspecial;
import static com.zgczx.utils.FullPermutationUtil.l;
import static com.zgczx.utils.RecursionTreeUtil.permute;
import static com.zgczx.utils.FullPermutationUtil.permute2;
import static com.zgczx.utils.RecursionTreeUtil.randomSort;
import static com.zgczx.utils.WordRedUtil.readWord;

/**
 * @author aml
 * @date 2019/12/11 15:48
 */
@Service
@Slf4j
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ChapterDao chapterDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private ExamPaperDao examPaperDao;

    @Autowired
    private UserQuestionRecordDao userQuestionRecordDao;

    @Autowired
    private UserCollectDao userCollectDao;

    private String info;

    @Override
    public String parseWord(MultipartFile file, HttpSession session, HttpServletRequest request) {
        String text = null;
        try {
            text = readWord(file);

            System.out.println("打印text： " + text);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    @Override
    public List<String> getAllChapter(String levelName, String subject) {
        List<String> name = chapterDao.findByLevelNameAndSubject(levelName,subject);
        if (name == null || name.size() == 0) {
            info = "暂时没有此年级的章目";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }

        return name;
    }

    @Override
    public List<String> getAllSection(String levelName, String chapter, String subject) {
        List<String> name = chapterDao.findByLevelNameAndChapterAndSubject(levelName, chapter,subject);
        if (name == null || name.size() == 0) {
            info = "暂时没有此年级此章目的小节";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        log.info(String.valueOf(name));
        return name;
    }

    @Transactional
    @Override
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

    @Override
    public List<QuestionDTO> findExamQuestionInfo(String examName, String subject) {
        ExamPaper examPaper = examPaperDao.findByExamNameAndSubjectAndValid(examName, subject, 1);
        if (examPaper == null) {
            info = "暂时没有此科目的此试卷";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        // 去除[] 和 空格，或者从插库时处理，直接就存1,2,3... ；而不是存成[1, 2, 3...]
        String[] questionList = filterMiddleBrackets(examPaper.getQuestionList()).split(",");

        List<Integer> idList = new ArrayList<>();
        for (int i = 0; i < questionList.length; i++) {
            int integer = Integer.parseInt(questionList[i]);
            idList.add(integer);
            //System.out.println(idList);
        }
        List<QuestionDTO> list = new ArrayList<>();
        for (Integer integer : idList){
            QuestionDTO questionDTO = new QuestionDTO();
            Question one = questionDao.findOne(integer);
            questionDTO.setQuestion(one);

            List<String> optionList = new LinkedList<>();
            List<String> optionList1 = new LinkedList<>();
            List<String> optionList2 = new LinkedList<>();

            List<Map<String, String>> optionMapList = new LinkedList<>();
            Map<String, String> map = new HashMap<>();

            String oneQuestionOption = one.getQuestionOption();//获取所有选项的文本
            String questionOption = filterspecial(oneQuestionOption);//过滤下\t,\n等字符

            log.info("【去除t,n等字符】： {}",questionOption);
            int i1 = questionOption.indexOf("A．");
            int i2 = questionOption.indexOf("B．");
            int i3 = questionOption.indexOf("C．");
            int i4 = questionOption.indexOf("D．");

            List<Integer> letterList = new ArrayList<>();
            letterList.add(i1);
            letterList.add(i2);
            letterList.add(i3);
            letterList.add(i4);

//            String str1 = questionOption.substring(i1, i2);//A选项
//            String str2 = questionOption.substring(i2, i3);//B选项
//            String str3 = questionOption.substring(i3, i4);//C选项
//            String str4 = questionOption.substring(i4, questionOption.length());//D选项
            String str1 = questionOption.substring(i1+2, i2);//A选项
            String str2 = questionOption.substring(i2+2, i3);//B选项
            String str3 = questionOption.substring(i3+2, i4);//C选项
            String str4 = questionOption.substring(i4+2, questionOption.length());//D选项

            optionList.add(str1);
            optionList.add(str2);
            optionList.add(str3);
            optionList.add(str4);

            // 将选项内容做映射，请求全排列，
            Map<Integer, String> sortMap = new HashMap<>();

            sortMap.put(1,str1);
            sortMap.put(2,str2);
            sortMap.put(3,str3);
            sortMap.put(4,str4);
            optionList2.add(questionOption.substring(i1, i1+2));
            optionList2.add(questionOption.substring(i2, i2+2));
            optionList2.add(questionOption.substring(i3, i3+2));
            optionList2.add(questionOption.substring(i4, i4+2));

            int[] array = new int[]{1,2,3,4};

            boolean contains = questionOption.contains("E．");//判断选项中是否包含 D选项
            if (contains){
                int i5 = questionOption.indexOf("E．");
                letterList.add(i5);
                String str5 = questionOption.substring(i5+2, questionOption.length());//E选项
                optionList.add(str5);
                sortMap.put(5,str5);
                optionList2.add(questionOption.substring(i5, i5+2));
                array = new int[]{1,2,3,4,5};
            }
/*
  这里 是 实现 随机选项的 方案 2： 使用的是 全排列，随机性高，但是 递归过程慢，耗时长
 */
//            permute2(array,0);// 调用 全排列的递归函数
//            int random = (int) (Math.random() * (l.size() - 1) + 1); // 从递归得到的全排列中获取 任意的 一个结果
//            String s2 = filterMiddleBrackets(l.get(random));
//            String replace = s2.replace("\"", "");
//            String[] split = replace.split(",");
//            for (int i = 0; i < split.length; i++){
//                String s3 = split[i];
//                int s4 = Integer.parseInt(s3.replaceAll("\"", ""));
//                String s = sortMap.get(s4);
//                String s1 = optionList2.get(i);
//                optionList1.add(s1+s);
//            }

/*
  这里 是 实现 随机选项的 方案 1： 使用的是random，随机性不高
 */
            int[] ints = randomSort(array,0);// 这个是随机函数，不是全排列函数
            for (int i=0; i< ints.length; i++){
                String s = sortMap.get(ints[i]);
                String s1 = optionList2.get(i);
                optionList1.add(s1+s);
                //optionList1.add(sortMap.get(ints[i]));
            }
            System.out.println(optionList1);
           for (int i =0; i < optionList1.size(); i++){

               String answer = optionLetter(optionList1.get(i));
               if (one.getCorrectText().equals(answer)){
                   String answerOption = optionList1.get(i).substring(0, 1);
                   questionDTO.setRightOption(answerOption);
               }
           }
//Collections.shuffle(list);//集合打乱顺序
//            questionDTO.setOption(optionList);
            questionDTO.setRandomOption(optionList1);

            list.add(questionDTO);
        }
        return list;
    }

    @Transactional
    @Override
    public DoQuestionInfoDTO judgeQuestionRight(int id, String studentNumber, String openid, String commitString, String examName, String subject) {
        Question question = questionDao.findOne(id);
        if (question == null){
            info = "您所查询的此题不存在，请核对后再查";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        String subjectName = questionDao.getSubjectName(id);
        UserQuestionRecord userQuestionRecord  = new UserQuestionRecord();

        String userAnswer = optionLetter(commitString);
        if (question.getCorrectText().equals(userAnswer)){
            userQuestionRecord.setDoRight(1);
        }else {
            userQuestionRecord.setDoRight(2);
        }
        userQuestionRecord.setUserAnswer(userAnswer);
        userQuestionRecord.setSubject(subjectName);
        userQuestionRecord.setStudentNumber(studentNumber);
        userQuestionRecord.setOpenid(openid);
        userQuestionRecord.setQuestionId(id);
        userQuestionRecord.setExamPaperId(question.getExamId());

        UserQuestionRecord save = userQuestionRecordDao.save(userQuestionRecord);

        DoQuestionInfoDTO dto = getDto(studentNumber, examName, subject);
//        ExamPaper examPaper = examPaperDao.findByExamNameAndSubjectAndValid(examName, subject, 1);
//        if (examPaper == null) {
//            info = "暂时没有此科目的此试卷";
//            log.error("【错误信息】: {}", info);
//            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
//        }
//        // 去除[] 和 空格，或者从插库时处理，直接就存1,2,3... ；而不是存成[1, 2, 3...]
//        String[] questionList = filterMiddleBrackets(examPaper.getQuestionList()).split(",");
//
//        List<Integer> idList = new ArrayList<>();
//        for (int i = 0; i < questionList.length; i++) {
//            int integer = Integer.parseInt(questionList[i]);
//            idList.add(integer);
//        }
//        // 这一份试卷的 题的数量
//        int questionCount = idList.size();
//        //  获取某学生->某科目 -> 某试卷的所有做题记录；
//        List<UserQuestionRecord> stulist = userQuestionRecordDao.getByStudentNumberAndSubjectAndExamPaperId(studentNumber, subject, question.getExamId());
//        int doRight = 0;
//        int doError = 0;
//        List<Integer> doRightList = new ArrayList<>(); // 做对的题号
//        List<Integer> doErrorList = new ArrayList<>(); // 做错的题号
//        List<Integer> notDoList = new ArrayList<>();
//        for (UserQuestionRecord questionRecord : stulist){
//            if (questionRecord.getDoRight() == 1){
//                if (!doRightList.contains(questionRecord.getQuestionId())){
//                    doRightList.add(questionRecord.getQuestionId());
//                    doRight++;
//                }
//
//            }else {
//                if (!doErrorList.contains(questionRecord.getQuestionId())){
//                    doErrorList.add(questionRecord.getQuestionId());
//                    doError++;
//                }
//
//            }
//        }
//        int notDo = questionCount - doRight - doError;
//        for (int i = 1; i <= questionCount; i++){
//            if (!doRightList.contains(i) && !doErrorList.contains(i)){
//                notDoList.add(i);
//            }
//        }
//        log.info("【总共做题数量：】{}", questionCount);
//        log.info("【作对题的数量：】{}", doRight);
//        log.info("【作错题的数量：】{}", doError);
//        log.info("【未做题的数量：】{}", notDo);
//
//        //List<DoQuestionInfoDTO> dtoList = new ArrayList<>();
//        DoQuestionInfoDTO dto = new DoQuestionInfoDTO();
//        dto.setQuestionCount(questionCount);
//        dto.setDoRight(doRight);
//        dto.setDoError(doError);
//        dto.setNotDo(notDo);
//        dto.setDoRightList(doRightList);
//        dto.setDoErrorList(doErrorList);
//        dto.setNotDoList(notDoList);

       // dtoList.add(dto);

        return dto;
    }

    @Override
    public UserCollect insertCollect(int id, String studentNumber, String openid, String classification, String commitString) {
        Question question = questionDao.findOne(id);
        if (question == null){
            info = "您所查询的此题不存在，请核对后再查";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        // 科目名称
        String subjectName = questionDao.getSubjectName(id);

        String userAnswer = optionLetter(commitString);

        UserCollect userCollect = new UserCollect();
        userCollect.setStudentNumber(studentNumber);
        userCollect.setOpenid(openid);
        userCollect.setSubject(subjectName);
        userCollect.setExamPaperId(question.getExamId());
        userCollect.setQuestionId(id);
        userCollect.setUserAnswer(userAnswer);
        userCollect.setClassification(classification);

        UserCollect save = userCollectDao.save(userCollect);
        return save;
    }

    @Override
    public DoQuestionInfoDTO getDoQuestionInfo(String studentNumber, String examName, String subject) {
        DoQuestionInfoDTO dto = getDto(studentNumber, examName, subject);
        return dto;
    }

    /**
     * 将六、八、接口公共的抽出来： 动态实时呈现用户做题详情 并记录用户所有的做题情况 接口中
     * 获取 做题情况抽出来，作为一个 公共的函数
     * @author aml
     * @date 2019/12/18 15:02
     */
    public DoQuestionInfoDTO getDto(String studentNumber,String examName, String subject) {
        ExamPaper examPaper = examPaperDao.getBy(examName, subject, 1);
        if (examPaper == null) {
            info = "暂时没有此科目的此试卷";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        // 去除[] 和 空格，或者从插库时处理，直接就存1,2,3... ；而不是存成[1, 2, 3...]
        String[] questionList = filterMiddleBrackets(examPaper.getQuestionList()).split(",");

        List<Integer> idList = new ArrayList<>();
        for (int i = 0; i < questionList.length; i++) {
            int integer = Integer.parseInt(questionList[i]);
            idList.add(integer);
        }
        // 这一份试卷的 题的数量
        int questionCount = idList.size();
        //  获取某学生->某科目 -> 某试卷的所有做题记录；
        List<UserQuestionRecord> stulist = userQuestionRecordDao.getByStudentNumberAndSubjectAndExamPaperId(studentNumber, subject, examPaper.getId());
        int doRight = 0;
        int doError = 0;
        List<Integer> doRightList = new ArrayList<>(); // 做对的题号
        List<Integer> doErrorList = new ArrayList<>(); // 做错的题号
        List<Integer> notDoList = new ArrayList<>();
        for (UserQuestionRecord questionRecord : stulist) {
            if (questionRecord.getDoRight() == 1) {
                if (!doRightList.contains(questionRecord.getQuestionId())) {
                    doRightList.add(questionRecord.getQuestionId());
                    doRight++;
                }

            } else {
                if (!doErrorList.contains(questionRecord.getQuestionId())) {
                    doErrorList.add(questionRecord.getQuestionId());
                    doError++;
                }

            }
        }
        int notDo = questionCount - doRight - doError;
        for (int i = 1; i <= questionCount; i++) {
            if (!doRightList.contains(i) && !doErrorList.contains(i)) {
                notDoList.add(i);
            }
        }
        log.info("【总共做题数量：】{}", questionCount);
        log.info("【作对题的数量：】{}", doRight);
        log.info("【作错题的数量：】{}", doError);
        log.info("【未做题的数量：】{}", notDo);

        //List<DoQuestionInfoDTO> dtoList = new ArrayList<>();
        DoQuestionInfoDTO dto = new DoQuestionInfoDTO();
        dto.setQuestionCount(questionCount);
        dto.setDoRight(doRight);
        dto.setDoError(doError);
        dto.setNotDo(notDo);
        dto.setDoRightList(doRightList);
        dto.setDoErrorList(doErrorList);
        dto.setNotDoList(notDoList);
        return dto;
    }
}
