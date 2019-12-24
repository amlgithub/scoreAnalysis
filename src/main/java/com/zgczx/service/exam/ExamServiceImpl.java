package com.zgczx.service.exam;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import com.zgczx.enums.ResultEnum;
import com.zgczx.exception.ScoreException;
import com.zgczx.repository.mysql1.exam.dao.*;
import com.zgczx.repository.mysql1.exam.dto.*;
import com.zgczx.repository.mysql1.exam.model.*;
import com.zgczx.repository.mysql3.unifiedlogin.dao.UserLoginDao;
import com.zgczx.repository.mysql3.unifiedlogin.model.UserLogin;
import com.zgczx.utils.StringToMapUtil;
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

    @Autowired
    private ExamContentDao examContentDao;

    @Autowired
    private UserPaperRecordDao userPaperRecordDao;

    @Autowired
    private UserLoginDao userLoginDao;

    private String info;

    @Override
    public String parseWord(MultipartFile file, HttpSession session, HttpServletRequest request) {
        String text = null;
        try {
            text = readWord(file);
// 1. 将试卷读进去
//            ExamContent examContent = new ExamContent();
//            examContent.setContent(text);
//            examContentDao.save(examContent);

            // 2. 将答案读进去
            ExamContent examContent = examContentDao.findOne(2);
            examContent.setAnswer(text);
            examContentDao.save(examContent);
            System.out.println("打印text： " + text);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    @Override
    public List<String> getAllChapter(String levelName, String subject) {
        List<String> name = chapterDao.findByLevelNameAndSubject(levelName, subject);
        if (name == null || name.size() == 0) {
            info = "暂时没有此年级的章目";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }

        return name;
    }

    @Override
    public List<String> getAllSection(String levelName, String chapter, String subject) {
        List<String> name = chapterDao.findByLevelNameAndChapterAndSubject(levelName, chapter, subject);
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
    public List<QuestionDTO> findExamQuestionInfo(String examName, String subject, String studentNumber, String openid) {
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
        for (Integer integer : idList) {
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

            log.info("【去除t,n等字符】： {}", questionOption);
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
            String str1 = questionOption.substring(i1 + 2, i2);//A选项
            String str2 = questionOption.substring(i2 + 2, i3);//B选项
            String str3 = questionOption.substring(i3 + 2, i4);//C选项
            String str4 = questionOption.substring(i4 + 2, questionOption.length());//D选项

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
            optionList2.add(questionOption.substring(i1, i1 + 2));
            optionList2.add(questionOption.substring(i2, i2 + 2));
            optionList2.add(questionOption.substring(i3, i3 + 2));
            optionList2.add(questionOption.substring(i4, i4 + 2));

            int[] array = new int[]{1, 2, 3, 4};

            boolean contains = questionOption.contains("E．");//判断选项中是否包含 D选项
            if (contains) {
                int i5 = questionOption.indexOf("E．");
                letterList.add(i5);
                String str5 = questionOption.substring(i5 + 2, questionOption.length());//E选项
                optionList.add(str5);
                sortMap.put(5, str5);
                optionList2.add(questionOption.substring(i5, i5 + 2));
                array = new int[]{1, 2, 3, 4, 5};
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
            int[] ints = randomSort(array, 0);// 这个是随机函数，不是全排列函数
            for (int i = 0; i < ints.length; i++) {
                String s = sortMap.get(ints[i]);
                String s1 = optionList2.get(i);
                optionList1.add(s1 + s);
                //optionList1.add(sortMap.get(ints[i]));
            }
//            System.out.println(optionList1);
            for (int i = 0; i < optionList1.size(); i++) {

                String answer = optionLetter(optionList1.get(i));
                if (one.getCorrectText().equals(answer)) {
                    String answerOption = optionList1.get(i).substring(0, 1);
                    questionDTO.setRightOption(answerOption);
                }
            }
//Collections.shuffle(list);//集合打乱顺序
//            questionDTO.setOption(optionList);
            questionDTO.setRandomOption(optionList1);

            questionDTO.setSourcePaperId(examPaper.getId());

            UserCollect userCollect = userCollectDao.getByStudentNumberAndSubjectAndExamPaperIdAndQuestionId(studentNumber, subject, examPaper.getId(), integer, 1);
            if (userCollect != null) {
                questionDTO.setCollect(1);
            } else {
                questionDTO.setCollect(2);
            }

            list.add(questionDTO);
        }
        return list;
    }

    @Transactional
    @Override
    public DoQuestionInfoDTO judgeQuestionRight(int id, String studentNumber, String openid, String commitString, String examName, String subject, int sourcePaperId) {
        Question question = questionDao.findOne(id);
        if (question == null) {
            info = "您所查询的此题不存在，请核对后再查";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        // 获取此试卷的所有信息
        ExamPaper paper = examPaperDao.findByExamNameAndSubjectAndValid(examName, subject, 1);
        String examSource = paper.getExamSource();// 获取试卷的类别，章节练习，模拟考试，历年真题等
        String paperExamName = paper.getExamName();
        String subjectName = questionDao.getSubjectName(id);

        List<UserQuestionRecord> repatQuestion = userQuestionRecordDao.getByStudentNumberAndExamPaperIdAndQuestionId(studentNumber, sourcePaperId, id);
        if (repatQuestion == null || repatQuestion.size() == 0) {

            UserQuestionRecord userQuestionRecord = new UserQuestionRecord();

            String userAnswer = optionLetter(commitString);
            if (question.getCorrectText().equals(userAnswer)) {
                userQuestionRecord.setDoRight(1);
            } else {
                userQuestionRecord.setDoRight(2);
            }
            userQuestionRecord.setUserAnswer(userAnswer);
            userQuestionRecord.setSubject(subjectName);
            userQuestionRecord.setStudentNumber(studentNumber);
            userQuestionRecord.setOpenid(openid);
            userQuestionRecord.setQuestionId(id);
            userQuestionRecord.setExamPaperId(sourcePaperId);// 试卷id：（不是这道题是从哪个试卷中录入进去的）保存这道题被组卷在哪套试题中
            userQuestionRecord.setTimes(1);
            userQuestionRecord.setExamPaperName(paperExamName);
            userQuestionRecord.setExamCategory(examSource);
            UserQuestionRecord save = userQuestionRecordDao.save(userQuestionRecord);
        } else {
            int times = repatQuestion.get(0).getTimes();
            int repatTime = times + 1;
            UserQuestionRecord userQuestionRecord = new UserQuestionRecord();

            String userAnswer = optionLetter(commitString);
            if (question.getCorrectText().equals(userAnswer)) {
                userQuestionRecord.setDoRight(1);
            } else {
                userQuestionRecord.setDoRight(2);
            }
            userQuestionRecord.setUserAnswer(userAnswer);
            userQuestionRecord.setSubject(subjectName);
            userQuestionRecord.setStudentNumber(studentNumber);
            userQuestionRecord.setOpenid(openid);
            userQuestionRecord.setQuestionId(id);
            userQuestionRecord.setExamPaperId(sourcePaperId);// 试卷id：（不是这道题是从哪个试卷中录入进去的）保存这道题被组卷在哪套试题中
            userQuestionRecord.setTimes(repatTime);
            userQuestionRecord.setExamPaperName(paperExamName);
            userQuestionRecord.setExamCategory(examSource);

            UserQuestionRecord save = userQuestionRecordDao.save(userQuestionRecord);
        }

//        DoQuestionInfoDTO dto = getDto(studentNumber, examName, subject,sourcePaperId);
        ExamPaper examPaper = examPaperDao.findOne(sourcePaperId);
        //ExamPaper examPaper = examPaperDao.getBy(examName, subject, 1);
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
        // List<UserQuestionRecord> repatQuestion = userQuestionRecordDao.getByStudentNumberAndExamPaperIdAndQuestionId(studentNumber, sourcePaperId, idList.get(0));
        int times = 1;
        if (repatQuestion.size() > 0) {
            times = repatQuestion.get(0).getTimes();
        }
        // 这一份试卷的 题的数量
        int questionCount = idList.size();
        //  获取某学生->某科目 -> 某试卷 --》 “某次” (最近一次)-->的所有做题记录；
        List<UserQuestionRecord> stulist = userQuestionRecordDao.getByStudentNumberAndSubjectAndExamPaperIdAndTimes(studentNumber, subject, examPaper.getId(), times);
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

    @Override
    public UserCollect insertCollect(int id, String studentNumber, String openid, String classification) {
        Question question = questionDao.findOne(id);
        if (question == null) {
            info = "您所查询的此题不存在，请核对后再查";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        // 科目名称
        String subjectName = questionDao.getSubjectName(id);

//        String userAnswer = optionLetter(commitString);
        UserCollect userCollect = new UserCollect();
        userCollect.setStudentNumber(studentNumber);
        userCollect.setOpenid(openid);
        userCollect.setSubject(subjectName);
        userCollect.setExamPaperId(question.getExamId());// 收藏时，是针对题，如果这道题已经收藏过了，就不允许再次收藏
        userCollect.setQuestionId(id);
        userCollect.setValid(1);
//        userCollect.setUserAnswer(userAnswer);
        userCollect.setClassification(classification);

        UserCollect save = userCollectDao.save(userCollect);
        return save;
    }

    @Override
    public DoQuestionInfoDTO getDoQuestionInfo(String studentNumber, String examName, String subject, int sourcePaperId) {
        DoQuestionInfoDTO dto = getDto(studentNumber, examName, subject, sourcePaperId);
        return dto;
    }


    @Override
    public List<EchoDoQuestionDTO> echoDoQuestionInfo(String studentNumber, String examName, String subject) {
        ExamPaper examPaper = examPaperDao.getBy(examName, subject, 1);
        if (examPaper == null) {
            info = "暂时没有此科目的此试卷";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        List<UserQuestionRecord> echolist = userQuestionRecordDao.getByStudentNumberAndSubjectAndExamPaperId(studentNumber, subject, examPaper.getId());
        if (echolist == null || echolist.size() == 0) {
            info = "您还未做过此试卷，暂无记录";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        // 调用公共函数 2，获取questionList
        List<Integer> questionList = questionList(examPaper.getQuestionList());

        List<EchoDoQuestionDTO> echoDoQuestionDTOList = new ArrayList<>();

        int times = echolist.get(0).getTimes();
        for (UserQuestionRecord questionRecord : echolist) {
            if (questionRecord.getTimes() == times) {
                EchoDoQuestionDTO echoDoQuestionDTO = new EchoDoQuestionDTO();
                // 题号
                int questionNo = questionList.indexOf(questionRecord.getQuestionId()) + 1;
                echoDoQuestionDTO.setQuestionNo(questionNo);
                // 题填写的文本
                echoDoQuestionDTO.setQuestionNoText(questionRecord.getUserAnswer());
                echoDoQuestionDTOList.add(echoDoQuestionDTO);
            }

        }
        return echoDoQuestionDTOList;
    }


    @Override
    public UserCollect cancelCollect(int id, String studentNumber, String openid, String examName, String subject, int cancel) {
        ExamPaper examPaper = examPaperDao.getBy(examName, subject, 1);
        if (examPaper == null) {
            info = "暂时没有此科目的此试卷";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        UserCollect userCollect = userCollectDao.getByStudentNumberAndSubjectAndExamPaperIdAndQuestionId(studentNumber, subject, examPaper.getId(), id, 1);
        if (userCollect == null) {
            info = "您此题还未收藏过，暂无法取消收藏";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        userCollect.setValid(cancel);
        UserCollect save = userCollectDao.save(userCollect);
        return save;
    }

    @Override
    public UserPaperRecord fullPaperRecord(String studentNumber, String openid, String examName, String subject, String examPaperContent, String examPaperAnwer) {
        ExamPaper examPaper = examPaperDao.getBy(examName, subject, 1);
        if (examPaper == null) {
            info = "暂时没有此科目的此试卷";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        List<UserPaperRecord> paperRecordlist = userPaperRecordDao.getByStudentNumberAndSubjectAndExamPaperId(studentNumber, subject, examPaper.getId());
        int times = 1;
        if (paperRecordlist.size() == 0) {
            UserPaperRecord userPaperRecord = new UserPaperRecord();
            userPaperRecord.setStudentNumber(studentNumber);
            userPaperRecord.setOpenid(openid);
            userPaperRecord.setExamPaperId(examPaper.getId());
            userPaperRecord.setSubject(subject);
            userPaperRecord.setExamPaperContent(examPaperContent);
            userPaperRecord.setExamPaperAnwer(examPaperAnwer);
            userPaperRecord.setTimes(times);

            UserPaperRecord save = userPaperRecordDao.save(userPaperRecord);
            return save;
        } else {
            int times1 = paperRecordlist.get(0).getTimes() + 1;
            UserPaperRecord userPaperRecord = new UserPaperRecord();
            userPaperRecord.setStudentNumber(studentNumber);
            userPaperRecord.setOpenid(openid);
            userPaperRecord.setExamPaperId(examPaper.getId());
            userPaperRecord.setSubject(subject);
            userPaperRecord.setExamPaperContent(examPaperContent);
            userPaperRecord.setExamPaperAnwer(examPaperAnwer);
            userPaperRecord.setTimes(times1);

            UserPaperRecord save = userPaperRecordDao.save(userPaperRecord);
            return save;
        }

    }

    @Override
    public ChapterErrNumberDTO getChapterErrNumber(String stuNumber, String openid, String subject) {
        UserLogin userInfo = userLoginDao.findByDiyid(stuNumber);// 获取此用户的所有基本信息
        if (userInfo == null) {
            info = "暂时没有学号所对应的信息，请认真核对您的学号";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        String gradeLevel = userInfo.getGradeLevel();//此用户的年级水平，例如高1
        //1. 先获取 所有节的名称
        List<String> paperName = userQuestionRecordDao.getAllExamPaperName(stuNumber, subject, "章节练习");
        //2. 根据所有 节的名称 获取所有章的名称
        List<String> chapterNameList = chapterDao.findBySectionIn(paperName);
        //3. 获取所有 错题信息
        List<UserQuestionRecord> errInList = userQuestionRecordDao.getByStudentNumberAndSubjectAndDoRightAndExamCategory(stuNumber, subject, 2, "章节练习");
        // 4. 筛选属于

//            for (int i =0; i < chapterNameList.size(); i++){
//                if (paperName.getExamPaperName().equals(chapterNameList.get(i))){
//
//
//
//                }
//
//        }


        List<String> errInfo = userQuestionRecordDao.getAllErrInfo(stuNumber, subject, 2, "章节练习");
        if (errInfo.size() == 0) {
            info = "您所做的章节练习中还没错题";
            ChapterErrNumberDTO chapterErrNumberDTO = new ChapterErrNumberDTO();
            chapterErrNumberDTO.setGradeLevel(gradeLevel);
            chapterErrNumberDTO.setChapterNumber(null);
            return chapterErrNumberDTO;
        } else {
            //


        }

        return null;



       /* //获取 此用户有错题的 所有试卷id
        List<Integer> examPaperIdList = userQuestionRecordDao.getAllExamPaperId(stuNumber, subject);
        if (examPaperIdList.size() == 0){
            info = "您所做的练习中还没错题";
            ChapterErrNumberDTO chapterErrNumberDTO = new ChapterErrNumberDTO();
            chapterErrNumberDTO.setGradeLevel(gradeLevel);
            chapterErrNumberDTO.setChapterNumber(null);
            return chapterErrNumberDTO;
        }else {
            // 获取试卷的 exam_source试卷来源名称： 例如：模拟考试；历年真题；章节练习；专项练习等
            List<ExamPaper> examPaperList = examPaperDao.findByIdIn(examPaperIdList);
            for (ExamPaper examPaper : examPaperList){
                if (examPaper.getExamSource().equals("章节练习")){

                }
                int id = examPaper.getId();
                String examName = examPaper.getExamName(); //

            }

        }*/


    }

    @Override
    public List<EchoPaperCompleteDTO> echoPaperInfo(String stuNumber, String openid, String subject, String examName) {
        ExamPaper examPaper = examPaperDao.getBy(examName, subject, 1);
        if (examPaper == null) {
            info = "暂时没有此科目的此试卷";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        List<UserPaperRecord> paperRecordList = userPaperRecordDao.getByStudentNumberAndSubjectAndExamPaperId(stuNumber, subject, examPaper.getId());
        if (paperRecordList.size() == 0) {
            info = "您还没做过此时卷，因此暂无保存进度";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.NEVER_DID_THIS_PAPER, info);

        } else {
            String examPaperContent1 = paperRecordList.get(0).getExamPaperContent();

            String examPaperContent = examPaperContent1.replaceAll("\\{\"question\":", "");
            String s1 = filter1(examPaperContent);
            String s2 = filter2(s1);
            String b = s2.replaceAll("(\",\"B)", "B");
            String c = b.replaceAll("(\",\"C)", "C");
            String d = c.replaceAll("(\",\"D)", "D");
//            Map<String, Object> map = new HashMap<String, Object>();
//            map = gson.fromJson(text, map.getClass());
            List<EchoPaperDTO> echoPaperDTOList = JSON.parseObject(d, new TypeReference<List<EchoPaperDTO>>() {
            });

            //处理 用户的所有选项
            String paperAnwer = paperRecordList.get(0).getExamPaperAnwer();
            StringToMapUtil stringToMapUtil = new StringToMapUtil();
            Map<String, String> map = stringToMapUtil.stringToMap(paperAnwer);
            log.info("【map: 】{}", map);
            EchoPaperCompleteDTO completeDTO = new EchoPaperCompleteDTO();
            for (String value : map.values()){
                if (value.equals("")){
                    completeDTO.setEffective(2);//此时卷没做完
                    break;
                }
                completeDTO.setEffective(1);//此试卷已经做完
            }

            int complete = 1;// 默认此题为 已经做完此时卷
            List<EchoPaperCompleteDTO> completeDTOList = new ArrayList<>();
            List<EchoPaperTotalDTO> list = new ArrayList<>();

            for (int i = 0; i < echoPaperDTOList.size(); i++) {
                EchoPaperTotalDTO paperTotalDTO = new EchoPaperTotalDTO();
                String o = String.valueOf(map.get(String.valueOf(i)));
                if (o.equals("")) {
                    complete = 2;
                    paperTotalDTO.setUserOption("");
                }else {
                    paperTotalDTO.setUserOption(String.valueOf(o));
                }
                paperTotalDTO.setEchoPaperDTO(echoPaperDTOList.get(i));
                paperTotalDTO.setComplete(complete);
                list.add(paperTotalDTO);
                completeDTO.setList(list);
                completeDTOList.add(completeDTO);
            }
           // log.info("【list: 】{}", list);
            return completeDTOList;
        }

    }

    /**
     * 公共函数 1.
     * 将六、八、接口公共的抽出来： 动态实时呈现用户做题详情 并记录用户所有的做题情况 接口中
     * 获取 做题情况抽出来，作为一个 公共的函数
     *
     * @author aml
     * @date 2019/12/18 15:02
     */
    public DoQuestionInfoDTO getDto(String studentNumber, String examName, String subject, int sourcePaperId) {
        ExamPaper examPaper = examPaperDao.findOne(sourcePaperId);
        //ExamPaper examPaper = examPaperDao.getBy(examName, subject, 1);
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
        List<UserQuestionRecord> repatQuestion = userQuestionRecordDao.getByStudentNumberAndExamPaperIdAndQuestionId(studentNumber, sourcePaperId, idList.get(0));
        int times = 1;
        if (repatQuestion != null || repatQuestion.size() > 0) {
            times = repatQuestion.get(0).getTimes();
        }
        // 这一份试卷的 题的数量
        int questionCount = idList.size();
        //  获取某学生->某科目 -> 某试卷 --》 “某次” (最近一次)-->的所有做题记录；
        List<UserQuestionRecord> stulist = userQuestionRecordDao.getByStudentNumberAndSubjectAndExamPaperIdAndTimes(studentNumber, subject, examPaper.getId(), times);
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

    /**
     * 公共函数 2
     * 将一份试卷中的 question_list 转换为 list 数组: 用来获取此试卷的题号
     * 这个 抽取为公共函数
     */
    public static List<Integer> questionList(String questionListString) {
        String[] questionList = filterMiddleBrackets(questionListString).split(",");

        List<Integer> idList = new ArrayList<>();
        for (int i = 0; i < questionList.length; i++) {
            int integer = Integer.parseInt(questionList[i]);
            idList.add(integer);
            //System.out.println(idList);
        }
        return idList;
    }


}

