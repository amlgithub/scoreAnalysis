package com.zgczx.VO;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * @author aml
 * @date 2019/9/10 15:59
 * 最外层的显示对象；
 * @param <T>
 */
@Data
public class ResultVO<T> {
    //错误码
    private Integer code;
    //提示信息
    private String msg;
    //具体内容
    private T data;

    public ResultVO(){
        this.data = data;
        this.code = code;
        this.msg = msg;
    }

    public ResultVO(T data, HttpStatus status){
        this.code = status.value();
        this.msg = status.getReasonPhrase();
        this.data = data;
    }

}
