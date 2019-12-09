package com.zgczx.repository.mysql1.score.dto;

import com.zgczx.repository.mysql1.score.model.ManuallyEnterGrades;
import lombok.Data;

/**
 * 五、录入统计4） 根据考试名称和openid获取对应的数据
 * 封装下 图片，将string改为 string[]类型
 * @author aml
 * @date 2019/12/9 15:52
 */
@Data
public class ManuallyEnterGradesDTO {

    private ManuallyEnterGrades manuallyEnterGrades;
    //图片list
    private String[] imgurllist;
}
