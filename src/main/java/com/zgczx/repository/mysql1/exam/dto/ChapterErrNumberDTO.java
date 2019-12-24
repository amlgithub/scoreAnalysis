package com.zgczx.repository.mysql1.exam.dto;

import lombok.Data;

import java.util.Map;

/**
 * 十二、点击练习错题时，展现的章节名称和对应的错题数量  封装类
 * @author aml
 * @date 2019/12/23 19:02
 */
@Data
public class ChapterErrNumberDTO {

    private String gradeLevel;//年级水平

    private Map<String, Integer> chapterNumber;//错题的章节名称 和 对应的数量
}
