package com.zgczx.controller.score;

import com.zgczx.VO.ResultVO;
import com.zgczx.dataobject.score.ExamCoversionTotal;
import com.zgczx.dataobject.score.ExamInfo;
import com.zgczx.dto.ExamCoversionTotalDTO;
import com.zgczx.dto.ExamCoversionTotalSectionDTO;
import com.zgczx.dto.ExamCoversionTotalSingleDTO;
import com.zgczx.dto.SixRateDTO;
import com.zgczx.service.score.ScoreService;
import com.zgczx.utils.ResultVOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author aml
 * @date 2019/9/10 14:44
 * 成绩相关的路由皆由此转发
 */
@RestController
@RequestMapping("/score")
//@CrossOrigin(origins = "*", maxAge = 3600)
public class ScoreController {

    private static final Logger logger = LoggerFactory.getLogger(ScoreController.class);

    @Autowired
    ScoreService scoreService;

    /**
     * 获取学生的所有成绩
     * @param userId 学号id
     * @param examType 具体哪次考试
     * @return ResultVOUtil中的code、msg、data
     */
    @GetMapping("/getExamCoversionTotal")
    public ResultVO<?> getExamCoversionTotal(@RequestParam(value = "userId") Integer userId,
                                             @RequestParam(value = "examType") String examType){

        ExamCoversionTotal examCoversionTotal = scoreService.getExamCoversionTotal(userId, examType);

        return ResultVOUtil.success(examCoversionTotal);

    }

    @GetMapping(value = "/getListExamInfols", name = "查询所有的考试名称")
    public ResultVO<?>  getListExamInfols(){

        List<ExamInfo> listExamInfols = scoreService.getListExamInfols();

        return ResultVOUtil.success(listExamInfols);
    }

    /**
     *  旭日图中的中心，主要是对年级和班级的进退名次的实现
     * @param stuNumber 学生学号
     * @param examType 具体某次考试名称
     * @return VO类中的data数据返回
     */
    @GetMapping(value = "/getExamCoversionTotalInfo")
    public ResultVO<?> getExamCoversionTotalInfo(@RequestParam(value = "stuNumber") String stuNumber,
                                                 @RequestParam(value = "examType") String examType ){
        List<ExamCoversionTotalDTO> examCoversionTotalInfo = scoreService.getExamCoversionTotalInfo(stuNumber, examType);

        return ResultVOUtil.success(examCoversionTotalInfo);
    }

    /**
     * 旭日图第三层，获取各单科的班排、年排、进退名次，班级年级人数
     * 总分标准，各单科满分标准
     * @param stuNumber 学生学号
     * @param examType 具体某次考试名称
     * @return VO类中的data数据返回
     */
    @GetMapping(value = "/getExamCoversionTotalSingleInfo")
    public ResultVO<?> getExamCoversionTotalSingleInfo(@RequestParam(value = "stuNumber") String stuNumber,
                                                       @RequestParam(value = "examType") String examType,
                                                       @RequestParam(value = "subject") String subject){
        List<ExamCoversionTotalSingleDTO> examCoversionTotalSingleInfo = scoreService.getExamCoversionTotalSingleInfo(stuNumber, examType, subject);

        return ResultVOUtil.success(examCoversionTotalSingleInfo);
    }

    /**
     *  旭日图的第二层， 获取三科和综合（6选3）的分值，
     *  三科班排、年排；综合班排、年排
     * @param stuNumber 学生学号
     * @param examType 某次考试
     * @return 返回DTO
     */
    @GetMapping(value = "/getExamCoversionTotalSectionInfo")
    public ResultVO<?> getExamCoversionTotalSectionInfo(@RequestParam(value = "stuNumber") String stuNumber,
                                                        @RequestParam(value = "examType") String examType){
        List<ExamCoversionTotalSectionDTO> examCoversionTotalSectionInfo = scoreService.getExamCoversionTotalSectionInfo(stuNumber, examType);

        return ResultVOUtil.success(examCoversionTotalSectionInfo);
    }


    /**
     * 获取六率信息，高分率、优秀率、良好率、及格率、低分率、超均率，当前学生所处率值
     * @param stuNumber 学生学号
     * @param examType 某次考试
     * @return 返回具体的DTO
     */
    @GetMapping(value = "/getSixRateInfo")
    public ResultVO<?> getSixRateInfo(@RequestParam(value = "stuNumber") String stuNumber,
                                      @RequestParam(value = "examType") String examType){
        List<SixRateDTO> sixRateInfo = scoreService.getSixRateInfo(stuNumber, examType);
        return ResultVOUtil.success(sixRateInfo);
    }

}
