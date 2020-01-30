package com.zgczx.repository.mysql1.exam.dto;

import lombok.Data;

import java.util.Map;

/**
 * 十五、获取此章下面的所有节的名称和对应的错题数量
 * @author aml
 * @date 2019/12/29 15:16
 */
@Data
public class SectionErrNumberDTO {

    private Map<String, Integer> sectionNumber;// 节的名称 和 错题数量

}
