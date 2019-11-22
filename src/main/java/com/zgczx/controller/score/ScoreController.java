package com.zgczx.controller.score;

import com.zgczx.VO.ResultVO;
import com.zgczx.repository.mysql1.score.dto.*;
import com.zgczx.repository.mysql2.scoretwo.dto.ExamCoversionTotalDTO;
import com.zgczx.repository.mysql2.scoretwo.dto.ExamCoversionTotalSectionDTO;
import com.zgczx.repository.mysql2.scoretwo.dto.ExamCoversionTotalSingleDTO;
import com.zgczx.repository.mysql2.scoretwo.model.ExamCoversionTotal;
import com.zgczx.repository.mysql1.score.model.ExamInfo;
import com.zgczx.service.score.ScoreService;
import com.zgczx.utils.ResultVOUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author aml
 * @date 2019/9/10 14:44
 * 成绩相关的路由皆由此转发
 */
@Api(description = "第一个score分析模块")
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
    @ApiOperation(value = "一、 获取学生的所有成绩，根据学号id和考试名称")
    @GetMapping("/getExamCoversionTotal")
    public ResultVO<?> getExamCoversionTotal(@RequestParam(value = "userId") Integer userId,
                                             @RequestParam(value = "examType") String examType){

        ExamCoversionTotal examCoversionTotal = scoreService.getExamCoversionTotal(userId, examType);

        return ResultVOUtil.success(examCoversionTotal);

    }
    @ApiOperation(value = "二、 查询所有的考试名称")
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
    @ApiOperation(value = "三、 旭日图中的中心，主要是对年级和班级的进退名次的实现")
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
    @ApiOperation(value = "四、旭日图第三层，获取各单科的班排、年排、进退名次，班级年级人数,总分标准，各单科满分标准")
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
//    @CrossOrigin(origins = "*", maxAge = 3600)
    @ApiOperation(value = "五、旭日图的第二层， 获取三科和综合（6选3）的分值，三科班排、年排；综合班排、年排")
    @GetMapping(value = "/getExamCoversionTotalSectionInfo")
    public ResultVO<?> getExamCoversionTotalSectionInfo(@RequestParam(value = "stuNumber") String stuNumber,
                                                        @RequestParam(value = "examType") String examType){
        List<ExamCoversionTotalSectionDTO> examCoversionTotalSectionInfo = scoreService.getExamCoversionTotalSectionInfo(stuNumber, examType);

        return ResultVOUtil.success(examCoversionTotalSectionInfo);
    }


    /**
     * 作为定位对比一图：
     * 获取六率信息，高分率、优秀率、良好率、及格率、低分率、超均率，当前学生所处率值
     * @param stuNumber 学生学号
     * @param examType 某次考试
     * @return 返回具体的DTO
     */
    @ApiOperation(value = "六、定位对比，获取此用户所在班级的高分、低分、良好、及格、低分人数和自己所处位置")
    @GetMapping(value = "/getSixRateInfo")
    public ResultVO<?> getSixRateInfo(
            @ApiParam(value = "用户学号：stuNumber", required = true)
            @RequestParam(value = "stuNumber") String stuNumber,
            @ApiParam(value = "考试名称：examType",  required = true)
            @RequestParam(value = "examType") String examType){
        List<SixRateDTO> sixRateInfo = scoreService.getSixRateInfo(stuNumber, examType);
        return ResultVOUtil.success(sixRateInfo);
    }


    /**
     *  学科分析,各学科贡献率和各学科均衡差值，都是和年级值对比
     * @param stuNumber 学生学号
     * @param examType 具体的考试名称
     * @return 返回DTO对象
     */
    @ApiOperation(value = "七、学科分析,各学科贡献率和各学科均衡差值，都是和年级值对比")
    @GetMapping(value = "/getSubjectAnalysisInfo")
    public ResultVO<?> getSubjectAnalysisInfo(@RequestParam(value = "stuNumber") String stuNumber,
                                              @RequestParam(value = "examType") String examType){
        List<SubjectAnalysisDTO> subjectAnalysisInfo = scoreService.getSubjectAnalysisInfo(stuNumber, examType);

        return ResultVOUtil.success(subjectAnalysisInfo);
    }

    /**
     *  历史分析中的-01 ：总分分析，包含总分、班排、年排、班均分
     *  年均分， 以上五个值的标准化（百分率）
     * @param stuNumber 学生学号
     * @param examType 具体的考试名称
     * @return 返回DTO对象
     */
    @ApiOperation(value = "八、历史分析中的-01 ：总分分析，包含总分、班排、年排、班均分，年均分， 以上五个值的标准化（百分率）")
    @GetMapping(value = "/getHistoricalAnalysisTotalInfo")
    public ResultVO<?> getHistoricalAnalysisTotalInfo(@RequestParam(value = "stuNumber") String stuNumber,
                                                      @RequestParam(value = "examType") String examType,
                                                      @RequestParam(value = "openid") String openid){
        List<HistoricalAnalysisTotalDTO> historicalAnalysisTotalInfo = scoreService.getHistoricalAnalysisTotalInfo(stuNumber,examType,openid);

        return ResultVOUtil.success(historicalAnalysisTotalInfo);
    }

    /**
     * 历史分析中的-02 ：单科分数分析，包含单科分值、班排、年排、班均分
     *   年均分， 以上五个值的标准化（百分率）
     * @param stuNumber 学号
     * @param examType 考试名称
     * @param subject 具体科目
     * @return 返回dto对象
     */
    @ApiOperation(value = "九、历史分析中的-02 ：单科分数分析，包含单科分值、班排、年排、班均分，年均分， 以上五个值的标准化（百分率）")
    @GetMapping(value = "/getHistoricalAnalysisSingleInfo")
    public ResultVO<?> getHistoricalAnalysisSingleInfo(@RequestParam(value = "stuNumber") String stuNumber,
                                                       @RequestParam(value = "examType") String examType,
                                                       @RequestParam(value = "subject") String subject,
                                                       @RequestParam(value = "openid") String openid){
        List<HistoricalAnalysisSingleDTO> historicalAnalysisSingleInfo = scoreService.getHistoricalAnalysisSingleInfo(stuNumber, examType, subject,openid);

        return ResultVOUtil.success(historicalAnalysisSingleInfo);
    }


    /**
     *  旭日图中总分、三科、综合、各单科的率值
     *  率值 = 所得分 / 各标准总分
     * @param stuNumber 学号
     * @param examType 考试名称
     * @return 返回DTO对象
     */
    @ApiOperation(value = "十、旭日图中总分、三科、综合、各单科的率值；率值 = 所得分 / 各标准总分")
    @GetMapping(value = "/getAsahiChartAllRate")
    public ResultVO<?> getAsahiChartAllRate(@RequestParam(value = "stuNumber") String stuNumber,
                                            @RequestParam(value = "examType") String examType){
        List<AsahiChartAllRateDTO> asahiChartAllRate = scoreService.getAsahiChartAllRate(stuNumber, examType);

        return ResultVOUtil.success(asahiChartAllRate);
    }


    /**
     *  成绩单接口：获取总分的班排、年排，各具体单科的分数、班排、年排
     * @param stuNumber 学号
     * @param examType 考试名称
     * @return DTO对象
     */
    @ApiOperation(value = "十一、成绩单接口：获取总分的班排、年排，各具体单科的分数、班排、年排")
    @GetMapping(value = "/getScoreReportInfo")
    public ResultVO<?> getScoreReportInfo(@RequestParam(value = "stuNumber") String stuNumber,
                                          @RequestParam(value = "examType") String examType){
        List<ScoreReportDTO> scoreReportDTOList = scoreService.getScoreReport(stuNumber, examType);

        return ResultVOUtil.success(scoreReportDTOList);
    }


}
