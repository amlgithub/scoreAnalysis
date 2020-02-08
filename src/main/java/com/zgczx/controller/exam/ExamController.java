package com.zgczx.controller.exam;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zgczx.VO.ResultVO;
import com.zgczx.repository.mysql1.exam.dto.*;
import com.zgczx.repository.mysql1.exam.model.Question;
import com.zgczx.repository.mysql1.exam.model.UserCollect;
import com.zgczx.repository.mysql1.exam.model.UserPaperRecord;
import com.zgczx.repository.mysql1.exam.model.UserQuestionRecord;
import com.zgczx.service.exam.ExamService;
import com.zgczx.utils.ResultVOUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 在线题库第一个controller
 * @author aml
 * @date 2019/12/11 15:40
 */

@Api(description = "第一个exam模块")
@RestController
@RequestMapping("/exam")
@Slf4j
public class ExamController {

    @Autowired
    private ExamService examService;

    @ApiOperation(value = "一、 读取Word中的内容并生成json串处理")
    @PostMapping("/parseWord")
    public ResultVO<?> parseWord(
            @ApiParam(value = "file文件", required = true)
            @RequestParam("filename")MultipartFile file,
            HttpSession session, HttpServletRequest request){

        String text = examService.parseWord(file,session,request);

        return ResultVOUtil.success(text);
    }

    @ApiOperation(value = "二、 获取此科目此年级的所有章目")
    @GetMapping("/getAllChapter")
    public ResultVO<?> getAllChapter(
            @ApiParam(value = "levelName年级水平", required = true)
            @RequestParam("levelName") String levelName,
            @ApiParam(value = "subject科目名称", required = true)
            @RequestParam("subject") String subject
    ){
        List<String> list = examService.getAllChapter(levelName,subject);

        return ResultVOUtil.success(list);
    }

    @ApiOperation(value = "三、 获取此科目此年级所有的 小节名称")
    @GetMapping("/getAllSection")
    public ResultVO<?> getAllSection(
            @ApiParam(value = "levelName年级水平", required = true)
            @RequestParam("levelName") String levelName,
            @ApiParam(value = "chapter具体章名称", required = true)
            @RequestParam("chapter") String chapter,
            @ApiParam(value = "subject科目名称", required = true)
            @RequestParam("subject") String subject
    ){
        List<String> list = examService.getAllSection(levelName,chapter,subject);

        return ResultVOUtil.success(list);
    }


    @ApiOperation(value = "四、 将一试卷切分为一道道题存入题库表中")
    @GetMapping("/splitExam")
    public ResultVO<?> splitExam(
            @ApiParam(value = "试卷全称", required = true)
            @RequestParam("paperName") String examName,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject
    ){
        List<Question> list = examService.splitExam(examName,subject);

        return ResultVOUtil.success(list);
    }


    @ApiOperation(value = "# 2.8 模拟考试、章节练习 + 年级，五、 根据科目和考试名称返回所有题的信息数据")
    @GetMapping("/findExamQuestionInfo")
    public ResultVO<?> findExamQuestionInfo(
            @ApiParam(value = "试卷全称", required = true)
            @RequestParam("paperName") String examName,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid", required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "年级",required = true)
            @RequestParam("gradeLevel") String gradeLevel
    ){
        List<QuestionDTO> list = examService.findExamQuestionInfo(examName,subject,studentNumber,openid,gradeLevel);

        return ResultVOUtil.success(list);
    }

    @ApiOperation(value = "六、【属于做一道题后，做题记录跟着变化】动态实时呈现用户做题详情 并记录用户所有的做题情况")
    @GetMapping("/doQuestionInfo")
    public ResultVO<?> doQuestionInfo(
            @ApiParam(value = "哪道题：题库表的主键id", required = true)
            @RequestParam("id") int id,
            @ApiParam(value = "studentNumber用户学号", required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "commitString一道题提交的内容", required = true)
            @RequestParam("commitString") String commitString,
            @ApiParam(value = "试卷全称", required = true)
            @RequestParam("paperName") String examName,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "试卷id", required = true)
            @RequestParam("sourcePaperId") int sourcePaperId,
            @ApiParam(value = "年级",required = true)
            @RequestParam("gradeLevel") String gradeLevel,
            @ApiParam(value = "做题时间",required = true)
            @RequestParam("doTime") String doTime
    ){
        DoQuestionInfoDTO list = examService.judgeQuestionRight(id,studentNumber,openid,commitString,examName,subject,sourcePaperId,gradeLevel,doTime);

        return ResultVOUtil.success(list);
    }

    @ApiOperation(value = "七、记录用户的收藏情况 ")
    @GetMapping("/insertCollect")
    public ResultVO<?> insertCollect(
            @ApiParam(value = "哪道题：题库表的主键id", required = true)
            @RequestParam("id") int id,
            @ApiParam(value = "studentNumber用户学号", required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "分类名称", required = true)
            @RequestParam("classification") String classification
//            @ApiParam(value = "这道题提交的内容", required = true)
//            @RequestParam("commitString") String commitString
//            @ApiParam(value = "试卷全称", required = true)
//            @RequestParam("paperName") String examName,
//            @ApiParam(value = "科目名称", required = true)
//            @RequestParam("subject") String subject
    ){
        //UserCollect list = examService.insertCollect(id,studentNumber,openid,classification,commitString);//,examName,subject
        UserCollect list = examService.insertCollect(id,studentNumber,openid,classification);
        return ResultVOUtil.success(list);
    }

    @ApiOperation(value = "八、【不做题时查看】用户做题详情；需求：点击类似做题页面的键盘，出现的正确、错误、未做数量  ")
    @GetMapping("/getDoQuestionInfo")
    public ResultVO<?> getDoQuestionInfo(
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("paperName") String examName,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "试卷id", required = true)
            @RequestParam("sourcePaperId") int sourcePaperId
    ){

        DoQuestionInfoDTO list = examService.getDoQuestionInfo(studentNumber,examName,subject,sourcePaperId);

        return ResultVOUtil.success(list);
    }

    @ApiOperation(value = "九、回显这个用户最近做的章节练习情况  ")
    @GetMapping("/echoDoQuestionInfo")
    public ResultVO<?> echoDoQuestionInfo(
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "试卷名称", required = true)
            @RequestParam("paperName") String examName,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject
    ){

        List<EchoDoQuestionDTO> list = examService.echoDoQuestionInfo(studentNumber,examName,subject);

        return ResultVOUtil.success(list);
    }

    @ApiOperation(value = "#2.1 兼容错题本中没有试卷名称情况：十、取消收藏  ")
    @GetMapping("/cancelCollect")
    public ResultVO<?> cancelCollect(
            @ApiParam(value = "哪道题：题库表的主键id", required = true)
            @RequestParam("id") int id,
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid", required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "试卷名称", required = false)
            @RequestParam("paperName") String examName,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "取消收藏传值为 2", required = true)
            @RequestParam("cancel") int cancel
    ){

        UserCollect list = examService.cancelCollect(id,studentNumber,openid,examName,subject,cancel);

        return ResultVOUtil.success(list);
    }


    @ApiOperation(value = "十一、 用户整套试卷（一节题）的记录")
    @PostMapping("/fullPaperRecord")
    public ResultVO<?> fullPaperRecord(
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid", required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "试卷名称", required = true)
            @RequestParam("paperName") String examName,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "此试卷的所有内容（为了记录此时每道题的选项内容）", required = true)
            @RequestParam("examPaperContent") String examPaperContent,
            @ApiParam(value = "此时卷用户的答题信息", required = true)
            @RequestParam("examPaperAnwer") String examPaperAnwer
    ){

        UserPaperRecord userPaperRecord = examService.fullPaperRecord(studentNumber,openid,examName,subject,examPaperContent,examPaperAnwer);
        return ResultVOUtil.success(userPaperRecord);
    }

    @ApiOperation(value = "十二、点击练习错题(考试错题)时，展现已掌握和未掌握的章名称和对应错题数(已掌握和未掌握的考试错题数)  ")
    @GetMapping("/getChapterErrNumber")
    public ResultVO<?> getChapterErrNumber(
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid", required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "试卷类型 1:练习；2：考试", required = true)
            @RequestParam("examCategory") String examCategory
    ){

        JSONObject list = examService.getChapterErrNumber(studentNumber,openid,subject, examCategory);

        return ResultVOUtil.success(list);
    }

    @ApiOperation(value = "十三、将用户最近做的此试卷信息回显给用户  ")
    @GetMapping("/echoPaperInfo")
    public ResultVO<?> echoPaperInfo(
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid", required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "试卷名称", required = true)
            @RequestParam("paperName") String examName
    ){

        List<EchoPaperCompleteDTO> list = examService.echoPaperInfo(studentNumber,openid,subject,examName);

        return ResultVOUtil.success(list);
    }

    @ApiOperation(value = "十四、查询某个用户是否收藏过某道题  ")
    @GetMapping("findCollectInfo")
    public ResultVO<?> findCollectInfo(
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid", required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "学科", required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "哪道题：题库表的主键id", required = true)
            @RequestParam("question_id") int questionId
    ){
        FindCollectDTO list = examService.findCollectInfo(studentNumber,subject,questionId);
        return ResultVOUtil.success(list);
    }

    @ApiOperation(value = "十五、获取此章下面的所有节的名称和对应的错题数量  ")
    @GetMapping("/getSectionErrNumber")
    public ResultVO<?> getSectionErrNumber(
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid", required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "章的名称", required = true)
            @RequestParam("chapterName") String chapterName,
            @ApiParam(value = "是否已掌握",required = true)
            @RequestParam("ifMastered") String ifMastered
    ){

        SectionErrNumberDTO list = examService.getSectionErrNumber(studentNumber,openid,subject,chapterName, ifMastered);

        return ResultVOUtil.success(list);
    }

    @ApiOperation(value = "# 1.14 十六、错题本：获取某类别所有未掌握（已经掌握）题的所有情况  ")
    @GetMapping("/getNotMasteredInfo")
    public ResultVO<?> getNotMasteredInfo(
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid", required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "分类：章节练习", required = true)
            @RequestParam("examCategory") String examCategory,
            @ApiParam(value = "年级",required = true)
            @RequestParam("gradeLevel") String gradeLevel,
            @ApiParam(value = "掌握还是未掌握：1为掌握；2为未掌握",required = true)
            @RequestParam("master") int master
    ){

        JSONObject jsonArray = examService.getNotMasteredInfo(studentNumber,openid,subject,examCategory,gradeLevel,master);

        return ResultVOUtil.success(jsonArray);
    }

    @ApiOperation(value = "# 1.15 十七、错题本中的 下面的分类详情  ")
    @GetMapping("/getClassification")
    public ResultVO<?> getClassification(
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid", required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "分类：章节练习", required = true)
            @RequestParam("examCategory") String examCategory,
            @ApiParam(value = "年级",required = true)
            @RequestParam("gradeLevel") String gradeLevel,
            @ApiParam(value = "掌握还是未掌握：1为掌握；2为未掌握",required = true)
            @RequestParam("master") int master
    ){
        JSONObject jsonArray = examService.getClassification(studentNumber,openid,subject,examCategory,gradeLevel,master);
        return ResultVOUtil.success(jsonArray);
    }


    @ApiOperation(value = "# 1.14 十八、错题本：统计分类中 未掌握或已掌握的 各分类的数量  ")
    @GetMapping("/getClassificationQuantity")
    public ResultVO<?> getClassificationQuantity(
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid", required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "年级",required = true)
            @RequestParam("gradeLevel") String gradeLevel,
            @ApiParam(value = "掌握还是未掌握：1为掌握；2为未掌握",required = true)
            @RequestParam("master") int master
    ){

        JSONObject jsonArray = examService.getClassificationQuantity(studentNumber,openid,subject,gradeLevel,master);

        return ResultVOUtil.success(jsonArray);
    }

    @ApiOperation(value = "# 1. 31十九、错题本：根据题库id获取此题的所有信息  ")
    @GetMapping("/getQuestionInfo")
    public ResultVO<?> getQuestionInfo(
            @ApiParam(value = "题库主键id", required = true)
            @RequestParam("id") int id,
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid", required = true)
            @RequestParam("openid") String openid
//            @ApiParam(value = "科目名称", required = true)
//            @RequestParam("subject") String subject,
//            @ApiParam(value = "年级",required = true)
//            @RequestParam("gradeLevel") String gradeLevel
    ){

        JSONObject jsonArray = examService.getQuestionInfo(id,studentNumber,openid);

        return ResultVOUtil.success(jsonArray);
    }

    @ApiOperation(value = "# 2.3 二十、专项练习：获取此年级、科目的所有知识点  ")
    @GetMapping("/getAllKnowledge")
    public ResultVO<?> getAllKnowledge(
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid", required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "年级",required = true)
            @RequestParam("gradeLevel") String gradeLevel
    ){

        JSONObject jsonArray = examService.getAllKnowledge(studentNumber,openid,subject,gradeLevel);

        return ResultVOUtil.success(jsonArray);
    }

    @ApiOperation(value = "# 2.3 二十一、专项练习：根据知识点获取所有相关的题  ")
    @GetMapping("/getAllQuestionByPoint")
    public ResultVO<?> getAllQuestionByPoint(
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid", required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "年级",required = true)
            @RequestParam("gradeLevel") String gradeLevel,
            @ApiParam(value = "知识点", required = true)
            @RequestParam("knowledgePoint") String knowledgePoint
    ){

        JSONArray jsonArray = examService.getAllQuestionByPoint(studentNumber,openid,subject,gradeLevel,knowledgePoint);

        return ResultVOUtil.success(jsonArray);
    }

    @ApiOperation(value = "# 2.4 二十二、专项练习：记录用户此题到 用户记录表中 ")
    @PostMapping("/specialRecordId")
    public ResultVO<?> specialRecordId(
            @ApiParam(value = "哪道题：题库表的主键id", required = true)
            @RequestParam("id") int id,
            @ApiParam(value = "studentNumber用户学号", required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "commitString一道题提交的内容", required = true)
            @RequestParam("commitString") String commitString,
            @ApiParam(value = "专项练习", required = true)
            @RequestParam("examCategory") String examCategory,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject,
//            @ApiParam(value = "试卷id", required = true)
//            @RequestParam("sourcePaperId") int sourcePaperId,
            @ApiParam(value = "年级",required = true)
            @RequestParam("gradeLevel") String gradeLevel,
            @ApiParam(value = "做题时间",required = true)
            @RequestParam("doTime") String doTime
    ){
        JSONObject list = examService.specialRecordId(id,studentNumber,openid,commitString,examCategory,subject,gradeLevel,doTime);

        return ResultVOUtil.success(list);
    }

    @ApiOperation(value = "# 2.7 二十三、模拟考试：获取此年级、科目的分類的各个考试名称和题数  ")
    @GetMapping("/getAllExamName")
    public ResultVO<?> getAllExamName(
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid", required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "年级",required = true)
            @RequestParam("gradeLevel") String gradeLevel
    ){

        JSONObject jsonArray = examService.getAllExamName(studentNumber,openid,subject,gradeLevel);

        return ResultVOUtil.success(jsonArray);
    }



}
