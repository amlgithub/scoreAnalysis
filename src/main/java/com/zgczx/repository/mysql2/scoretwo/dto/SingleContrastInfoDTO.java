package com.zgczx.repository.mysql2.scoretwo.dto;

import lombok.Data;

import java.util.Map;

/**
 * 定位对比：1. 单科的 自己排名、目标排名、自己分数、目标分数、差值
 * @author aml
 * @date 2019/11/27 14:29
 */
@Data
public class SingleContrastInfoDTO {

    private Map<String, Map<String,String>> map;

}
