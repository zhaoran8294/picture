package com.yupi.yupipicture.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLogicRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;


}
