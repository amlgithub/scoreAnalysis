package com.zgczx.controller.score;

import com.zgczx.VO.ResultVO;
import com.zgczx.dataobject.score.ManuallyEnterGrades;
import com.zgczx.service.scoretwo.ScoreTwoService;
import com.zgczx.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 承接ScoreController剩下的接口
 * 因为 ScoreController中的实现类达到了2000千行
 * @author aml
 * @date 2019/10/29 12:27
 */
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
}
