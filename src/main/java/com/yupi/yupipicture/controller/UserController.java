package com.yupi.yupipicture.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yupipicture.annotation.AuthCheck;
import com.yupi.yupipicture.common.BaseResponse;
import com.yupi.yupipicture.common.DeleteRequest;
import com.yupi.yupipicture.common.ResultUtils;
import com.yupi.yupipicture.constant.UserConstant;
import com.yupi.yupipicture.exception.BusinessException;
import com.yupi.yupipicture.exception.ErrorCode;
import com.yupi.yupipicture.exception.ThrowUtils;
import com.yupi.yupipicture.model.dto.user.*;
import com.yupi.yupipicture.model.entity.User;
import com.yupi.yupipicture.model.vo.LoginUserVO;
import com.yupi.yupipicture.model.vo.UserVO;
import com.yupi.yupipicture.service.UserService;
import lombok.val;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
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
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLogicRequest userLogicRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLogicRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userLogicRequest.getUserAccount();
        String userPassword = userLogicRequest.getUserPassword();
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword,request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 获取当前登录用户
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(loginUser));
    }

    /**
     * 用户注销
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout (HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        boolean result = userService.userLogout(request);
        return ResultUtils.success(true);
    }

    /**
     *创建用户
     * @param userAddRequest
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest){
        ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtil.copyProperties(userAddRequest,user);
        //默认密码
        final String DEFAULT_PASSWORD = "12345678";
        String encryptPassword = userService.getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);
        //插入数据库
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());

    }

    /**
     * 根据id获取用户(管理员)
     * @param id
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id){
        ThrowUtils.throwIf(id <=0,ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NO_AUTH_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据id获取包装类
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id){
        BaseResponse<User> response =getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 删除用户
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest){
        if (deleteRequest == null || deleteRequest.getId() <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b =userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更行用户
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest){
        if (userUpdateRequest == null || userUpdateRequest.getId() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtil.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest){
        ThrowUtils.throwIf(userQueryRequest == null , ErrorCode.PARAMS_ERROR);
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userpage = userService.page(new Page<>(current, pageSize), userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current ,pageSize, userpage.getTotal());
        List<UserVO> userVOList = userService.getUserVOList(userpage.getRecords());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }
}




