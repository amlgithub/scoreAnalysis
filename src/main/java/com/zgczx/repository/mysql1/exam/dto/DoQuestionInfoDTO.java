package com.zgczx.repository.mysql1.exam.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 六、动态实时呈现用户做题详情 并记录用户所有的做题情况 的封装类
 * @author aml
 * @date 2019/12/17 15:46
 */
@Data
public class DoQuestionInfoDTO {
    //总共做题数量
    private int questionCount;
//作对题的数量
    private int doRight;
//作错题的数量
    private int doError;
//未做题的数量
    private int notDo;

    private List<Integer> doRightList;  // 做对的题号
    private List<Integer> doErrorList;  // 做错的题号
    private List<Integer> notDoList; // 未做的题号

}
