package com.zgczx.exception;

import com.zgczx.enums.ResultEnum;
import com.zgczx.enums.UserEnum;
import lombok.Getter;
import org.apache.catalina.User;

/**
 * @author aml
 * @date 2019/9/10 20:32
 * 统一的异常调度
 */
@Getter
public class ScoreException extends RuntimeException{

    public Integer code;

    public String data;

    public ScoreException(Integer code, String message){
        super(message);
        this.code = code;
    }

    /**
     * ResultEnum 定义的错误code和msg
     * @param resultEnum result结果集中相关的错误code、msg
     * @param data Impl中自定义的info 具体错误信息
     */
    public ScoreException(ResultEnum resultEnum, String data){
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
        this.data = data;
    }

    /**
     * UserEnum 定义的错误code和msg
     * @param userEnum user相关的错误code、msg
     * @param data Impl中自定义的info 具体错误信息
     */
    public ScoreException(UserEnum userEnum, String data){
        super(userEnum.getMessage());
        this.code = userEnum.getCode();
        this.data = data;
    }
}
