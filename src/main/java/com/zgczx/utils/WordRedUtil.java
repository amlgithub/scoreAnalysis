package com.zgczx.utils;

import com.alibaba.fastjson.JSONObject;
import com.microsoft.schemas.vml.CTShape;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPicture;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 读取Word 的 util
 * //以数字开头并且包含.表示一个新的题目开始
 *             String regex = "^\\d{1,10}\\.";
 *  *             Pattern pattern = Pattern.compile(regex);
 *  *             pattern.matcher(line)
 * @author aml
 * @date 2019/12/11 15:23
 */
@Slf4j
public class WordRedUtil {

    private final static String doc = "doc";
    private final static String docx = "docx";

    public static JSONObject readWord(MultipartFile file) throws IOException {
        JSONObject jsonObject = new JSONObject();
        //1. 检查文件
        checkFile(file);
        //2.创建输入流读取DOC文件
        String filename = file.getOriginalFilename();
        System.out.println("文件名称： " + filename);
        InputStream inputStream = file.getInputStream();
        String text = "";
        if (filename.endsWith("doc")){
            //FileInputStream fis = new FileInputStream((File) file);
            HWPFDocument doc = new HWPFDocument(inputStream);
            text = doc.getDocumentText();

        }else {
//3. 创建wordExtractor
            XWPFDocument xdoc = new XWPFDocument(inputStream);
            XWPFWordExtractor extractor = new XWPFWordExtractor(xdoc);

            //WordExtractor extractor = new WordExtractor(inputStream);
            //4. 对doc文件进行提取
            text = extractor.getText();
            int i = 1;// 第一个图片
            //用XWPFDocument的getAllPictures来获取所有图片
            List<XWPFPictureData> pictureDataList = xdoc.getAllPictures();
            List<String> imgList = new ArrayList<>();
            for (XWPFPictureData pic : pictureDataList) {
                byte[] fileBytes = pic.getData();

                String fileName = filename + "_" + i + "_" + pic.getFileName();
                i++;

                if (fileBytes.length > 500) {//文件大于 500 字节，筛选出一些莫名其妙的小图片
                    // 文件上传路径，这个路径是13服务器上的地址，而工程是在14服务器上，所以暂时都是手动存图片。
//                String uploadPath = "/home/bigdata/application/canteen-system-image/";
                    String uploadPath = "J:\\A";
                    // 上传文件
                    File upload = new File(uploadPath, fileName);
                    OutputStream out = new FileOutputStream(upload);
                    out.write(fileBytes);
                    out.flush();
                    System.out.println("download success");
                    out.close();

                    // 上传到服务器上的url，可直接拿到浏览器直接打开的url
                    String fileUrl = "http://zhongkeruitong.top/image/" + fileName;
//                returnMsg = "http://zhongkeruitong.top/image/" + fileName;
                    log.info("===> 图片上传地址：" + fileUrl);
                    imgList.add(fileUrl);
                }
            }
            jsonObject.put("imgList", imgList);
        }
        jsonObject.put("doctext", text);
        System.out.println(jsonObject);

        jsonObject.put("title",filename);

//        return text;
        return jsonObject;
    }


    private static void checkFile(MultipartFile file) throws IOException {
        //判断文件是否存在
        if (null == file) {
            log.error("文件不存在！");
            throw new FileNotFoundException("文件不存在！");
        }
        //获得文件名
        String fileName = file.getOriginalFilename();
        //判断文件是否是excel文件
        if (!fileName.endsWith(doc) && !fileName.endsWith(docx)) {
            log.error(fileName + "不是word文件");
            throw new IOException(fileName + "不是word文件");
        }
    }


    //获取某一个段落中的所有图片索引
    public static List<String> readImageInParagraph(XWPFParagraph paragraph) {
        //图片索引List
        List<String> imageBundleList = new ArrayList<String>();
        //段落中所有XWPFRun
        List<XWPFRun> runList = paragraph.getRuns();
        for (XWPFRun run : runList) {
            //XWPFRun是POI对xml元素解析后生成的自己的属性，无法通过xml解析，需要先转化成CTR
            CTR ctr = run.getCTR();
            //对子元素进行遍历
            XmlCursor c = ctr.newCursor();
            //这个就是拿到所有的子元素：
            c.selectPath("./*");
            while (c.toNextSelection()) {
                XmlObject o = c.getObject();
                //如果子元素是<w:drawing>这样的形式，使用CTDrawing保存图片
                if (o instanceof CTDrawing) {
                    CTDrawing drawing = (CTDrawing) o;
                    CTInline[] ctInlines = drawing.getInlineArray();
                    for (CTInline ctInline : ctInlines) {
                        CTGraphicalObject graphic = ctInline.getGraphic();
                        //
                        XmlCursor cursor = graphic.getGraphicData().newCursor();
                        cursor.selectPath("./*");
                        while (cursor.toNextSelection()) {
                            XmlObject xmlObject = cursor.getObject();
// 如果子元素是<pic:pic>这样的形式
                            if (xmlObject instanceof CTPicture) {
                                org.openxmlformats.schemas.drawingml.x2006.picture.CTPicture picture = (org.openxmlformats.schemas.drawingml.x2006.picture.CTPicture) xmlObject;
                                //拿到元素的属性
                                imageBundleList.add(picture.getBlipFill().getBlip().getEmbed());
                            }
                        }
                    }
                }
                //使用CTObject保存图片
//<w:object>形式
                if (o instanceof CTObject) {
                    CTObject object = (CTObject) o;
                    System.out.println(object);
                    XmlCursor w = object.newCursor();
                    w.selectPath("./*");
                    while (w.toNextSelection()) {
                        XmlObject xmlObject = w.getObject();
                        if (xmlObject instanceof CTShape) {
                            CTShape shape = (CTShape) xmlObject;
                            //imageBundleList.add(shape.getImagedataArray()[0]);
                        }
                    }
                }
            }
        }
        return imageBundleList;
    }
}
