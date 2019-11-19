package com.zgczx.repository.mysql2.scoretwo.dto;

import lombok.Data;

import java.util.Map;

/**
 * 定位对比二 和前排人的差距值
 * @author aml
 * @date 2019/11/19 19:18
 */
@Data
public class LocationComparisonDTO {

    private Map<String,Map<String,String>> stringMap;
}
