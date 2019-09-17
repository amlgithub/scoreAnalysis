package com.zgczx.utils;

import com.zgczx.VO.ResultVO;
import lombok.Data;

/**
 * @author aml
 * @date 2019/9/10 16:06
 * 成功状态码，和错误提示
 */
@Data
public class ResultVOUtil {

    public static ResultVO success(Object object){
        ResultVO resultVO = new ResultVO();
        resultVO.setData(object);
        resultVO.setCode(0);
        resultVO.setMsg("成功");
        return resultVO;
    }

    public static ResultVO success(){
        return success(null);
    }

    public static ResultVO error(Integer code, String msg, Object object){
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(code);
        resultVO.setMsg(msg);
        resultVO.setData(object);
        return resultVO;

    }
}
