package com.zgczx.repository.mysql1.exam.dto;

import lombok.Data;

/**
 * @author aml
 * @date 2019/12/24 14:19
 */
@Data
public class EchoPaperTotalDTO {

    private EchoPaperDTO echoPaperDTO;

    private int complete; //是否做完这个卷子

    private String userOption;//用户的每道题的选项
}
