package com.yupi.yupipicture.service;

import javax.servlet.http.HttpServletRequest;
import com.yupi.yupipicture.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yupipicture.model.vo.LogicUserVO;

/**
* @author 达令
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-02-19 12:09:48
*/
public interface UserService extends IService<User> {

    long userRegister(String userAccount ,String userPassword, String checkPassword);

    LogicUserVO userLogin(String userAccount, String userPassword,  HttpServletRequest request);

    String getEncryptPassword(String userPassword);

    LogicUserVO getLogicUserVO(User user);
}
