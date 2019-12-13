package com.zgczx.controller.exam;

import com.zgczx.VO.ResultVO;
import com.zgczx.repository.mysql1.exam.model.Question;
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

    @ApiOperation(value = "二、 获取此年级的所有章目")
    @GetMapping("/getAllChapter")
    public ResultVO<?> getAllChapter(
            @ApiParam(value = "levelName年级水平", required = true)
            @RequestParam("levelName") String levelName
    ){
        List<String> list = examService.getAllChapter(levelName);

        return ResultVOUtil.success(list);
    }

    @ApiOperation(value = "三、 获取此年级所有的 小节名称")
    @GetMapping("/getAllSection")
    public ResultVO<?> getAllSection(
            @ApiParam(value = "levelName年级水平", required = true)
            @RequestParam("levelName") String levelName,
            @ApiParam(value = "chapter具体章名称", required = true)
            @RequestParam("chapter") String chapter
    ){
        List<String> list = examService.getAllSection(levelName,chapter);

        return ResultVOUtil.success(list);
    }


    @ApiOperation(value = "四、 将一试卷切分为一道道题存入题库表中")
    @GetMapping("/splitExam")
    public ResultVO<?> splitExam(
            @ApiParam(value = "试卷全称", required = true)
            @RequestParam("examName") String examName,
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
            @RequestParam("examName") String examName,
            @ApiParam(value = "科目名称", required = true)
            @RequestParam("subject") String subject
    ){
        List<Question> list = examService.findExamQuestionInfo(examName,subject);

        return ResultVOUtil.success(list);
    }
}
