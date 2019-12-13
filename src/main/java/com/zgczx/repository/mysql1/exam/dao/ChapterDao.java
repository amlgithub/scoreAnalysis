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
    @Query(value = "select DISTINCT chapter from e_chapter where level_name=?1", nativeQuery = true)
    List<String> findByLevelName(String levelName);

    //2. 获取所有的 节名称，根据 高中 和 章的名称
    @Query(value = "SELECT section FROM e_chapter WHERE level_name=?1 AND chapter=?2 ", nativeQuery = true)
    List<String> findByLevelNameAndChapter(String levelName, String chapter);

}