package com.zgczx.controller.score;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.zgczx.VO.ResultVO;
import com.zgczx.repository.mysql1.score.dto.ManuallyEnterGradesDTO;
import com.zgczx.repository.mysql1.score.dto.MonthByYearListDTO;
import com.zgczx.repository.mysql1.score.model.GoalSet;
import com.zgczx.repository.mysql1.score.model.ManuallyEnterGrades;
import com.zgczx.repository.mysql1.user.model.StudentInfo;
import com.zgczx.repository.mysql2.scoretwo.dto.CommentValueDTO;
import com.zgczx.repository.mysql2.scoretwo.dto.LocationComparisonDTO;
import com.zgczx.repository.mysql2.scoretwo.dto.SingleContrastInfoDTO;
import com.zgczx.repository.mysql2.scoretwo.dto.TotalScoreInfoDTO;
import com.zgczx.service.scoretwo.ScoreTwoService;
import com.zgczx.utils.Param;
import com.zgczx.utils.ResultVOUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 承接ScoreController剩下的接口
 * 因为 ScoreController中的实现类达到了2000千行
 * @author aml
 * @date 2019/10/29 12:27
 */
@Api(description = "第二个scoreTwo分析模块")
@RestController
@RequestMapping("/scoreTwo")
@Slf4j
public class ScoreTwoController {

    @Autowired
    ScoreTwoService scoreTwoService;

    /**
     *  插入操作
     * @param wechatOpenid openid
     * @param studentNumber 学号
     * @param subject 科目名称 或 “全科”
     * @param score 分数
     * @param classRank 班级排名
     * @param gradeRank 年级排名
     * @param examName 考试名称
     * @return ManualGradeEntry对象
     */
    @PostMapping("/save")
    public ResultVO<?> save(@RequestParam(value = "wechat_openid") String wechatOpenid,
                                            @RequestParam(value = "student_number") String studentNumber,
                                            @RequestParam(value = "subject") String subject,
                                            @RequestParam(value = "score") String score,
                                            @RequestParam(value = "class_rank") String classRank,
                                            @RequestParam(value = "grade_rank") String gradeRank,
                                            @RequestParam(value = "exam_name") String examName){
        ManuallyEnterGrades manuallyEnterGrades = scoreTwoService.saveEntity(wechatOpenid,studentNumber,subject,score,classRank,gradeRank,examName);
        return ResultVOUtil.success(manuallyEnterGrades);
    }

    /**
     * 注意： 前端用Vue直接传不了，list中包含类似json串的格式，
     * 前端只能传string，后端用fastjson.JSON处理一下，封装成list数组形式
     * @RequestBody是把整个HttpServletRequest的输入(request.getInputStream())，
     * 转换成一个对象，常用的转换是采用json方式，
     * 在spring中是RequestResponseBodyMethodProcessor利用HttpMessageConventer做的。
     * @param list
     * @return
     *
     */
    //用户手动录入成绩自己各科成绩，暂时没用，用的   "一、 录入成绩，可批量录入"
    @ApiOperation(value = "一、 录入成绩，可批量录入")
    @PostMapping("/saveList")
    public ResultVO<?> saveList(@RequestBody @ApiParam(name="用户Model对象 传参名称为 list",value="传入json格式",required=true) String list){
        List<ManuallyEnterGrades> enterGradesList = JSON.parseObject(list, new TypeReference<List<ManuallyEnterGrades>>() {
        });
        List<ManuallyEnterGrades> list1 = scoreTwoService.saveList(enterGradesList);

        return ResultVOUtil.success(list1);
    }
    //用户手动录入成绩自己各科成绩
    @ApiOperation(value = "一、 录入成绩，可批量录入")
    @PostMapping("/saveList2")
    public ResultVO<?> saveList2(HttpServletRequest request, HttpServletResponse response){
        String list = request.getParameter("0");
        List<ManuallyEnterGrades> enterGradesList = JSON.parseObject(list, new TypeReference<List<ManuallyEnterGrades>>() {
        });
        List<ManuallyEnterGrades> list1 = scoreTwoService.saveList(enterGradesList);

        return ResultVOUtil.success(list1);
    }


    /**
     * 获取此用户录入数据的所有年份
     * @param openid 用户openid
     * @return StringList 对象
     */
    @ApiOperation(value = "二、录入统计1） 获取此用户录入数据的所有年份")
    @GetMapping("/getYearList")
    public ResultVO<?> getYearList(
            @ApiParam(value = "openid", required = true)
            @RequestParam(value = "openid") String openid){

        List<String> stringList = scoreTwoService.getYearList(openid);

        return ResultVOUtil.success(stringList);
    }

    /**
     *  根据年份获取对应的数据中的月份
     * @param openid
     * @param year
     * @return
     */
    @ApiOperation(value = "三、录入统计2） 根据年份和openid获取对应的数据中的月份信息")
    @GetMapping(value = "getMonthByYearList")
    public ResultVO<?> getMonthByYearList(
            @ApiParam(value = "用户openid", required = true)
            @RequestParam(value = "openid") String openid,
            @ApiParam(value = "year", required = true)
            @RequestParam(value = "year") String year){

        List<MonthByYearListDTO> stringList = scoreTwoService.getMonthByYearList(openid,year);
        return ResultVOUtil.success(stringList);
    }

    /**
     * 根据年份和月份获取对应的数据中的考试名称，例如2018年08月月考，此接口就是获取 “月考”
     * @param openid
     * @param yearMonth
     * @return
     */
    @ApiOperation(value = "四、录入统计3） 根据年份和月份获取对应的数据中的考试名称")
    @GetMapping(value = "getExamNameByYearMonthList")
    public ResultVO<?> getExamNameByYearMonthList(
            @ApiParam(value = "用户openid", required = true)
            @RequestParam(value = "openid") String openid,
            @ApiParam(value = "年月参数名称",required = true)
            @RequestParam(value = "yearMonth") String yearMonth){

        List<String> stringList = scoreTwoService.getExamNameByYearMonthList(openid,yearMonth);
        return ResultVOUtil.success(stringList);
    }

    //获取此用户录入本次考试的所有信息
    @ApiOperation(value = "#12.9: 五、录入统计4） 根据考试名称和openid获取对应的数据")
    @GetMapping(value = "/findAll")
    public ResultVO<?> findAll(
            @ApiParam(value = "用户openid", required = true)
            @RequestParam(value = "openid") String openid,
            @ApiParam(value = "exam_name全称",required = true)
            @RequestParam(value = "examName") String examName
    ){
        List<ManuallyEnterGradesDTO> list = scoreTwoService.findAll(openid,examName);
        return ResultVOUtil.success(list);
    }


    //根据学号验证 学校是否提供数据了
    @ApiOperation(value = "六、暂时无用，验证目前从统一登录那块验证的；登录模块：根据学号验证 学校是否提供数据了；")
    @GetMapping(value = "/verifyStudentId")
    public ResultVO<?> verifyStudentCode(
            @ApiParam(value = "用户openid", required = true)
            @RequestParam(value = "openid") String openid,
            @ApiParam(value = "学生学号",required = true)
            @RequestParam(value = "verifyStudentId") String verifyStudentId
    ){
        StudentInfo list = scoreTwoService.verifyStudentCode(openid,verifyStudentId);
        return ResultVOUtil.success(list);
    }

    //定位对比二图： 和前排人的差距
    @ApiOperation(value = "七、定位对比二图： 和前排人的差距")
    @GetMapping(value = "/getGapValue")
    public ResultVO<?> getGapValue(
            @ApiParam(value = "用户openid", required = true)
            @RequestParam(value = "openid") String openid,
            @ApiParam(value = "用户学号", required = true)
            @RequestParam(value = "stuNumber") String stuNumber,
            @ApiParam(value = "exam_name全称",required = true)
            @RequestParam(value = "examName") String examName
    ){
        List<LocationComparisonDTO> list = scoreTwoService.getGapValue(openid,stuNumber,examName);
        return ResultVOUtil.success(list);
    }

    //定位对比三图一： 评语中的各个值
    @ApiOperation(value = "八、 定位对比三：图一上评语中的各个值")
    @GetMapping(value = "/getCommentValue")
    public ResultVO<?> getCommentValue(
            @ApiParam(value = "用户openid", required = true)
            @RequestParam(value = "openid") String openid,
            @ApiParam(value = "用户学号", required = true)
            @RequestParam(value = "stuNumber") String stuNumber,
            @ApiParam(value = "考试全称",required = true)
            @RequestParam(value = "examName") String examName
    ){
        List<CommentValueDTO> list = scoreTwoService.getCommentValue(openid,stuNumber,examName);
        return ResultVOUtil.success(list);
    }

    //九 定位对比： 1。 总分的 自己排名、目标排名、自己分数、目标分数、差值
    @ApiOperation(value = "（定位对比从这个开始）九 定位对比： 1。 总分的 自己排名、目标排名、自己分数、目标分数、差值 ")
    @GetMapping(value = "/getTotalScoreInfo")
    public ResultVO<?> getTotalScoreInfo(
            @ApiParam(value = "用户openid", required = true)
            @RequestParam(value = "openid") String openid,
            @ApiParam(value = "用户学号", required = true)
            @RequestParam(value = "stuNumber") String stuNumber,
            @ApiParam(value = "考试全称",required = true)
            @RequestParam(value = "examName") String examName,
            @ApiParam(value = "总分的目标排名",required = true)
            @RequestParam(value = "targetRank") String targetRank
    ){
        List<TotalScoreInfoDTO> list = scoreTwoService.getTotalScoreInfo(openid,stuNumber,examName,targetRank);
        return ResultVOUtil.success(list);
    }

    //十、定位对比： 2。 此用户所选的科目集合
    @ApiOperation(value = "十、(无用) 定位对比： 此用户所选的科目集合 ")
    @GetMapping(value = "/getSubjectCollection")
    public ResultVO<?> getSubjectCollection(
            @ApiParam(value = "用户openid", required = true)
            @RequestParam(value = "openid") String openid,
            @ApiParam(value = "用户学号", required = true)
            @RequestParam(value = "stuNumber") String stuNumber,
            @ApiParam(value = "考试全称",required = true)
            @RequestParam(value = "examName") String examName
    ){
        List<String> list = scoreTwoService.getSubjectCollection(openid,stuNumber,examName);
        return ResultVOUtil.success(list);
    }

    //十一、定位对比：1. 单科的 自己排名、目标排名、自己分数、目标分数、差值
    @ApiOperation(value = "十一、定位对比：2. 单科的 自己排名、目标排名、自己分数、目标分数、差值")
    @GetMapping("/getSingleContrastInfo")
    public ResultVO<?> getSingleContrastInfo(
//            @ApiParam(value = "用户openid", required = true)
//            @RequestParam(value = "openid") String openid,
//            @ApiParam(value = "用户学号", required = true)
//            @RequestParam(value = "stuNumber") String stuNumber,
//            @ApiParam(value = "考试全称",required = true)
//            @RequestParam(value = "examName") String examName,
            HttpServletRequest request, HttpServletResponse response){

        Map<String, Object> param = Param.getParam(request);
        //JSONObject singleContrastInfo = scoreTwoService.getSingleContrastInfo(param);
        List<SingleContrastInfoDTO> singleContrastInfo = scoreTwoService.getSingleContrastInfo(param);

//        // 取出json串
//        String list = request.getParameter("0");
////        List<SingleContrastInfoDTO> singleContrastInfo = scoreTwoService.getSingleContrastInfo(openid, stuNumber, examName, list);
//
//        // json串处理；暂无用放到，impl
//        List<ManuallyEnterGrades> enterGradesList = JSON.parseObject(list, new TypeReference<List<ManuallyEnterGrades>>() {
//        });
//        List<ManuallyEnterGrades> list1 = scoreTwoService.saveList(enterGradesList);

        return ResultVOUtil.success(singleContrastInfo);
    }

    //定位中查询之前各科设定的目标值
    @ApiOperation(value = "十二、 定位中查询之前各科设定的目标值")
    @GetMapping(value = "/findTargetValue")
    public ResultVO<?> findTargetValue(
            @ApiParam(value = "用户学号", required = true)
            @RequestParam(value = "stuNumber") String stuNumber,
            @ApiParam(value = "考试全称",required = true)
            @RequestParam(value = "examName") String examName
    ){
        GoalSet list = scoreTwoService.findTargetValue(stuNumber,examName);
        return ResultVOUtil.success(list);
    }


    @ApiOperation(value = "#12.9: 十三、 录入统计中的 删除 功能")
    @GetMapping(value = "/deleteManuallyEnter")
    public ResultVO<?> deleteManuallyEnter(
            @ApiParam(value = "用户学号", required = true)
            @RequestParam(value = "stuNumber") String stuNumber,
            @ApiParam(value = "用户openid", required = true)
            @RequestParam(value = "openid") String openid,
            @ApiParam(value = "考试全称",required = true)
            @RequestParam(value = "examName") String examName
    ){
        ManuallyEnterGrades list = scoreTwoService.deleteManuallyEnter(stuNumber,openid,examName);
        return ResultVOUtil.success(list);
    }

    @ApiOperation(value = "#12.9: 十四、 录入统计中的 更新 功能")
    @PostMapping(value = "/updateManuallyEnter")
    public ResultVO<?> updateManuallyEnter(
            @ApiParam(value = "用户学号", required = true)
            @RequestParam(value = "stuNumber") String stuNumber,
            @ApiParam(value = "用户openid", required = true)
            @RequestParam(value = "openid") String openid,
            @ApiParam(value = "考试全称",required = true)
            @RequestParam(value = "oldexamName") String oldexamName,
            ManuallyEnterGrades manuallyEnterGrades
    ){
        ManuallyEnterGrades list = scoreTwoService.updateManuallyEnter(stuNumber,openid,oldexamName,manuallyEnterGrades);
        return ResultVOUtil.success(list);
    }
}
