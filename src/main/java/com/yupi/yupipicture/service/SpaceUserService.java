package com.yupi.yupipicture.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yupipicture.model.dto.space.SpaceAddRequest;
import com.yupi.yupipicture.model.dto.space.SpaceQueryRequest;
import com.yupi.yupipicture.model.dto.spaceUser.SpaceUserAddRequest;
import com.yupi.yupipicture.model.dto.spaceUser.SpaceUserQueryRequest;
import com.yupi.yupipicture.model.entity.Space;
import com.yupi.yupipicture.model.entity.SpaceUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yupipicture.model.entity.User;
import com.yupi.yupipicture.model.vo.SpaceUserVO;
import com.yupi.yupipicture.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 达令
* @description 针对表【space_user(空间用户关联)】的数据库操作Service
* @createDate 2025-03-05 00:07:19
*/
public interface SpaceUserService extends IService<SpaceUser> {
    /**
     * 创建空间成员
     * @param spaceUserAddRequest
     * @return
     */
    long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);
    /**
     * 空间成员数据校验方法
     * @param spaceUser
     * @param add 是否为创建时校验
     */
    void validSpaceUser(SpaceUser spaceUser,boolean add);

    /**
     * 获取空间成员包装类(单条)
     * @param spaceUser
     * @param request
     */
    SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request);



    /**
     * 获取空间成员包装类(列表)
     * @param spaceUserList
     */
    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList);


    /**
     * 获取查询条件
     * @param spaceUserQueryRequest
     * @return
     */
    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);



}

