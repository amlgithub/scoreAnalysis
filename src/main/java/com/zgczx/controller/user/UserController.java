package com.zgczx.controller.user;

import com.zgczx.VO.ResultVO;
import com.zgczx.repository.mysql1.user.model.WechatStudent;
import com.zgczx.service.user.UserService;
import com.zgczx.utils.ResultVOUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

}
