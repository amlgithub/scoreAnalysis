package com.zgczx.controller.exam;

import com.zgczx.VO.ResultVO;
import com.zgczx.repository.mysql1.exam.dto.DoQuestionInfoDTO;
import com.zgczx.repository.mysql1.exam.dto.EchoDoQuestionDTO;
import com.zgczx.repository.mysql1.exam.dto.QuestionDTO;
import com.zgczx.repository.mysql1.exam.model.Question;
import com.zgczx.repository.mysql1.exam.model.UserCollect;
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

    @ApiOperation(value = "五、 根据科目和考试名称返回所有题的信息数据")
    @GetMapping("/findExamQuestionInfo")
    public ResultVO<?> findExamQuestionInfo(
            @ApiParam(value = "试卷全称", required = true)
            @RequestParam("paperName") String examName,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid", required = true)
            @RequestParam("openid") String openid
    ){
        List<QuestionDTO> list = examService.findExamQuestionInfo(examName,subject,studentNumber,openid);

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
            @RequestParam("sourcePaperId") int sourcePaperId
    ){
        DoQuestionInfoDTO list = examService.judgeQuestionRight(id,studentNumber,openid,commitString,examName,subject,sourcePaperId);

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

    @ApiOperation(value = "十、取消收藏  ")
    @GetMapping("/cancelCollect")
    public ResultVO<?> cancelCollect(
            @ApiParam(value = "哪道题：题库表的主键id", required = true)
            @RequestParam("id") int id,
            @ApiParam(value = "用户学号", required = true)
            @RequestParam("studentNumber") String studentNumber,
            @ApiParam(value = "用户openid", required = true)
            @RequestParam("openid") String openid,
            @ApiParam(value = "试卷名称", required = true)
            @RequestParam("paperName") String examName,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject,
            @ApiParam(value = "取消收藏传值为 2", required = true)
            @RequestParam("cancel") int cancel
    ){

        UserCollect list = examService.cancelCollect(id,studentNumber,openid,examName,subject,cancel);

        return ResultVOUtil.success(list);
    }

}
