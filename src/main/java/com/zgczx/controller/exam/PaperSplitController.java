package com.zgczx.controller.exam;

import com.alibaba.fastjson.JSONObject;
import com.zgczx.VO.ResultVO;
import com.zgczx.repository.mysql1.exam.dao.*;
import com.zgczx.repository.mysql1.exam.model.Chapter;
import com.zgczx.repository.mysql1.exam.model.ExamContent;
import com.zgczx.repository.mysql1.exam.model.ExamPaper;
import com.zgczx.repository.mysql1.exam.model.Question;
import com.zgczx.repository.mysql3.unifiedlogin.dao.UserLoginDao;
import com.zgczx.service.exam.PaperSplitService;
import com.zgczx.utils.Excel2BeanList;
import com.zgczx.utils.ResultVOUtil;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.zgczx.utils.FilterStringUtil.filterChineseAndMath;
import static com.zgczx.utils.FilterStringUtil.filterspecial;
import static com.zgczx.utils.WordRedUtil.readWord;

/**
 * @author aml
 * @date 2019/12/27 15:31
 */
@Api(description = "word解析为题库模块")
@RestController
@RequestMapping("/word")
@Slf4j
public class PaperSplitController {

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
    private PaperSplitService paperSplitService;

    private String info;


    @ApiOperation(value = "一、 读取Word中的内容并生成json串处理")
    @PostMapping("/parseWord2")
    public ResultVO<?> parseWord2(
            @ApiParam(value = "file文件", required = true)
            @RequestParam("filename") MultipartFile file,
            HttpSession session, HttpServletRequest request) throws Exception{

        String text = null;

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

        ResultVO<?> resultVO = splitPaper(save.getExamName(), save.getSubject());
        log.info("【打印出结果：】{}",resultVO.getData());


        return ResultVOUtil.success(text);
    }


    @ApiOperation(value = " 将一试卷切分为一道道题存入题库表中")
    @GetMapping("/splitPaper")
    @Transactional
    public ResultVO<?> splitPaper(
            @ApiParam(value = "试卷全称", required = true)
            @RequestParam("paperName") String paperName,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject
    ) {
        ExamContent examContent = examContentDao.findByExamNameAndSubject(paperName, subject);
//        String[] split = examContent.getContent().split("#");

        // 1. 获取使用年级
        String content = examContent.getContent();//获取Word中的所有数据
        int gradeStartIndex = content.indexOf("【适用年级】");
        int gradeEndIndex = content.indexOf("一、选择题");
        String gradeLevel = content.substring(gradeStartIndex + 6, gradeEndIndex);

        String[] split = content.split("#");
        log.info("[split: ]{}", split);
        // 2. 确定此Word是章节练习还是模拟考试
        String wordType = split[0];
        String paperType = null;
        if (wordType.contains("节")) {
            paperType = "章节练习";
        } else {
            paperType = "模拟练习";
        }

        List<Integer> idList = new ArrayList<>();
        for (int i = 1; i < split.length; i++) {
            Question question = new Question();
            String s = filterspecial(split[i]);//过滤下\t,\n等字符
            log.info("【去除t,n等字符的每道题的内容】： {}", s);
            int anwerIndex = s.indexOf("【答案】");//此题的答案索引位置
            String itemOption = s.substring(0, anwerIndex);//题干+选项
            question.setQuestionContext(itemOption);
            int analysisIndex = s.indexOf("【解析】");// 此题的解析索引位置
            String anwerOption = s.substring(analysisIndex - 1, analysisIndex);// 答案选项
            question.setCorrectOption(anwerOption);
            //正确的答案文本
           /* int i1 = s.indexOf("A．");
            int i2 = s.indexOf("B．");
            int i3 = s.indexOf("C．");
            int i4 = s.indexOf("D．");*/
            int i1 = -1;
            if (s.indexOf("A．") != -1) {
                i1 = s.indexOf("A．");
            } else {
                i1 = s.indexOf("A.");
            }
            int i2 = -1;
            if (s.indexOf("B．") != -1) {
                i2 = s.indexOf("B．");
            } else {
                i2 = s.indexOf("B.");
            }
            int i3 = -1;
            if (s.indexOf("C．") != -1) {
                i3 = s.indexOf("C．");
            } else {
                i3 = s.indexOf("C.");
            }
            int i4 = -1;
            if (s.indexOf("D．") != -1) {
                i4 = s.indexOf("D．");
            } else {
                i4 = s.indexOf("D.");
            }
            String contentA = s.substring(i1 + 2, i2);//A选项
            String contentB = s.substring(i2 + 2, i3);//B选项
            String contentC = s.substring(i3 + 2, i4);//C选项
            String contentD = s.substring(i4 + 2, anwerIndex);//D选项
            if (anwerOption.trim().equals("A")) {
                question.setCorrectText(contentA);//正确答案的文本
            } else if (anwerOption.trim().equals("B")) {
                question.setCorrectText(contentB);//正确答案的文本
            } else if (anwerOption.trim().equals("C")) {
                question.setCorrectText(contentC);//正确答案的文本
            } else {
                question.setCorrectText(contentD);//正确答案的文本
            }

            int knowledgePointsIndex = s.indexOf("【知识点】");// 知识点索引位置
            String analysisText = s.substring(analysisIndex, knowledgePointsIndex);//此题的解析文本
            question.setCorrectAnalysis(analysisText);
            int difficultyIndex = s.indexOf("【难易程度】");// 难易程度索引位置
            String knowledgePoint = s.substring(knowledgePointsIndex + 5, difficultyIndex);//此题的知识点属性
            question.setQuestionAttribute(knowledgePoint);
            int cognition = s.indexOf("【认知层次】");// 认知层次索引位置
            String difficultyText = s.substring(difficultyIndex + 6, cognition);//此题的难易程度
            question.setQuestionDifficult(difficultyText);
            String cognitionText = s.substring(cognition + 6, s.length());//此题的认知层次
            question.setCognitiveLevel(cognitionText);

            // 还剩下一个 图片的字段没存了

            question.setExamLocation("北京");
            question.setQuestionType("单选");
            question.setQuestionSource(paperType);
            question.setExamName(examContent.getExamName());
            question.setSubject(examContent.getSubject());
            question.setValid(1);
            question.setLevelName(gradeLevel);// 适用年级： 例如：高1

            Question save = questionDao.save(question);

            idList.add(save.getId());// 将每个题 在题库表的主键id存放到list中去
        }
        // 将数据存到 exampaer中，试卷中
        ExamPaper examPaper = new ExamPaper();
        String examName = examPaper.getExamName();
//        String[] split1 = examName.split(",");
        String[] split1 = null;
        if (wordType.contains(",")){
            split1 = wordType.split(",");
        }else if (wordType.contains("，")){
            split1 = wordType.split("，");
        }

        String string = split1[1];
        int gradeIndex = string.indexOf("【适用年级】");
        String sectionString = string.substring(0, gradeIndex);

        // 将数据存入chapter中，章节中
        Chapter chapter = new Chapter();
        chapter.setSubject("生物");
        chapter.setSubjectId(6);
        chapter.setChapter(split1[0]);
        chapter.setSection(sectionString);
        chapter.setLevelName(gradeLevel);
        Chapter chapterSave = chapterDao.save(chapter);

        examPaper.setExamName(sectionString);
        examPaper.setQuestionList(String.valueOf(idList));
        examPaper.setExamSource(paperType);
        examPaper.setExamLocation("北京");
        examPaper.setSubject("生物");
        examPaper.setSubjectId(6);
        examPaper.setQuestionCount(idList.size());
        examPaper.setExamContentId(examContent.getId());
        examPaper.setExamContent(content);
        examPaper.setGradeLevel(gradeLevel);
        examPaper.setChapterId(chapterSave.getId());
        examPaper.setValid(1);

        ExamPaper examPaperSave = examPaperDao.save(examPaper);

        int ids = questionDao.updateByIds(idList, examPaperSave.getId());
        log.info("【共修改的条数：】{}", ids);
//        examPaper.setExamName();


        return ResultVOUtil.success(idList);
    }

    @ApiOperation(value = "用户信息批量录入", notes = "根据excel文件进行用户信息批量录入")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file_url", value = " 文件服务器地址 ", required = true, dataType =  "__file", paramType = "form")//不是 file，而是双下划线“__file”:原因是版本不一样：dataType =  "__file"
    })
    @RequestMapping(value = "/batchAddUser", method = RequestMethod.POST)
    public JSONObject batchAddUser(HttpServletResponse response, HttpServletRequest request) throws Exception {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request; // 将普通请求转换为文件请求
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap(); // 获取所有上传的文献集合，数量不限
        List<Map<String, Object>> content = new ArrayList<>();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) { // 遍历单个文件
            MultipartFile mf = entity.getValue();
            try {
                content = Excel2BeanList.readExcel(mf, 33); // 33指excel中的最大列数，可以根据需要调整

            } catch (IOException e) {
                e.printStackTrace();
                JSONObject json_store = new JSONObject();
                json_store.put("errno", "001");
                json_store.put("errmsg", "批量插入用户信息失败!");
                return json_store;
            }
        }
        JSONObject jsonRespond = paperSplitService.batchAddUser(content);

        return jsonRespond;
    }
}
