package com.yupi.yupipicture.controller;

import com.yupi.yupipicture.common.BaseResponse;
import com.yupi.yupipicture.common.ResultUtils;
import com.yupi.yupipicture.exception.ErrorCode;
import com.yupi.yupipicture.exception.ThrowUtils;
import com.yupi.yupipicture.model.dto.UserLogicRequest;
import com.yupi.yupipicture.model.dto.UserRegisterRequest;
import com.yupi.yupipicture.model.vo.LogicUserVO;
import com.yupi.yupipicture.service.UserService;
import lombok.val;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public BaseResponse<LogicUserVO> userLogin(@RequestBody UserLogicRequest userLogicRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLogicRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userLogicRequest.getUserAccount();
        String userPassword = userLogicRequest.getUserPassword();
        LogicUserVO logicUserVO = userService.userLogin(userAccount, userPassword,request);
        return ResultUtils.success(logicUserVO);
    }
}
