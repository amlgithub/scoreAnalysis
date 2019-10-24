package com.zgczx.repository.score;

import com.zgczx.dataobject.score.ExamCoversionTotal;
import com.zgczx.dto.SubjectDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * @author aml
 * @date 2019/10/23 14:18
 */
@Repository
public interface SubjectDTODao extends JpaRepository<SubjectDTO, Integer> {

    @Query(value = "SELECT id,AVG(yuwen_score) as yuwen,AVG(shuxue_score) as shuxue,AVG(yingyu_score)as yingyu, AVG(wuli_coversion)as wuli,AVG(huaxue_coversion)as huaxue,AVG(shengwu_coversion)as shengwu,AVG(zhengzhi_coversion)as zhengzhi,AVG(lishi_coversion)as lishi,AVG(dili_coversion)as dili FROM exam_coversion_total WHERE exam_type=?1" , nativeQuery = true)
    List<SubjectDTO> avgSubject(String examType);
}
