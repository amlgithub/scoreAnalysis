package com.zgczx.repository.mysql1.exam.dao;

import com.zgczx.repository.mysql1.exam.model.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 章节表
 * @author aml
 * @date 2019/12/11 17:00
 */
@Repository
public interface ChapterDao extends JpaRepository<Chapter, Integer> {

    //1. 获取 此年级的所有的章名称：例如获取高一的所有章
    @Query(value = "select DISTINCT chapter from e_chapter where level_name=?1 and subject=?2 ", nativeQuery = true)
    List<String> findByLevelNameAndSubject(String levelName, String subject);

    //2. 获取所有的 节名称，根据 高中 和 章的名称
    @Query(value = "SELECT section FROM e_chapter WHERE level_name=?1 AND chapter=?2 and subject=?3 ", nativeQuery = true)
    List<String> findByLevelNameAndChapterAndSubject(String levelName, String chapter, String subject);

    //3. 获取所有章的名称，根据节的名称
//    SELECT DISTINCT chapter FROM e_chapter WHERE section IN('第1节　细胞膜的结构和功能','第2节　细胞','ddd')
    @Query(value = "SELECT DISTINCT chapter FROM e_chapter WHERE section IN(?1)", nativeQuery = true)
    List<String> findBySectionIn(List<String> sectionList);

    //4. 获取所有节名称，根据章名称 和 科目
    @Query(value = "SELECT section FROM e_chapter WHERE chapter=?1 AND SUBJECT=?2 ", nativeQuery = true)
    List<String> findByChapterAndSubject(String chapter,String subject);
}
