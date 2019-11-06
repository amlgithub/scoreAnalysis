package com.zgczx.repository.mysql1.score.dto;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author aml
 * @date 2019/10/23 14:14
 */
@Data
@Entity
public class SubjectDTO  {

    @Id
    int id;
    private String yuwen;
    private String shuxue;
    private String yingyu;
    private String wuli;
    private String huaxue;
    private String shengwu;
    private String zhengzhi;
    private String lishi;
    private String dili;

}
