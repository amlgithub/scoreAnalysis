package com.zgczx.service.exam;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import java.sql.Timestamp;
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

    @Autowired
    private UserWrongQustionDao userWrongQustionDao;

    private String info;

    @Override
    public String parseWord(MultipartFile file, HttpSession session, HttpServletRequest request) {
        String text = null;
        try {
            JSONObject docJson = readWord(file);
            text = String.valueOf(docJson.get("doctext"));
            String title = filterChineseAndMath(String.valueOf(docJson.get("title")));
            String imgList = String.valueOf(docJson.get("imgList"));


// 1. 将试卷读进去
            ExamContent examContent = new ExamContent();
            examContent.setContent(text);
            examContent.setExamName(title);
            examContent.setSubject("生物");
            ExamContent save = examContentDao.save(examContent);
            String[] split = save.getContent().split("#");//
            for (String string : split) {

            }
//            // 2. 将答案读进去
//            ExamContent examContent = examContentDao.findOne(2);
//            examContent.setAnswer(text);
//            examContentDao.save(examContent);
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
        ExamContent content = examContentDao.findByExamNameAndSubject(examName, subject);
        String[] split = content.getContent().split("#");
        log.info("[split: ]{}", split);


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
    public List<QuestionDTO> findExamQuestionInfo(String examName, String subject, String studentNumber, String openid,String gradeLevel) {
        ExamPaper examPaper = examPaperDao.findByExamNameAndSubjectAndValidAndGradeLevel(examName, subject, 1,gradeLevel);
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
            if (one == null){
                continue;
            }
            questionDTO.setQuestion(one);
            //2.4  修改 图片为list
            List<String> imgList = new LinkedList<>();//2.4 新修改
            String questionImgs = one.getQuestionImgs();
            if (questionImgs == null){
//            imgList.add();
//                jsonObject.put("imgList",imgList);
                questionDTO.setImgList(imgList);
            }
            else if (questionImgs.contains(",")){
                String[] split = questionImgs.split(",");
                for (int i=0; i<split.length;i++){
                    imgList.add(split[i]);
                }
//                jsonObject.put("imgList",imgList);
                questionDTO.setImgList(imgList);
            }else {
                if (!questionImgs.equals("")){
                    imgList.add(questionImgs);
                }
//                jsonObject.put("imgList",imgList);
                questionDTO.setImgList(imgList);
            }

            List<String> optionList = new LinkedList<>();
            List<String> optionList1 = new LinkedList<>();
            List<String> optionList2 = new LinkedList<>();

            List<Map<String, String>> optionMapList = new LinkedList<>();
            Map<String, String> map = new HashMap<>();

            String oneQuestionOption = one.getQuestionOption();//获取所有选项的文本
            String questionOption = filterspecial(oneQuestionOption);//过滤下\t,\n等字符

            log.info("【去除t,n等字符】： {}", questionOption);
            int i1 = -1;
            if (questionOption.indexOf("A．") != -1) {
                i1 = questionOption.indexOf("A．");
            } else {
                i1 = questionOption.indexOf("A.");
            }
            int i2 = -1;
            if (questionOption.indexOf("B．") != -1) {
                i2 = questionOption.indexOf("B．");
            } else {
                i2 = questionOption.indexOf("B.");
            }
            int i3 = -1;
            if (questionOption.indexOf("C．") != -1) {
                i3 = questionOption.indexOf("C．");
            } else {
                i3 = questionOption.indexOf("C.");
            }
            int i4 = -1;
            if (questionOption.indexOf("D．") != -1) {
                i4 = questionOption.indexOf("D．");
            } else {
                i4 = questionOption.indexOf("D.");
            }
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
            //这个是截取原选项中的ABCD.;可能有的是中文点
            /*optionList2.add(questionOption.substring(i1, i1 + 2));
            optionList2.add(questionOption.substring(i2, i2 + 2));
            optionList2.add(questionOption.substring(i3, i3 + 2));
            optionList2.add(questionOption.substring(i4, i4 + 2));*/
            //全部定死为 英文点
            optionList2.add("A.");
            optionList2.add("B.");
            optionList2.add("C.");
            optionList2.add("D.");
            int[] array = new int[]{1, 2, 3, 4};

            boolean contains = questionOption.contains("E．");//判断选项中是否包含 D选项
            if (contains) {
                int i5 = questionOption.indexOf("E．");
                letterList.add(i5);
                String str5 = questionOption.substring(i5 + 2, questionOption.length());//E选项
                optionList.add(str5);
                sortMap.put(5, str5);
//                optionList2.add(questionOption.substring(i5, i5 + 2));
                optionList2.add("E.");
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
    public DoQuestionInfoDTO judgeQuestionRight(int id, String studentNumber, String openid, String commitString, String examName, String subject, int sourcePaperId,String gradeLevel,String doTime) {
        Question question = questionDao.getByIdAndValid(id,1);
        if (question == null) {
            info = "您所查询的此题不存在，请核对后再查";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        // 获取此试卷的所有信息
        ExamPaper paper = examPaperDao.findByExamNameAndSubjectAndValidAndGradeLevel(examName, subject, 1,gradeLevel);
        String examSource = paper.getExamSource();// 获取试卷的类别，章节练习，模拟考试，历年真题等
        String paperExamName = paper.getExamName();
        String subjectName = questionDao.getSubjectName(id);
        String userAnswer = optionLetter(commitString);//用户的答案
        List<UserQuestionRecord> repatQuestion = userQuestionRecordDao.getByStudentNumberAndExamPaperIdAndQuestionId(studentNumber, sourcePaperId, id);
        if (repatQuestion == null || repatQuestion.size() == 0) {
            UserQuestionRecord userQuestionRecord = new UserQuestionRecord();

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
            userQuestionRecord.setDoTime(doTime);//2.2 新增做题时间
            UserQuestionRecord save = userQuestionRecordDao.save(userQuestionRecord);
        } else {
            int times = repatQuestion.get(0).getTimes();
            int repatTime = times + 1;
            UserQuestionRecord userQuestionRecord = new UserQuestionRecord();

//            String userAnswer = optionLetter(commitString);
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
            userQuestionRecord.setDoTime(doTime);//2.2 新增做题时间
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
            times = repatQuestion.get(0).getTimes() + 1;
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

        // 新增往错题表中插数据
        List<UserQuestionRecord> repatQuestion2 = userQuestionRecordDao.getByStudentNumberAndExamPaperIdAndQuestionId(studentNumber, sourcePaperId, id);
        UserQuestionRecord questionRecord = repatQuestion2.get(0);// 获取刚插入的此题所有数据
        if (questionRecord.getDoRight() == 2) {
            // 此题错误，判断此题的 相同来源是否 插入过库中
            UserWrongQustion userWrong = userWrongQustionDao.getByStudentNumberAndExamCategoryAndQuestionId(studentNumber, questionRecord.getExamCategory(), id, subject);
            if (userWrong == null) {
                //如果不存在，则插入
                UserWrongQustion wrongQustion = new UserWrongQustion();
                wrongQustion.setStudentNumber(studentNumber);
                wrongQustion.setOpenid(openid);
                wrongQustion.setSubject(subject);
                wrongQustion.setDoRight(2);
                wrongQustion.setQuestionId(id);
                wrongQustion.setUserAnswer(userAnswer);
                wrongQustion.setExamPaperId(sourcePaperId);// 试卷id：（不是这道题是从哪个试卷中录入进去的）保存这道题被组卷在哪套试题中
                wrongQustion.setExamPaperName(paperExamName);
                wrongQustion.setExamCategory(examSource);
                wrongQustion.setDoTime(doTime);//2.2 新增做题时间
                userWrongQustionDao.save(wrongQustion);
            }


        }

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
        UserCollect collect = userCollectDao.findByStudentNumberAndQuestionId(studentNumber, id);
        if (collect != null && collect.getValid() == 2) {
            collect.setValid(1);// 将此题重新设置为收藏状态
            Timestamp date = new Timestamp(System.currentTimeMillis());
            collect.setUpdatetime(date);
            UserCollect save = userCollectDao.save(collect);
            return save;
        }
        if (collect == null) {
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
        }else {
            return collect;
        }
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
        if (examName.equals("")){
            //错题本中的情况，无法传此题的试卷名称，可能此题是多个分类的情况下
            UserCollect userCollect = userCollectDao.getByStudentNumberAndSubjectAndQuestionId(studentNumber, subject, id);
            if (userCollect == null) {
                info = "您此题还未收藏过，暂无法取消收藏";
                log.error("【错误信息】: {}", info);
                throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
            }
            userCollect.setValid(cancel);
            UserCollect save = userCollectDao.save(userCollect);
            return save;
        }
        ExamPaper examPaper = examPaperDao.getBy(examName, subject, 1);
        if (examPaper == null) {
            info = "暂时没有此科目的此试卷";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        // 模拟考试和历年真题 其他模块下已经存入此题了，因此不需要传入 试卷名称
        if (examPaper.getExamSource().equals("模拟考试") || examPaper.getExamSource().equals("历年真题")){
            UserCollect userCollect = userCollectDao.getByStudentNumberAndSubjectAndQuestionId(studentNumber, subject, id);
            if (userCollect == null) {
                info = "您此题还未收藏过，暂无法取消收藏";
                log.error("【错误信息】: {}", info);
                throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
            }
            userCollect.setValid(cancel);
            UserCollect save = userCollectDao.save(userCollect);
            return save;
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
    public JSONObject getChapterErrNumber(String stuNumber, String openid, String subject, String examCategory) {


        JSONObject json = new JSONObject();

        UserLogin userInfo = userLoginDao.findByDiyid(stuNumber);// 获取此用户的所有基本信息
        if (userInfo == null) {
            info = "暂时没有学号所对应的信息，请认真核对您的学号";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
//        String gradeLevel = userInfo.getGradeLevel();//此用户的年级水平，例如高1

        Map<String, Integer> getNotMasteredErrorNum = new LinkedHashMap<>();
        Map<String, Integer> getMasteredErrorNum = new LinkedHashMap<>();
        if (examCategory.equals("1")) {

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
                    // 章练习未掌握错题数
                    int getNotMasteredErrorNumByChapter = userWrongQustionDao.getErrorNumByChapter(stuNumber, subject, chapter, 2);
                    if (getNotMasteredErrorNumByChapter != 0) {
                        getNotMasteredErrorNum.put(chapter, getNotMasteredErrorNumByChapter);
                    }
                    // 章练习已掌握错题数
                    int getMasteredErrorNumByChapter = userWrongQustionDao.getErrorNumByChapter(stuNumber, subject, chapter, 1);
                    if (getMasteredErrorNumByChapter != 0) {
                        getMasteredErrorNum.put(chapter, getMasteredErrorNumByChapter);
                    }
                }
            }
        } else if (examCategory.equals("2")) {
            // 得到该学科所有的章节
            List<String> getExamName = examPaperDao.getExamName();
            if (getExamName == null || getExamName.size() == 0) {
                info = "暂时没有考试题";
                log.error("【错误信息】: {}", info);
                throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
            } else {
                for (int i = 0; i < getExamName.size(); i++) {
                    String examName = getExamName.get(i);
                    // 考试未掌握错题数
                    int getNotMasteredErrorNumByExam = userWrongQustionDao.getErrorNumByExam(stuNumber, subject, examName, 2);
                    if (getNotMasteredErrorNumByExam != 0) {
                        getNotMasteredErrorNum.put(examName, getNotMasteredErrorNumByExam);
                    }

                    // 考试已掌握错题数
                    int getMasteredErrorNumByExam = userWrongQustionDao.getErrorNumByExam(stuNumber, subject, examName, 1);
                    if (getMasteredErrorNumByExam != 0) {
                        getMasteredErrorNum.put(examName, getMasteredErrorNumByExam);
                    }
                }
            }
        }
        json.put("notMastered", getNotMasteredErrorNum);
        json.put("mastered", getMasteredErrorNum);
        return json;

//        Map<String, Integer> chapterErrNumMap = new HashMap<>();
//        List<String> chapterSectionList = new ArrayList<>();//章-节 list
//        Map<String, String> chapterSectionMap = new HashMap<>();//章-节 map
//        Map<String, Integer> sectionErrNumMap = new HashMap<>();// 节-错题数量 map
//        String examCategory1 = null;
//        String examCategory2 = null;
//        if (examCategory.equals("1")){
//            examCategory1 = "章节练习";
//            examCategory2 = "专项练习";
//        }else {
//            examCategory1 = "模拟考试";
//            examCategory2 = "历年真题";
//        }
//
//        //1. 先 获取此用户-》此科目-》章节练习中 所有错题  试卷名称（每节的名称）
////        List<String> paperName = userQuestionRecordDao.getAllErrInfo(stuNumber, subject, 2, "章节练习");
//        List<String> paperName = userWrongQustionDao.getAllErrInfo(stuNumber, subject, 2, examCategory1,examCategory2);
//        if (paperName.size() == 0) {
//            info = "您所做的章节练习中还没错题";
//            ChapterErrNumberDTO chapterErrNumberDTO = new ChapterErrNumberDTO();
//            chapterErrNumberDTO.setGradeLevel(gradeLevel);
//            chapterErrNumberDTO.setChapterNumber(null);
//            return chapterErrNumberDTO;
//        } else {
//            //2. 根据所有 节的名称 获取所有章的名称
//            List<String> chapterNameList = chapterDao.findBySectionIn(paperName);
//            for (String chapterName : chapterNameList){
//                //3. 获取所有节的名称，根据章名称和科目
//                List<String> sectionList = chapterDao.findByChapterAndSubject(chapterName, subject);
//                for (String section: sectionList){
//                    System.out.println(section);
//                    //4. 获取此节的 错题数量
//                    int errNumber = userWrongQustionDao.getByErrNumber(stuNumber, subject, section);
//                    chapterSectionList.add(chapterName+","+section);
//                    chapterSectionMap.put(chapterName,section);
//                    sectionErrNumMap.put(section,errNumber);
//                }
//            }
//
//            log.info("【chapterSectionMap】{}",chapterSectionMap);
//            log.info("【sectionErrNumMap】{}",sectionErrNumMap);
//            log.info("【chapterSectionList】{}",chapterSectionList);
//        }
//        ChapterErrNumberDTO chapterErrNumberDTO = new ChapterErrNumberDTO();
//        for (String string : chapterSectionList){
//            int i = string.indexOf(",");
//            String chapterName = string.substring(0, i);
//            String sectionName = string.substring(i + 1, string.length());
//            Integer integer = sectionErrNumMap.get(sectionName);
//            if (chapterErrNumMap.containsKey(chapterName)){
//                Integer integer1 = chapterErrNumMap.get(chapterName);
//                integer += integer1;
//                chapterErrNumMap.put(chapterName,integer);
//            }
//            chapterErrNumMap.put(chapterName,integer);
//        }
//        chapterErrNumberDTO.setGradeLevel(gradeLevel);
//        chapterErrNumberDTO.setChapterNumber(chapterErrNumMap);
//        log.info("【chapterErrNumMap】{}",chapterErrNumMap);
//
//        return chapterErrNumberDTO;



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
//            for (String value : map.values()){
//                if (value.equals("")){
//                    completeDTO.setEffective(2);//此时卷没做完
//                    break;
//                }
//                completeDTO.setEffective(1);//此试卷已经做完
//            }

            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (entry.getValue().equals("")) {
                    completeDTO.setEffective(2);//此时卷没做完
                    completeDTO.setFirstNoDoneNum(entry.getKey());
                    break;
                }
                completeDTO.setFirstNoDoneNum(String.valueOf(map.entrySet().size()));
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
                    paperTotalDTO.setComplete(2);
                    paperTotalDTO.setUserOption("");
                } else {
                    paperTotalDTO.setUserOption(String.valueOf(o));
                    paperTotalDTO.setComplete(1);
                }
                EchoPaperDTO paperDTO = echoPaperDTOList.get(i);
                paperTotalDTO.setQuestion(paperDTO);
                //将 选项的字符文本，封装为linkedlist按顺序添加
                //List<String> list1 = stringTurnList(paperDTO.getRandomOption());
                List<String> list1 = stringTurnList2(paperDTO.getRandomOption());
                paperTotalDTO.setRandomOption(list1);

//                paperTotalDTO.setComplete(complete);
                // 此题是否已经收藏过,如果userCollect存在，则此题收藏了
                UserCollect userCollect = userCollectDao.getByStudentNumberAndSubjectAndExamPaperIdAndQuestionId(stuNumber, subject, examPaper.getId(), paperDTO.getId(), 1);
                if (userCollect == null) {
                    paperTotalDTO.setCollect(2);
                } else {
                    paperTotalDTO.setCollect(1);
                }

                // 设置 rightOption
                paperTotalDTO.setRightOption(paperDTO.getRightOption());
                //设置 sourcePaperId
                paperTotalDTO.setSourcePaperId(paperDTO.getSourcePaperId());
                list.add(paperTotalDTO);
                completeDTO.setList(list);

                //2.4  修改 图片为list
                List<String> imgList = new LinkedList<>();//2.4 新修改
                String questionImgs = paperDTO.getQuestionImgs();
                if (questionImgs == null){
                    paperTotalDTO.setImgList(imgList);
                }
                else if (questionImgs.contains(",")){
                    String[] split = questionImgs.split(",");
                    for (int j=0; j<split.length;j++){
                        imgList.add(split[j]);
                    }

                    paperTotalDTO.setImgList(imgList);
                }else {
                    if (!questionImgs.equals("")){
                        imgList.add(questionImgs);
                    }
                    paperTotalDTO.setImgList(imgList);
                }

            }
            completeDTOList.add(completeDTO);
            // log.info("【list: 】{}", list);
            return completeDTOList;
        }
    }

    @Override
    public FindCollectDTO findCollectInfo(String stuNumber, String subject, int questionId) {
        // int valid =1;//收藏的标志
        int ifCollectByUserAndQuestionId = userCollectDao.getIfCollectByStuNumAndQuestionId(stuNumber, subject, questionId);

        FindCollectDTO findCollectDTO = new FindCollectDTO();
        if (ifCollectByUserAndQuestionId == 1) {
            findCollectDTO.setCollect(1);
            return findCollectDTO;
        } else {
            findCollectDTO.setCollect(2);
            return findCollectDTO;
        }
//        FindCollectDTO findCollectDTO = new FindCollectDTO();
//        if (userCollect == null){
//            findCollectDTO.setCollect(2);
//            return findCollectDTO;
//        }else {
//            findCollectDTO.setCollect(1);
//            return findCollectDTO;
//        }

    }

    @Override
    public SectionErrNumberDTO getSectionErrNumber(String stuNumber, String openid, String subject, String chapterName, String ifMastered) {
        //1. 根据章的名称获取所有节的名称
        List<String> sectionList = chapterDao.findByChapterAndSubject(chapterName, subject);
        if (sectionList.size() == 0) {
            info = "暂无您要查询的小节名称";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        Map<String, Integer> sectionErrNumMap = new LinkedHashMap<>();
        SectionErrNumberDTO sectionErrNumberDTO = new SectionErrNumberDTO();
        for (String section : sectionList) {
            System.out.println(section);
            if (ifMastered.equals("未掌握")) {
                //4. 获取此节的 错题数量(未掌握)
                int errNumber = userWrongQustionDao.getByErrNumber(stuNumber, subject, section, 2);
                if (errNumber == 0) {
                    continue;
                }
                sectionErrNumMap.put(section, errNumber);
            } else if (ifMastered.equals("已掌握")) {
                //4. 获取此节的 错题数量(已掌握)
                int errNumber = userWrongQustionDao.getByErrNumber(stuNumber, subject, section, 1);
                if (errNumber == 0) {
                    continue;
                }
                sectionErrNumMap.put(section, errNumber);
            }
        }
        sectionErrNumberDTO.setSectionNumber(sectionErrNumMap);
        return sectionErrNumberDTO;
    }

    @Override
    public JSONObject getNotMasteredInfo(String studentNumber, String openid, String subject, String examCategory, String gradeLevel, int master) {
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();// 提的所有详情
        if (!examCategory.equals("全部")) {
            List<UserWrongQustion> wrongQustions = null;
            if (examCategory.equals("专项练习")){
                 wrongQustions = userWrongQustionDao.getAllInfo2(studentNumber, gradeLevel, subject, master, examCategory);//去重qustion_id
            }else {
                 wrongQustions = userWrongQustionDao.getAllInfo(studentNumber, gradeLevel, subject, master, examCategory);//去重qustion_id
            }
            if (wrongQustions.size() == 0) {
                info = "暂无您要查询的未掌握题情况";
                log.error("【错误信息】: {}", info);
                throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
            } else {
                for (UserWrongQustion wrongQustion : wrongQustions) {
                    JSONObject jsonObject1 = new JSONObject();// 分装Json
                    int questionId = wrongQustion.getQuestionId();// question表的id
                    //此题的分类标签，可能有多个
                    List<String> questionSource = userWrongQustionDao.getallQuestionSource(studentNumber, gradeLevel, subject, master, questionId);
                    Question question = questionDao.getByIdAndValid(questionId, 1);// 获取此题的所有数据
                    String questionContext = question.getQuestionContext();
                    String titleContent = filterTitleNumber(questionContext);// 题目内容
                    jsonObject1.put("titleContent", titleContent);
                    List<String> labelList = new LinkedList<>();// 此题的标签属
                    String questionAttribute = question.getQuestionAttribute();// 此题的知识点属性
                    //labelList.add(wrongQustion.getExamCategory());// 分类标签： 章节还是 专项等
                    for (String s : questionSource) {
                        labelList.add(s);
                    }
                    if (questionAttribute.contains(",")) {
                        String[] split = questionAttribute.split(",");
                        for (int i = 0; i < split.length; i++) {
                            String s = split[i];
                            if (s.equals("")){
                                continue;
                            }
                            labelList.add(s);
                        }
                    } else {
//                        labelList.add(questionAttribute);
                        if (!questionAttribute.equals("")){
                            labelList.add(questionAttribute);
                        }
                    }
                    jsonObject1.put("labelList", labelList);// 题的标签
                    jsonObject1.put("question", question);// 此题的所有情况
                    jsonArray.add(jsonObject1);
                    json.put("questionInfo", jsonArray);
                }
            }
        } else {
            // 获取全部的题，不重复的形式
            List<UserWrongQustion> wrongQustions = userWrongQustionDao.getAllByQuestion(studentNumber, gradeLevel, subject, master);//去重qustion_id
            if (wrongQustions.size() == 0) {
                info = "暂无您要查询的未掌握题情况";
                log.error("【错误信息】: {}", info);
                throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
            } else {
                for (UserWrongQustion wrongQustion : wrongQustions) {
                    JSONObject jsonObject1 = new JSONObject();// 分装Json
                    int questionId = wrongQustion.getQuestionId();
                    //此题的分类标签，可能有多个
                    List<String> questionSource = userWrongQustionDao.getallQuestionSource(studentNumber, gradeLevel, subject, master, questionId);
                    Question question = questionDao.getByIdAndValid(questionId, 1);// 获取此题的所有数据
                    String questionContext = question.getQuestionContext();
                    String titleContent = filterTitleNumber(questionContext);// 题目内容
                    jsonObject1.put("titleContent", titleContent);
                    List<String> labelList = new LinkedList<>();// 此题的标签属
                    String questionAttribute = question.getQuestionAttribute();// 此题的知识点属性
                    //labelList.add(wrongQustion.getExamCategory());// 分类标签： 章节还是 专项等
                    for (String s : questionSource) {
                        labelList.add(s);
                    }
                    if (questionAttribute.contains(",")) {
                        String[] split = questionAttribute.split(",");
                        for (int i = 0; i < split.length; i++) {
//                            labelList.add(split[i]);
                            String s = split[i];
                            if (s.equals("")){
                                continue;
                            }
                            labelList.add(s);
                        }
                    } else {
                        if (!questionAttribute.equals("")){
                            labelList.add(questionAttribute);
                        }

                    }
                    jsonObject1.put("labelList", labelList);// 题的标签
                    jsonObject1.put("question", question);// 此题的所有情况
                    jsonArray.add(jsonObject1);
                    json.put("questionInfo", jsonArray);
                }
            }
        }
        return json;
    }

    @Override
    public JSONObject getClassification(String studentNumber, String openid, String subject, String examCategory, String gradeLevel, int master) {
        JSONArray jsonArray1 = new JSONArray();// 分类详情
        JSONObject jsonObject = new JSONObject();
        //分类详情
        if (examCategory.equals("章节练习")) {
            //获取掌握或未掌握的 节的名称
            List<String> sectionName = userWrongQustionDao.getSectionName(studentNumber, gradeLevel, subject, master, examCategory);
            if (sectionName.size() == 0) {
                info = "您章节练习模块中未有错题";
                log.error("【错误信息】: {}", info);
                throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
            }
            Map<String, Map<String, Integer>> map = new HashMap<>();
            Map<String, Integer> sectionInfoMap = new HashMap<>();//k:节的名称、v:错题的数量
            Map<String, Integer> chapterMap = new HashMap<>();//k:章的名称、v:错题的数量
            for (String section : sectionName) {
                // 根据节的获取章的名称
                List<String> chapterName = chapterDao.findBySection(section);
                String chapter = chapterName.get(0);
                int chapterNum = userWrongQustionDao.getErrorNumByChapter(studentNumber, subject, chapter, master);
                chapterMap.put(chapter, chapterNum);
                int number = userWrongQustionDao.getByErrNumber(studentNumber, subject, section, master);
                sectionInfoMap.put(section, number);

                map.put(chapter, sectionInfoMap);
                log.info("【map: 】{}", map);
            }
//            jsonArray1.add(map);
            jsonObject.put("info", map);//分类详情
            List<UserWrongQustion> wrongQustions = userWrongQustionDao.getAllInfo(studentNumber, gradeLevel, subject, master, examCategory);
            jsonObject.put("totalNum", wrongQustions.size());
            return jsonObject;
        } else if (examCategory.equals("专项练习")) {
            //1. 列出所有错题的知识点属性
            List<String> questionLabelList = userWrongQustionDao.getQuestionAttribute(studentNumber, gradeLevel, subject, master, examCategory);
            if (questionLabelList.size() == 0) {
                info = "您专项练习模块中未有错题";
                log.error("【错误信息】: {}", info);
                throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
            }
            List<String> labelList = new ArrayList<>();// 错题的所有属性
            for (String questionAttribute : questionLabelList) {
                if (questionAttribute.contains(",")) {
                    String[] split = questionAttribute.split(",");
                    for (int i = 0; i < split.length; i++) {
                        if (labelList.contains(split[i])) {
                            continue;
                        }
                        labelList.add(split[i]);
                    }
                } else {
                   if (labelList.contains(questionAttribute)){
                       continue;
                   }
                    labelList.add(questionAttribute);
                }
            }
            log.info("【labelList：】{}",labelList);
            Map<String, Integer> map = new HashMap<>();
            for (String string : labelList){
                int questionAttributeNum = userWrongQustionDao.getQuestionAttributeNum(studentNumber, gradeLevel, subject, master, examCategory, string);
                map.put(string,questionAttributeNum);
            }
            jsonObject.put("info", map);//分类详情
            List<UserWrongQustion> wrongQustions = userWrongQustionDao.getAllChapterInfo(studentNumber, gradeLevel, subject, master, examCategory);
            jsonObject.put("totalNum", wrongQustions.size());
            return jsonObject;
        }else if (examCategory.equals("全部")){
            List<UserWrongQustion> userWrongQustions = userWrongQustionDao.totalNum(studentNumber, subject, gradeLevel, master);
            if (userWrongQustions.size() == 0) {
                info = "您曾未有错题";
                log.error("【错误信息】: {}", info);
                throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
            }
            jsonObject.put("totalNum", userWrongQustions.size());
            return jsonObject;
        }else {
            // 历年真题和 模拟考试
            //1. 先获取所有错题或正确题的 去重后的名称,放到list中
            List<String> examPaperNameList = userWrongQustionDao.getExamPaperName(studentNumber, gradeLevel, subject, master, examCategory);
            if (examPaperNameList.size() == 0) {
                info = "您专项考试模块中未有错题";
                log.error("【错误信息】: {}", info);
                throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
            }
            //2. 遍历list然后查询有多少道错题，放到map中
            Map<String, Integer> map = new HashMap<>();
            for (String examName : examPaperNameList){
                int examNameNum = userWrongQustionDao.getgetExamPaperNameNum(studentNumber, gradeLevel, subject, master, examName);
                map.put(examName,examNameNum);
            }
            jsonObject.put("info", map);//分类详情
            int totalExamPaperNum = userWrongQustionDao.getTotalExamPaperNum(studentNumber, gradeLevel, subject, master, examCategory);
            jsonObject.put("totalNum", totalExamPaperNum);

        }
        return jsonObject;
    }

    @Override
    public JSONObject getClassificationQuantity(String studentNumber, String openid, String subject, String gradeLevel, int master) {
        JSONObject jsonObject = new JSONObject();
        // 获取 全部的 已掌握或未掌握的 数量
        List<UserWrongQustion> userWrongQustions = userWrongQustionDao.totalNum(studentNumber, subject, gradeLevel, master);
        jsonObject.put("totalNum", userWrongQustions.size());
        // 章节练习 数量
        List<UserWrongQustion> wrongQustions = userWrongQustionDao.getAllInfo(studentNumber, gradeLevel, subject, master, "章节练习");
        jsonObject.put("chapterNum", wrongQustions.size());
        //专项练习 数量
        List<UserWrongQustion> wrongQustions2 = userWrongQustionDao.getAllChapterInfo(studentNumber, gradeLevel, subject, master, "专项练习");
        jsonObject.put("specialNum", wrongQustions2.size());
        //模拟考试 数量
        int totalExamPaperNum = userWrongQustionDao.getTotalExamPaperNum(studentNumber, gradeLevel, subject, master, "模拟考试");
        jsonObject.put("mockNum", totalExamPaperNum);
        // 历年真题 数量
        int totalExamPaperNum2 = userWrongQustionDao.getTotalExamPaperNum(studentNumber, gradeLevel, subject, master, "历年真题");
        jsonObject.put("truthNum", totalExamPaperNum2);

        return jsonObject;
    }


    @Override
    public JSONObject getQuestionInfo(int id, String stuNumber, String openid) {
        JSONObject jsonObject = new JSONObject();
        Question question = questionDao.getByIdAndValid(id, 1);
        if (question == null){
            info = "您专项考试模块中未有错题";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        jsonObject.put("question",question);
        List<String> optionList = new LinkedList<>();// 此题选项的list
        String oneQuestionOption = question.getQuestionOption();//获取所有选项的文本
        String questionOption = filterspecial(oneQuestionOption);//过滤下\t,\n等字符
        log.info("【去除t,n等字符】： {}", questionOption);
        int i1 = -1;
        if (questionOption.indexOf("A．") != -1) {
            i1 = questionOption.indexOf("A．");
        } else {
            i1 = questionOption.indexOf("A.");
        }
        int i2 = -1;
        if (questionOption.indexOf("B．") != -1) {
            i2 = questionOption.indexOf("B．");
        } else {
            i2 = questionOption.indexOf("B.");
        }
        int i3 = -1;
        if (questionOption.indexOf("C．") != -1) {
            i3 = questionOption.indexOf("C．");
        } else {
            i3 = questionOption.indexOf("C.");
        }
        int i4 = -1;
        if (questionOption.indexOf("D．") != -1) {
            i4 = questionOption.indexOf("D．");
        } else {
            i4 = questionOption.indexOf("D.");
        }
        List<Integer> letterList = new ArrayList<>();
        letterList.add(i1);
        letterList.add(i2);
        letterList.add(i3);
        letterList.add(i4);
        String str1 = questionOption.substring(i1+2, i2);//A选项
        String str2 = questionOption.substring(i2+2 , i3);//B选项
        String str3 = questionOption.substring(i3+2 , i4);//C选项
        String str4 = questionOption.substring(i4+2 , questionOption.length());//D选项
        optionList.add("A."+str1);
        optionList.add("B."+str2);
        optionList.add("C."+str3);
        optionList.add("D."+str4);
        jsonObject.put("optionList",optionList);
        List<String> imgList = new LinkedList<>();
        String questionImgs = question.getQuestionImgs();
        if (questionImgs == null){
//            imgList.add();
            jsonObject.put("imgList",imgList);
        }
        else if (questionImgs.contains(",")){
            String[] split = questionImgs.split(",");
            for (int i=0; i<split.length;i++){
                imgList.add(split[i]);
            }
            jsonObject.put("imgList",imgList);
        }else {
            if (!questionImgs.equals("")){
                imgList.add(questionImgs);
            }
            jsonObject.put("imgList",imgList);
        }
//        jsonObject.put("imgList",imgList);
        int collect = 0;// 是否收藏， 1为这道题已经收藏，2为未收藏
        UserCollect userCollect = userCollectDao.findByStudentNumberAndQuestionId(stuNumber, id);
        if (userCollect == null){
            collect = 2;// 未收藏
        }else if (userCollect.getValid() == 1){
            collect =1;// 已收藏
        }else if (userCollect.getValid() == 2){
            collect =2;//未收藏
        }
        jsonObject.put("collect",collect);
        return jsonObject;
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

    /**
     * 公共函数 3
     * A．较大的稳定性B．选择透性C．一定的流动性D．运输物质的功能
     * 将 上述字符串 切分然后放到list中
     */
    public static List<String> stringTurnList(String string) {
        List<String> list = new LinkedList<>();
        int i1 = string.indexOf("A．");
        int i2 = string.indexOf("B．");
        int i3 = string.indexOf("C．");
        int i4 = string.indexOf("D．");

        String str1 = string.substring(i1, i2);//A选项
        String str2 = string.substring(i2, i3);//B选项
        String str3 = string.substring(i3, i4);//C选项
        String str4 = string.substring(i4, string.length());//D选项
        list.add(str1);
        list.add(str2);
        list.add(str3);
        list.add(str4);

        return list;
    }

    /**
     * 公共函数 4 将中文的. 改为 英文的.
     * A．较大的稳定性B．选择透性C．一定的流动性D．运输物质的功能
     * 将 上述字符串 切分然后放到list中
     */
    public static List<String> stringTurnList2(String string) {
        List<String> list = new LinkedList<>();
        int i1 = -1;
        if (string.indexOf("A．") != -1) {
            i1 = string.indexOf("A．");
        } else {
            i1 = string.indexOf("A.");
        }
        int i2 = -1;
        if (string.indexOf("B．") != -1) {
            i2 = string.indexOf("B．");
        } else {
            i2 = string.indexOf("B.");
        }
        int i3 = -1;
        if (string.indexOf("C．") != -1) {
            i3 = string.indexOf("C．");
        } else {
            i3 = string.indexOf("C.");
        }
        int i4 = -1;
        if (string.indexOf("D．") != -1) {
            i4 = string.indexOf("D．");
        } else {
            i4 = string.indexOf("D.");
        }

        String str1 = "A." + string.substring(i1 + 2, i2);//A选项
        String str2 = "B." + string.substring(i2 + 2, i3);//B选项
        String str3 = "C." + string.substring(i3 + 2, i4);//C选项
        String str4 = "D." + string.substring(i4 + 2, string.length());//D选项
        list.add(str1);
        list.add(str2);
        list.add(str3);
        list.add(str4);

        return list;
    }


    @Override
    public JSONObject getAllKnowledge(String studentNumber, String openid, String subject, String gradeLevel) {
        JSONObject jsonObject = new JSONObject();
        // 查询所有知识点
        List<String> questionAttributes = questionDao.getQUestionAttribute(subject, gradeLevel);
        if (questionAttributes.size() == 0) {
            info = "该年级、该科目中暂时没有知识点";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        } else {
            List<String> attributesList = new ArrayList<>();
            attributesList.add("全部");
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
                    if (!(attributesList.contains(questionAttribute))) {
                        attributesList.add(questionAttribute);
                    }
                }
            }
            jsonObject.put("attributesList", attributesList);
            return jsonObject;
        }
    }

    @Override
    public JSONArray getAllQuestionByPoint(String studentNumber, String openid, String subject, String gradeLevel, String knowledgePoint) {
//        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        if (!knowledgePoint.equals("全部")) {
            List<Question> questions = questionDao.getAllSubjectAndLevelNameByQuestionAndAttribute(subject, gradeLevel, knowledgePoint);
            if (questions.size() == 0) {
                info = "该年级、该科目中暂时没有该知识点的题";
                log.error("【错误信息】: {}", info);
                throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
            } else {
                List<String> questionTitleList = new ArrayList<>();// 用户去除 题目相同的题
                for (Question question : questions) {
                    JSONObject jsonObject1 = new JSONObject();
                    String questionContext = question.getQuestionContext();
                    String titleContent = filterTitleNumber(questionContext);// 题目内容
                    if (questionTitleList.contains(titleContent)){
                        continue;
                    }
                    questionTitleList.add(titleContent);// 加入list中
                    jsonObject1.put("question", question);
                    jsonObject1.put("titleContent", titleContent);
                    List<String> labelList = new LinkedList<>();// 此题的标签属
                    String questionAttribute = question.getQuestionAttribute();// 此题的知识点属性
                    if (questionAttribute.contains(",")) {
                        String[] split = questionAttribute.split(",");
                        for (int i = 0; i < split.length; i++) {
                            String s = split[i];
                            if (s.equals("")) {
                                continue;
                            }
                            labelList.add(s);
                        }
                    } else {
//                        labelList.add(questionAttribute);
                        if (!questionAttribute.equals("")){
                            labelList.add(questionAttribute);
                        }
                    }
                    jsonObject1.put("labelList", labelList);// 题的标签
                    jsonArray.add(jsonObject1);
                }
                return jsonArray;
            }
        }else {
            // 默认 显示 全部的情况
            List<Question> questions = questionDao.getAllSubjectAndLevelName(subject, gradeLevel);
            if (questions.size() == 0){
                info = "该年级、该科目中暂时没有题";
                log.error("【错误信息】: {}", info);
                throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
            }
            List<String> questionTitleList = new ArrayList<>();// 用户去除 题目相同的题
            for (Question question : questions){
                JSONObject jsonObject1 = new JSONObject();
                String questionContext = question.getQuestionContext();
                String titleContent = filterTitleNumber(questionContext);// 题目内容
                if (questionTitleList.contains(titleContent)){
                    continue;
                }
                questionTitleList.add(titleContent);// 加入list中
                jsonObject1.put("question", question);
                jsonObject1.put("titleContent", titleContent);
                List<String> labelList = new LinkedList<>();// 此题的标签属
                String questionAttribute = question.getQuestionAttribute();// 此题的知识点属性
                if (questionAttribute.contains(",")) {
                    String[] split = questionAttribute.split(",");
                    for (int i = 0; i < split.length; i++) {
                        String s = split[i];
                        if (s.equals("")){
                            continue;
                        }
                        labelList.add(s);
                    }
                } else {
//                    labelList.add(questionAttribute);
                    if (!questionAttribute.equals("")){
                        labelList.add(questionAttribute);
                    }
                }
                jsonObject1.put("labelList", labelList);// 题的标签
                jsonArray.add(jsonObject1);
            }
            return jsonArray;
        }
    }

    @Transactional
    @Override
    public JSONObject specialRecordId(int id, String studentNumber, String openid, String commitString, String examCategory,String subject, String gradeLevel, String doTime) {
        JSONObject jsonObject = new JSONObject();
        Question question = questionDao.getByIdAndValid(id, 1);
        if (question == null) {
            info = "您所查询的此题不存在，请核对后再查";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        String userAnswer = optionLetter(commitString);//用户的答案
        List<UserQuestionRecord> repatQuestion = userQuestionRecordDao.getSpecialRecord(studentNumber, examCategory, id, subject);
        UserQuestionRecord save = null;
        if (repatQuestion == null || repatQuestion.size() == 0) {
            UserQuestionRecord userQuestionRecord = new UserQuestionRecord();
            if (question.getCorrectText().equals(userAnswer)) {
                userQuestionRecord.setDoRight(1);
            } else {
                userQuestionRecord.setDoRight(2);
            }
            userQuestionRecord.setUserAnswer(userAnswer);
            userQuestionRecord.setSubject(subject);
            userQuestionRecord.setStudentNumber(studentNumber);
            userQuestionRecord.setOpenid(openid);
            userQuestionRecord.setQuestionId(id);
//            userQuestionRecord.setExamPaperId(sourcePaperId);// 试卷id：（不是这道题是从哪个试卷中录入进去的）保存这道题被组卷在哪套试题中
            userQuestionRecord.setTimes(1);
//            userQuestionRecord.setExamPaperName(paperExamName);
            userQuestionRecord.setExamCategory(examCategory);
            userQuestionRecord.setDoTime(doTime);//2.2 新增做题时间
             save = userQuestionRecordDao.save(userQuestionRecord);
        } else {
            int times = repatQuestion.get(0).getTimes();
            int repatTime = times + 1;
            UserQuestionRecord userQuestionRecord = new UserQuestionRecord();

            if (question.getCorrectText().equals(userAnswer)) {
                userQuestionRecord.setDoRight(1);
            } else {
                userQuestionRecord.setDoRight(2);
            }
            userQuestionRecord.setUserAnswer(userAnswer);
            userQuestionRecord.setSubject(subject);
            userQuestionRecord.setStudentNumber(studentNumber);
            userQuestionRecord.setOpenid(openid);
            userQuestionRecord.setQuestionId(id);
//            userQuestionRecord.setExamPaperId(sourcePaperId);// 试卷id：（不是这道题是从哪个试卷中录入进去的）保存这道题被组卷在哪套试题中
            userQuestionRecord.setTimes(repatTime);
//            userQuestionRecord.setExamPaperName(paperExamName);
            userQuestionRecord.setExamCategory(examCategory);
            userQuestionRecord.setDoTime(doTime);//2.2 新增做题时间
             save = userQuestionRecordDao.save(userQuestionRecord);
        }
        // 新增往错题表中插数据
        List<UserQuestionRecord> repatQuestion2 = userQuestionRecordDao.getSpecialRecord(studentNumber, examCategory, id, subject);
        UserQuestionRecord questionRecord = repatQuestion2.get(0);// 获取刚插入的此题所有数据
        if (questionRecord.getDoRight() == 2) {
            // 此题错误，判断此题的 相同来源是否 插入过库中
            UserWrongQustion userWrong = userWrongQustionDao.getByStudentNumberAndExamCategoryAndQuestionId(studentNumber, questionRecord.getExamCategory(), id, subject);
            if (userWrong == null) {
                //如果不存在，则插入
                UserWrongQustion wrongQustion = new UserWrongQustion();
                wrongQustion.setStudentNumber(studentNumber);
                wrongQustion.setOpenid(openid);
                wrongQustion.setSubject(subject);
                wrongQustion.setDoRight(2);
                wrongQustion.setQuestionId(id);
                wrongQustion.setUserAnswer(userAnswer);
//                wrongQustion.setExamPaperId(sourcePaperId);// 试卷id：（不是这道题是从哪个试卷中录入进去的）保存这道题被组卷在哪套试题中
//                wrongQustion.setExamPaperName(paperExamName);
                wrongQustion.setExamCategory(examCategory);
                wrongQustion.setDoTime(doTime);//2.2 新增做题时间
                userWrongQustionDao.save(wrongQustion);
            }
        }
        jsonObject.put("save", save);
        return jsonObject;
    }

    @Override
    public JSONObject getAllExamName(String studentNumber, String openid, String subject, String gradeLevel,String examCategory) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray1 = new JSONArray();
        JSONArray jsonArray2 = new JSONArray();
        JSONArray jsonArray3 = new JSONArray();
        //1. 获取模拟题的 所有数据
        List<ExamPaper> mockExam = examPaperDao.getAllBySubjectAndGradeLevelAndExamSource(subject, gradeLevel, examCategory, "%期中%", "%期末%");
        if (mockExam.size() > 0){
            for (ExamPaper examPaper : mockExam){
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("name",examPaper.getExamName());
                // 去除[] 和 空格，或者从插库时处理，直接就存1,2,3... ；而不是存成[1, 2, 3...]
                String[] questionList = filterMiddleBrackets(examPaper.getQuestionList()).split(",");
                List<Integer> idList = new ArrayList<>();
                for (int i = 0; i < questionList.length; i++) {
                    int integer = Integer.parseInt(questionList[i]);
                    idList.add(integer);
                }
                jsonObject1.put("count",idList.size());
                jsonObject1.put("difficult",examPaper.getDifficult());
                jsonObject1.put("totalScore",examPaper.getExamScore());
                jsonArray1.add(jsonObject1);
            }
        }
        //2. 获取所有 期中考试的 数据
        List<ExamPaper> midtermList = examPaperDao.getAllBySubjectAndGradeLevelAndExamSource2(subject, gradeLevel, examCategory, "%期中%");
        if (midtermList.size() > 0){
            for (ExamPaper examPaper : midtermList){
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("name",examPaper.getExamName());
                // 去除[] 和 空格，或者从插库时处理，直接就存1,2,3... ；而不是存成[1, 2, 3...]
                String[] questionList = filterMiddleBrackets(examPaper.getQuestionList()).split(",");
                List<Integer> idList = new ArrayList<>();
                for (int i = 0; i < questionList.length; i++) {
                    int integer = Integer.parseInt(questionList[i]);
                    idList.add(integer);
                }
                jsonObject1.put("count",idList.size());
                jsonObject1.put("difficult",examPaper.getDifficult());
                jsonObject1.put("totalScore",examPaper.getExamScore());
                jsonArray2.add(jsonObject1);
            }
        }
//2. 获取所有 期中考试的 数据
        List<ExamPaper> finalExam = examPaperDao.getAllBySubjectAndGradeLevelAndExamSource2(subject, gradeLevel, examCategory, "%期末%");
        if (finalExam.size() > 0){
            for (ExamPaper examPaper : finalExam){
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("name",examPaper.getExamName());
                // 去除[] 和 空格，或者从插库时处理，直接就存1,2,3... ；而不是存成[1, 2, 3...]
                String[] questionList = filterMiddleBrackets(examPaper.getQuestionList()).split(",");
                List<Integer> idList = new ArrayList<>();
                for (int i = 0; i < questionList.length; i++) {
                    int integer = Integer.parseInt(questionList[i]);
                    idList.add(integer);
                }
                jsonObject1.put("count",idList.size());
                jsonObject1.put("difficult",examPaper.getDifficult());
                jsonObject1.put("totalScore",examPaper.getExamScore());
                jsonArray3.add(jsonObject1);
            }
        }
        jsonObject.put("mockExam",jsonArray1);//模拟题
        jsonObject.put("midterm",jsonArray2);//期中模拟题
        jsonObject.put("finalExam",jsonArray3);//期末模拟题
        return jsonObject;
    }

    @Override
    public JSONObject continueLearn(String studentNumber, String openid, String subject) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        List<UserQuestionRecord> echolist = userQuestionRecordDao.getByStudentNumberAndSubjectAndExamPaperId2(studentNumber, subject);
        if (echolist == null || echolist.size() == 0) {
            info = "您还未做过此试卷，暂无记录";
            log.error("【错误信息】: {}", info);
            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
        }
        // 调用公共函数 2，获取questionList
        ExamPaper one = examPaperDao.findOne(echolist.get(0).getExamPaperId());
        List<Integer> questionList = questionList(one.getQuestionList());
        List<String> chapter = chapterDao.findBySection(echolist.get(0).getExamPaperName());//章的名称
        int times = echolist.get(0).getTimes();
        for (UserQuestionRecord questionRecord : echolist) {
            if (questionRecord.getTimes() == times) {
                JSONObject jsonObject1 = new JSONObject();
                // 题号
                int questionNo = questionList.indexOf(questionRecord.getQuestionId()) + 1;
                jsonObject1.put("questionNo", questionNo);
                // 题填写的文本

                jsonObject1.put("questionNoText",questionRecord.getUserAnswer());
                jsonArray.add(jsonObject1);

            }

        }
        jsonObject.put("examPaperName",chapter.get(0));
        jsonObject.put("sectionName",echolist.get(0).getExamPaperName());
        jsonObject.put("info",jsonArray);
        return jsonObject;
    }

}

