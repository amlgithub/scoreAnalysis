package com.zgczx.controller.user;

import com.zgczx.VO.ResultVO;
import com.zgczx.enums.ResultEnum;
import com.zgczx.exception.ScoreException;
import com.zgczx.repository.mysql1.user.model.WechatStudent;
import com.zgczx.service.user.UserService;
import com.zgczx.utils.ResultVOUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.transform.Result;
import java.util.List;

/**
 * @author aml
 * @date 2019/11/8 19:32
 */
@Api(description = "用户模块")
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    private String info = null;

    @ApiOperation(value = "用户登录")
    @GetMapping("/login")
    public ResultVO<?> login(
            @ApiParam(value = "openid", required = true)
            @RequestParam(value = "openid") String openid,
            HttpServletRequest request,
            HttpServletResponse response){

        WechatStudent wechatStudent = userService.login(openid,request,response);

        return ResultVOUtil.success(wechatStudent);
    }

    @ApiOperation(value = "用户登出")
    @GetMapping("/logout")
    public ResultVO<?> logout(HttpServletRequest request,
                              HttpServletResponse response){
        String logout = userService.logout(request, response);
        return ResultVOUtil.success(logout);
    }

    @PostMapping("/registerWechatStudent")
    public ResultVO<?> registerWechatStudent(
            @Valid WechatStudent wechatStudent, BindingResult bindingResult
           // ,@RequestParam(value = "openid") String openid
    ){
        if (bindingResult.hasErrors()){
            info = "【学生注册】参数不正确," + wechatStudent.toString();
            log.error(info);
            // throw new ScoreException（错误code，错误message）
            throw new ScoreException(ResultEnum.PARAM_EXCEPTION.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }

        WechatStudent wechatStudent1 = userService.registerWechatStudent(wechatStudent);
        return ResultVOUtil.success(0);
    }



}
