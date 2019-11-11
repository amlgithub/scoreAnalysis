package com.zgczx.repository.mysql1.score.dto;

import lombok.Data;

import java.util.List;

/**
 * @author aml
 * @date 2019/11/11 14:37
 */
@Data
public class MonthByYearListDTO {

    private List<String> list;

    // 这一年共有几次考试
    private int countTimes;
}
