package com.zgczx.controller.exam;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zgczx.VO.ResultVO;
import com.zgczx.service.exam.ExamTwoService;
import com.zgczx.utils.ResultVOUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 在线题库第二个controller
 * @author lxj
 * @date 2019/12/29
 */

@Api(description = "第二个exam模块")
@RestController
@RequestMapping("/exam-two")
@Slf4j
public class ExamTwoController {

    @Autowired
    private ExamTwoService examTwoService;

    @ApiOperation(value = "一、统计错题数")
    @GetMapping("/getErrorProblemsNum")
    public ResultVO<?> getErrorProblemsNum(
            @ApiParam(value = "用户学号",required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid", required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "科目名称",required = true)
            @RequestParam("subject") String subject
    ){
        Map<String ,Integer> errorProblemsNum=examTwoService.getErrorProblemsNum(studentNumber,openid,subject);
        return ResultVOUtil.success(errorProblemsNum);
    }

    @ApiOperation(value="二、统计收藏题数")
    @GetMapping("/getCollectProblemsNum")
    public ResultVO<?> getCollectProblemsNum(
            @ApiParam(value = "用户学号",required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid", required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "科目名称",required = true)
            @RequestParam("subject") String subject
    ){
        Map<String, Integer> collectProblemsNum=examTwoService.getCollectProblemsNum(studentNumber,openid,subject);
        return ResultVOUtil.success(collectProblemsNum);
    }

    @ApiOperation(value = "三、我的收藏页面中，默认选择练习题收藏（包括章 和对应的收藏题数），选择考试题收藏时，显示考试名称和对应收藏题数  ")
    @GetMapping("/getChapterCollectNumber")
    public ResultVO<?> getCollectNumber(
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid", required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value="题来源",required=true)
            @RequestParam("category") String category
    ){
        Map<String ,Integer> getCollectNums=examTwoService.getCollectProblemsNum(studentNumber,openid,subject,category);
        return ResultVOUtil.success(getCollectNums);
    }

    @ApiOperation(value = "四、 练习题收藏：点击某一章，查询该章包含的节的名称 和对应收藏题数")
    @GetMapping("/getSectionCollectNumber")
    public ResultVO<?> getSectionsCollectNum(
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid", required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value="章节名称",required=true)
            @RequestParam("chapter") String chapter
    ){
        Map<String,Integer> getSectionsAndCollectNum=examTwoService.getCollectProblemsNumByChapter(studentNumber,openid,subject,chapter);
        return ResultVOUtil.success(getSectionsAndCollectNum);
    }

    @ApiOperation(value="五、 练习题收藏：点击章、节显示 此小节收藏题的详细信息")
    @GetMapping("/getChapterCollectProblems")
    public ResultVO<?> getChapterCollectProblems(
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid", required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value="章节名称",required=true)
            @RequestParam("chapter") String chapter,
            @ApiParam(value="小节名称",required=true)
            @RequestParam("section") String section
    ){
        JSONArray sectionCollectProblems=examTwoService.getSectionCollectProblems(studentNumber, openid, subject, chapter, section);
        return ResultVOUtil.success(sectionCollectProblems);
    }

    @ApiOperation(value = "六、 考试题收藏：点击某次考试，显示收藏的本次考试题的详细信息")
    @GetMapping("/getExamCollectProblems")
    public ResultVO<?> getExamCollectProblems(
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid", required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value="考试名称",required=true)
            @RequestParam("examName") String examName
    ){
        JSONArray examCollectProblems=examTwoService.getExamCollectProblems(studentNumber,openid,subject,examName);
        return ResultVOUtil.success(examCollectProblems);
    }

    @ApiOperation(value = "七、 练习错题：点击章、节显示 此小节错题的详细信息")
    @GetMapping("/getSectionErrorProblems")
    public ResultVO<?> getSectionErrorProblems(
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid", required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value="章名称",required=true)
            @RequestParam("chapter") String chapter,
            @ApiParam(value="节名称",required=true)
            @RequestParam("section") String section,
            @ApiParam(value = "是否掌握(已掌握/未掌握)",required = true)
            @RequestParam("ifMastered") String ifMastered
    ){
        JSONArray examErrorProblems=examTwoService.getErrorProblemsByChapterAndSection(studentNumber,openid,subject,chapter,section,ifMastered);
        return ResultVOUtil.success(examErrorProblems);
    }

    @ApiOperation(value = "八、 考试错题：点击某次考试，显示本次考试错题的详细信息")
    @GetMapping("/getExamErrorProblems")
    public ResultVO<?> getExamErrorProblems(
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid", required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value="考试名称",required=true)
            @RequestParam("examName") String examName,
            @ApiParam(value = "是否掌握(已掌握/未掌握)",required = true)
            @RequestParam("ifMastered") String ifMastered
    ){
        JSONArray examErrorProblems=examTwoService.getErrorProblemsByExamName(studentNumber,openid,subject,examName,ifMastered);
        return ResultVOUtil.success(examErrorProblems);
    }

    @ApiOperation(value="九、 删除已掌握错题中的某道题")
    @PostMapping("/deleteMasteredQuestions")
    public ResultVO<?> deleteMasteredQuestions(
            @ApiParam(value = "用户学号",required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid",required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "学科",required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "删除题号id",required = true)
            @RequestParam("questionId") int questionId,
            @ApiParam(value = "删除题来源（1.练习错题，2.考试错题）",required = false)
            @RequestParam("questionSource") String questionSource
    ){
        Map<String ,Integer> deleteMasterQuesiotns=examTwoService.deleteMasteredQuestions(studentNumber, openid,subject, questionId, questionSource);
        return ResultVOUtil.success(deleteMasterQuesiotns);
    }

    @ApiOperation(value="十、 做错题中未掌握的题，正确进入已掌握")
    @PostMapping("/doNotMasteredQuestions")
    public ResultVO<?> doNotMasteredQuestions(
            @ApiParam(value = "用户学号",required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid",required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "学科",required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "题号(针对所有题)",required = true)
            @RequestParam("questionId") int questionId,
            @ApiParam(value = "题来源(1.练习错题，2.考试错题)",required = true)
            @RequestParam("questionSource") String questionSource,
            @ApiParam(value = "用户答案",required = true)
            @RequestParam("userAnswer") String userAnswer,
            @ApiParam(value = "试卷id",required = true)
            @RequestParam("examPaperId") int examId,
            @ApiParam(value = "试卷名称",required = true)
            @RequestParam("examPaperName") String examName
    ){
        Map<String ,Integer> map=examTwoService.doNotMasteredQuestions(studentNumber, openid, subject, questionId, questionSource, userAnswer, examId, examName);
        return ResultVOUtil.success(map);
    }

    @ApiOperation(value = "十一、 专项练习：知识点中根据年级和科目统计每章的题数")
    @GetMapping("/getQuestionsNumsByChapter")
    public ResultVO<?> getQuestionsNumsByChapter(
            @ApiParam(value = "学科",required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "年级",required = true)
            @RequestParam("levelName") String levelName
    ){
        Map<String ,Integer> map=examTwoService.getQuestionsByChapterAndSubject(subject,levelName);
        return ResultVOUtil.success(map);
    }

    @ApiOperation(value = "十二、 专项练习：知识点中每章下所有知识点及对应的题数")
    @GetMapping("/getQuestionsNumsByAttributr")
    public ResultVO<?> getQuestionsNumsByAttributr(
            @ApiParam(value = "学科",required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "年级",required = true)
            @RequestParam("levelName") String levelName,
            @ApiParam(value = "章名称",required = true)
            @RequestParam("chapter") String chapter
    ){
        Map<String ,Integer> map=examTwoService.getQuestionsNumsByAttribute(subject, levelName, chapter);
        return ResultVOUtil.success(map);
    }

    @ApiOperation(value = "十三、 专项练习：根据章名称 和知识点查看题的详细信息")
    @GetMapping("/getQuestionsByQuestionsAttribute")
    public ResultVO<?> getQuestionsByQuestionsAttribute(
            @ApiParam(value = "用户学号",required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid",required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "学科",required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "章名称",required = true)
            @RequestParam("chapter") String chapter,
            @ApiParam(value = "年级",required = true)
            @RequestParam("levelName") String levelName,
            @ApiParam(value = "知识点",required = true)
            @RequestParam("questionAttribute") String questionAttribute
    ){
        JSONArray arr=examTwoService.getQuestionsByQuestionsAttribute(studentNumber, openid, subject, levelName, chapter, questionAttribute);
        return ResultVOUtil.success(arr);
    }

    @ApiOperation(value = "#1.27 十四、 根据学科和年级统计用户做题记录")
    @GetMapping("/getDoQuestionRecord")
    public ResultVO<?> getDoQuestionRecord(
            @ApiParam(value = "用户学号",required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid",required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "学科",required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "年级",required = true)
            @RequestParam("levelName") String levelName,
            @ApiParam(value = "筛选的开始时间",required = false)
            @RequestParam("starTime") String starTime,
            @ApiParam(value = "筛选的截止时间",required = false)
            @RequestParam("endTime") String endTime

    ){
        JSONArray array = examTwoService.getDoQUestionRecord(studentNumber, openid,subject,levelName,starTime,endTime);
        return ResultVOUtil.success(array);
    }

    @ApiOperation(value = "#1.27 十五、 做题记录：关于某一份试卷/章节/知识点做题详情(做题时间、题难易度等)")
    @GetMapping("/getDoQuestionRecordDetail")
    public ResultVO<?> getDoQuestionRecordDetail(
            @ApiParam(value = "用户学号",required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid",required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "学科",required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "年级",required = true)
            @RequestParam("levelName") String levelName,
            @ApiParam(value = "试卷名称(章节名称/知识点)",required = true)
            @RequestParam("examName") String examName,
            @ApiParam(value = "来源：章节练习、模拟考试、专项练习等",required = true)
            @RequestParam("source") String source
    ){
        JSONArray arr=examTwoService.getDoQuestionRecordDetail(studentNumber,openid,subject,levelName,examName, source);

        return ResultVOUtil.success(arr);
    }

    @ApiOperation(value = "十六、 学习记录：统计做题数")
    @GetMapping("getDoQuestionsCount")
    public ResultVO<?> getDoQuestionsCount(
            @ApiParam(value = "用户学号",required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid",required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "学科",required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "年级",required = true)
            @RequestParam("levelName") String levelName
    ){
        JSONArray arr=examTwoService.getDoQuestionsCount(studentNumber,openid,subject,levelName);
        return ResultVOUtil.success(arr);
    }

    @ApiOperation(value = "十七、 学习记录：统计错题和收藏总数")
    @GetMapping("/getWrongCollectQuestionsCount")
    public ResultVO<?> getWrongCollectQuestionsCount(
            @ApiParam(value = "用户学号",required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid",required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "学科",required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "年级",required = true)
            @RequestParam("levelName") String levelName
    ){
        Map<String ,Integer> map=examTwoService.getWrongCollectQuestionsCount(studentNumber,openid,subject,levelName);
        return ResultVOUtil.success(map);
    }

    @ApiOperation(value="十八、 学习记录：按天统计做题正确率和做题时长")
    @GetMapping("/getRightRateAndClassHours")
    public ResultVO<?> getRightRateAndClassHours(
            @ApiParam(value = "用户学号",required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid",required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "学科",required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "年级",required = true)
            @RequestParam("levelName") String levelName
    ){
        JSONArray arr=examTwoService.getRightRateAndClassHours(studentNumber,openid,subject,levelName);
        return ResultVOUtil.success(arr);
    }

    @ApiOperation(value="十九、 学习记录：上面三个数的统计")
    @GetMapping("/getPracticeRecord")
    public ResultVO<?> getPracticeRecord(
            @ApiParam(value = "用户学号",required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid",required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "学科",required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "年级",required = true)
            @RequestParam("levelName") String levelName
    ){
        JSONObject json=examTwoService.getPracticeRecord(studentNumber,openid,subject,levelName);
        return ResultVOUtil.success(json);
    }
}
