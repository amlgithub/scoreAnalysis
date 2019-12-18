//package com.zgczx.utils;
//
//import com.zgczx.enums.ResultEnum;
//import com.zgczx.exception.ScoreException;
//import com.zgczx.repository.mysql1.exam.dao.ExamPaperDao;
//import com.zgczx.repository.mysql1.exam.dao.UserQuestionRecordDao;
//import com.zgczx.repository.mysql1.exam.dto.DoQuestionInfoDTO;
//import com.zgczx.repository.mysql1.exam.model.ExamPaper;
//import com.zgczx.repository.mysql1.exam.model.UserQuestionRecord;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.zgczx.utils.FilterStringUtil.filterMiddleBrackets;
//
///**
// * 将 调用的 dao的东西放到这里，报错： 反射调用错误，所以不能放此位置
// * 将六、动态实时呈现用户做题详情 并记录用户所有的做题情况 接口中
// * 获取 做题情况抽出来，作为一个 公共的函数
// * @author aml
// * @date 2019/12/18 15:02
// */
//@Slf4j
//@RestController
//public class DoQuestionInfoDTOUtil {
//
//    @Autowired
//    private  ExamPaperDao examPaperDao;
//
//    @Autowired
//    private  UserQuestionRecordDao userQuestionRecordDao;
//
//    private  String info;
//
//    public DoQuestionInfoDTO getDto(String studentNumber,String examName, String subject) {
//        ExamPaper examPaper = examPaperDao.getBy(examName, subject, 1);
//        if (examPaper == null) {
//            info = "暂时没有此科目的此试卷";
//            log.error("【错误信息】: {}", info);
//            throw new ScoreException(ResultEnum.RESULE_DATA_NONE, info);
//        }
//        // 去除[] 和 空格，或者从插库时处理，直接就存1,2,3... ；而不是存成[1, 2, 3...]
//        String[] questionList = filterMiddleBrackets(examPaper.getQuestionList()).split(",");
//
//        List<Integer> idList = new ArrayList<>();
//        for (int i = 0; i < questionList.length; i++) {
//            int integer = Integer.parseInt(questionList[i]);
//            idList.add(integer);
//        }
//        // 这一份试卷的 题的数量
//        int questionCount = idList.size();
//        //  获取某学生->某科目 -> 某试卷的所有做题记录；
//        List<UserQuestionRecord> stulist = userQuestionRecordDao.getByStudentNumberAndSubjectAndExamPaperId(studentNumber, subject, examPaper.getId());
//        int doRight = 0;
//        int doError = 0;
//        List<Integer> doRightList = new ArrayList<>(); // 做对的题号
//        List<Integer> doErrorList = new ArrayList<>(); // 做错的题号
//        List<Integer> notDoList = new ArrayList<>();
//        for (UserQuestionRecord questionRecord : stulist) {
//            if (questionRecord.getDoRight() == 1) {
//                if (!doRightList.contains(questionRecord.getQuestionId())) {
//                    doRightList.add(questionRecord.getQuestionId());
//                }
//                doRight++;
//            } else {
//                if (!doErrorList.contains(questionRecord.getQuestionId())) {
//                    doErrorList.add(questionRecord.getQuestionId());
//                }
//                doError++;
//            }
//        }
//        int notDo = questionCount - doRight - doError;
//        for (int i = 1; i <= questionCount; i++) {
//            if (!doRightList.contains(i) && !doErrorList.contains(i)) {
//                notDoList.add(i);
//            }
//        }
//        log.info("【总共做题数量：】{}", questionCount);
//        log.info("【作对题的数量：】{}", doRight);
//        log.info("【作错题的数量：】{}", doError);
//        log.info("【未做题的数量：】{}", notDo);
//
//        //List<DoQuestionInfoDTO> dtoList = new ArrayList<>();
//        DoQuestionInfoDTO dto = new DoQuestionInfoDTO();
//        dto.setQuestionCount(questionCount);
//        dto.setDoRight(doRight);
//        dto.setDoError(doError);
//        dto.setNotDo(notDo);
//        dto.setDoRightList(doRightList);
//        dto.setDoErrorList(doErrorList);
//        dto.setNotDoList(notDoList);
//        return dto;
//    }
//
//}
