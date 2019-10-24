package com.zgczx.dto;

import com.zgczx.dataobject.score.ExamCoversionTotal;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

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
