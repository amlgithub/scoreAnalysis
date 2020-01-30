package com.zgczx.controller.exam;

import com.zgczx.VO.ResultVO;
import com.zgczx.repository.mysql1.exam.dao.*;
import com.zgczx.repository.mysql3.unifiedlogin.dao.UserLoginDao;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author aml
 * @date 2020/1/3 14:18
 */
@Api(description = "word解析为题库模块二")
@RestController
@RequestMapping("/word2")
@Slf4j
public class WordToQuestionController {

    @Autowired
    private ChapterDao chapterDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private ExamPaperDao examPaperDao;

    @Autowired
    private UserQuestionRecordDao userQuestionRecordDao;

    @Autowired
    private UserCollectDao userCollectDao;

    @Autowired
    private ExamContentDao examContentDao;

    @Autowired
    private UserPaperRecordDao userPaperRecordDao;

    @Autowired
    private UserLoginDao userLoginDao;

    private String info;


    @ApiOperation(value = "一、 读取Word中的内容并生成json串处理")
    @PostMapping("/parseWord2")
    public ResultVO<?> parseWord2(
            @ApiParam(value = "file文件", required = true)
            @RequestParam("filename") MultipartFile file,
            HttpSession session, HttpServletRequest request) throws Exception {

        InputStream in = file.getInputStream();//将Word文件转换为字节流

//        InputStream in = new FileInputStream(file.getOriginalFilename());//将Word文件转换为字节流

        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(in);
        //获取word所有内容
        List<Object> list = wordMLPackage.getMainDocumentPart().getContent();
        //将文件中的内容分隔成每一道题
        List<Map<String, String>> questionMapList = getSubjectList(list);

        log.info("【打印：】{}", questionMapList);
        return null;
    }

    /**
     * 将word中的题目分割成list<Object>放入List
     *
     * @param list
     * @return
     */
    private List<Map<String, String>> getSubjectList(List<Object> list) {
        List<Map<String, String>> subjectList = new ArrayList<Map<String, String>>();
        List<String> stringList = new ArrayList<>();
        StringBuffer subjectItem = new StringBuffer();
        int count = 0;
        int qNum = 0;
        //划分题目
        //以数字开头并且包含.表示一个新的题目开始
        String regex = "^\\d{1,100}\\．";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = null;
        Map<String, String> tempMap = new HashMap<String, String>();
        String qtype = "";
        String oldQtype = "";
        String line = "";
        int titleNumber = 0;
        for (int i = 0; i < list.size(); i++) {
            line = list.get(i).toString();
            m = pattern.matcher(line);
            if (m.find()) {//题干
                count++;

                if (qNum > 0) {//不是文件文件第一个题干，将之前的buffer保存

                    tempMap = new HashMap<String, String>();
//                    tempMap.put("qtype", oldQtype);//这个是这道题的类型：单选、多选等
                    tempMap.put(String.valueOf(qNum), subjectItem.toString());
                    subjectList.add(tempMap);

                    subjectItem = new StringBuffer();
                    subjectItem.append(line);

                } else {//文件第一个题干，创建新的buffer，并将题干放入buffer
//                    if (subjectItem != null){
//                        tempMap = new HashMap<String, String>();
////                    tempMap.put("qtype", oldQtype);//这个是这道题的类型：单选、多选等
//                        tempMap.put(String.valueOf(0), subjectItem.toString());
//                        subjectList.add(tempMap);
//                    }else {
                        subjectItem = new StringBuffer();
                        subjectItem.append(line);
//                    }
                }
                qNum++;

            }else {

                subjectItem.append(line);

            }



        }
        return subjectList;
    }


}
