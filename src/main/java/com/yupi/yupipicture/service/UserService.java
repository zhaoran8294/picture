package com.yupi.yupipicture.service;

import javax.servlet.http.HttpServletRequest;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.yupipicture.model.dto.user.UserQueryRequest;
import com.yupi.yupipicture.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yupipicture.model.vo.LoginUserVO;
import com.yupi.yupipicture.model.vo.UserVO;

import java.util.List;


/**
* @author 达令
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-02-19 12:09:48
*/
public interface UserService extends IService<User> {

    long userRegister(String userAccount ,String userPassword, String checkPassword);

    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取加密后的密码
     * @param userPassword
     * @return
     */
    String getEncryptPassword(String userPassword);

    /**
     * 当前登录用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取脱敏后的登录用户信息
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获得脱敏后的用户信息
     * @param user
     * @return
     */

    UserVO getUserVO(User user);
    /**
     * 获得脱敏后的用户信息列表
     * @param userList
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 用户注销
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取查询条件
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
}
