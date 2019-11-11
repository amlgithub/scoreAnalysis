package com.zgczx.controller.score;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.zgczx.VO.ResultVO;
import com.zgczx.repository.mysql1.score.dto.MonthByYearListDTO;
import com.zgczx.repository.mysql1.score.model.ManuallyEnterGrades;
import com.zgczx.service.score.ScoreService;
import com.zgczx.service.scoretwo.ScoreTwoService;
import com.zgczx.utils.ResultVOUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
     */
    @PostMapping("/saveList")
    public ResultVO<?> saveList(@RequestBody String list){
        List<ManuallyEnterGrades> enterGradesList = JSON.parseObject(list, new TypeReference<List<ManuallyEnterGrades>>() {
        });
        List<ManuallyEnterGrades> list1 = scoreTwoService.saveList(enterGradesList);

        return ResultVOUtil.success(list1);
    }
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
    @ApiOperation(value = "获取此用户录入数据的所有年份")
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
    @ApiOperation(value = "根据年份和openid获取对应的数据中的月份信息")
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
    @ApiOperation(value = "根据年份和月份获取对应的数据中的考试名称")
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
    @ApiOperation(value = "根据考试名称和openid获取对应的数据")
    @GetMapping(value = "/findAll")
    public ResultVO<?> findAll(
            @ApiParam(value = "用户openid", required = true)
            @RequestParam(value = "openid") String openid,
            @ApiParam(value = "exam_name全称",required = true)
            @RequestParam(value = "examName") String examName
    ){
        List<ManuallyEnterGrades> list = scoreTwoService.findAll(openid,examName);
        return ResultVOUtil.success(list);
    }

}
