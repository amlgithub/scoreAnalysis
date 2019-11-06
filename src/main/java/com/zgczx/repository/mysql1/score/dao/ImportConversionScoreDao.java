package com.zgczx.repository.mysql1.score.dao;

import com.zgczx.repository.mysql1.score.model.ImportConversionScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author aml
 * @date 2019/9/22 16:28
 */
@Repository
public interface ImportConversionScoreDao extends JpaRepository<ImportConversionScore, Integer> {
    /**
     *
     * @param machine 某此考试的机读号
     * @param examType 具体的考试名称
     * @return ImportConversionScore对象
     */
    ImportConversionScore findByStudentMachineCardAndExamType(String machine, String examType);
}
