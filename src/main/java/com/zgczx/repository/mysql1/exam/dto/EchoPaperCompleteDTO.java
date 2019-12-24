package com.zgczx.repository.mysql1.exam.dto;

import lombok.Data;

import java.util.List;

/**
 * 这个是真正的返回给前端的 DTO，里面套了两个dto
 * @author aml
 * @date 2019/12/24 15:02
 */
@Data
public class EchoPaperCompleteDTO {

    private List<EchoPaperTotalDTO> list;

    private int effective;//2此时卷没做完; 1 为时卷做完;
}
