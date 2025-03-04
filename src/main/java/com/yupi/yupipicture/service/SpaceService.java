package com.yupi.yupipicture.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yupipicture.model.dto.space.SpaceAddRequest;
import com.yupi.yupipicture.model.dto.space.SpaceQueryRequest;
import com.yupi.yupipicture.model.entity.Space;
import com.yupi.yupipicture.model.entity.User;
import com.yupi.yupipicture.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author 达令
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2025-03-03 17:28:33
*/
public interface SpaceService extends IService<Space> {

    /**
     * 创建空间
     * @param spaceAddRequest
     * @param loginUser
     * @return
     */
    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);

    /**
     * 获取空间包装类(单条)
     * @param space
     * @param request
     */
    SpaceVO getSpaceVO(Space space, HttpServletRequest request);



    /**
     * 获取空间包装类(分页)
     * @param spacePage
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

    /**
     * 空间数据校验方法
     * @param space
     * @param add 是否为创建时校验
     */
    void validSpace(Space space,boolean add);



    /**
     * 获取查询条件
     * @param spaceQueryRequest
     * @return
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);


    void fillSpaceBySpaceLevel(Space space);
}
