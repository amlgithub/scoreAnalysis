package com.zgczx.handler;

import com.zgczx.VO.ResultVO;
import com.zgczx.exception.ScoreException;
import com.zgczx.utils.ResultVOUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常拦截器，异常统一返回格式
 * @author aml
 * @date 2019/10/23 21:47
 */
@RestControllerAdvice
public class ScoreExceptionHandler {

    @ExceptionHandler(value = ScoreException.class)
//    @ResponseStatus(HttpStatus.FORBIDDEN) //控制返回的status ！= 200
    public ResultVO<?> handlerScoreException(ScoreException e){
        return ResultVOUtil.error(e.getCode(), e.getMessage(),e.data);
    }

}
