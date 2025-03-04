package com.yupi.yupipicture.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.yupipicture.exception.BusinessException;
import com.yupi.yupipicture.exception.ErrorCode;
import com.yupi.yupipicture.exception.ThrowUtils;
import com.yupi.yupipicture.mapper.SpaceMapper;
import com.yupi.yupipicture.model.dto.space.SpaceAddRequest;
import com.yupi.yupipicture.model.dto.space.SpaceQueryRequest;
import com.yupi.yupipicture.model.entity.Space;

import com.yupi.yupipicture.model.entity.User;
import com.yupi.yupipicture.model.enums.SpaceLevelEnum;
import com.yupi.yupipicture.model.vo.SpaceVO;
import com.yupi.yupipicture.model.vo.UserVO;
import com.yupi.yupipicture.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author 达令
* @description 针对表【space(空间)】的数据库操作Service实现
* @createDate 2025-03-03 17:28:33
*/
@Service
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space> implements com.yupi.yupipicture.service.SpaceService {

    @Resource
    private UserService userService;

    @Resource
    private TransactionTemplate transactionTemplate;

    /**
     * 创建空间
     * @param spaceAddRequest
     * @param loginUser
     * @return
     */
//    @Override
//
//    public long addSpace(SpaceAddRequest spaceAddRequest, User loginUser) {
//        //1.填充参数默认值
//        Space space = new Space();
//        BeanUtils.copyProperties(spaceAddRequest, space);
//        if (StrUtil.isBlank(space.getSpaceName())){
//            space.setSpaceName("默认空间");
//        }
//        if (space.getSpaceLevel() == null){
//            space.setSpaceLevel(SpaceLevelEnum.COMMON.getValue());
//        }
//        //填充容量和大小
//        this.fillSpaceBySpaceLevel(space);
//        //2.校验参数
//        this.validSpace(space,true);
//        //3.校验权限，非管理员只能创建普通类别的空间
//        Long userId = loginUser.getId();
//        space.setUserId(userId);
//        if (SpaceLevelEnum.COMMON.getValue() != space.getSpaceLevel()){
//            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"无权创建指定级别空间");
//        }
//        //4.控制统一用户只能创建一个私有空间
//        String lock = String.valueOf(userId).intern();
//        synchronized (lock) {
//            Long newSpaceId = transactionTemplate.execute(status ->{
//                    //判断是否已有空间
//            boolean exists = this.lambdaQuery()
//                    .eq(Space::getUserId, userId)
//                    .exists();
//            //如果有空间，就不能再创建
//            ThrowUtils.throwIf(exists, ErrorCode.OPERATION_ERROR,"每个用户仅能有一个用户异常");
//            //创建
//            boolean result = this.save(space);
//            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR,"保存空间到数据库失败");
//            //返回新写的数据 id
//            return space.getId();});
//            return Optional.ofNullable(newSpaceId).orElse(-1L);
//        }
//    }


    @Override
    public long addSpace(SpaceAddRequest spaceAddRequest, User loginUser) {
        // 在此处将实体类和 DTO 进行转换
        Space space = new Space();
        BeanUtils.copyProperties(spaceAddRequest, space);
        // 默认值
        if (StrUtil.isBlank(spaceAddRequest.getSpaceName())) {
            space.setSpaceName("默认空间");
        }
        if (spaceAddRequest.getSpaceLevel() == null) {
            space.setSpaceLevel(SpaceLevelEnum.COMMON.getValue());
        }
        // 填充数据
        this.fillSpaceBySpaceLevel(space);
        // 数据校验
        this.validSpace(space, true);
        Long userId = loginUser.getId();
        space.setUserId(userId);
        // 权限校验
        if (SpaceLevelEnum.COMMON.getValue() != spaceAddRequest.getSpaceLevel() && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限创建指定级别的空间");
        }
        // 针对用户进行加锁
        String lock = String.valueOf(userId).intern();
        synchronized (lock) {
            Long newSpaceId = transactionTemplate.execute(status -> {
                boolean exists = this.lambdaQuery().eq(Space::getUserId, userId).exists();
                ThrowUtils.throwIf(exists, ErrorCode.OPERATION_ERROR, "每个用户仅能有一个私有空间");
                // 写入数据库
                boolean result = this.save(space);
                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
                // 返回新写入的数据 id
                return space.getId();
            });
            // 返回结果是包装类，可以做一些处理
            return Optional.ofNullable(newSpaceId).orElse(-1L);
        }
    }


    @Override
    public SpaceVO getSpaceVO(Space space, HttpServletRequest request) {
        // 对象转封装类
        SpaceVO spaceVO = SpaceVO.objToVo(space);
        // 关联查询用户信息
        Long userId = space.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            spaceVO.setUser(userVO);
        }
        return spaceVO;
    }

    @Override
    public Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request) {

        List<Space> spaceList = spacePage.getRecords();
        Page<SpaceVO> spaceVOPage = new Page<>(spacePage.getCurrent(), spacePage.getSize(), spacePage.getTotal());
        if (CollUtil.isEmpty(spaceList)) {
            return spaceVOPage;
        }
        // 对象列表 => 封装对象列表
        List<SpaceVO> spaceVOList = spaceList.stream().map(SpaceVO::objToVo).collect(Collectors.toList());
        // 1. 关联查询用户信息
        Set<Long> userIdSet = spaceList.stream().map(Space::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 填充信息
        spaceVOList.forEach(spaceVO -> {
            Long userId = spaceVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            spaceVO.setUser(userService.getUserVO(user));
        });
        spaceVOPage.setRecords(spaceVOList);
        return spaceVOPage;
    }
    @Override
    public void validSpace(Space space,boolean add) {
        ThrowUtils.throwIf(space == null, ErrorCode.PARAMS_ERROR);
        String spaceName = space.getSpaceName();
        Integer spaceLevel = space.getSpaceLevel();
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(spaceLevel);
        //创建时校验
        if (add){
            if (StrUtil.isBlank(spaceName)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"空间名称不能为空");
            }
            if (spaceLevel == null){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"空间级别不能为空");
            }
        }
        // 修改数据时，空间名称进行校验
        if (spaceLevel != null && spaceLevelEnum == null)  {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"空间级别不存在");
        }
        if (StrUtil.isNotBlank(spaceName ) && spaceName.length() >30) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"空间名称过长");
        }
    }

    @Override
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest) {
        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        if (spaceQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = spaceQueryRequest.getId();
        Long userId = spaceQueryRequest.getUserId();
        String spaceName = spaceQueryRequest.getSpaceName();
        Integer spaceLevel = spaceQueryRequest.getSpaceLevel();
        String sortField = spaceQueryRequest.getSortField();
        String sortOrder = spaceQueryRequest.getSortOrder();
        //拼接查询条件
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(spaceName), "name", spaceName);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceLevel), "spaceLevel", spaceLevel);
        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public void fillSpaceBySpaceLevel(Space space) {
        // 根据空间级别，自动填充限额
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(space.getSpaceLevel());
        if (spaceLevelEnum != null) {
            long maxSize = spaceLevelEnum.getMaxSize();
            if (space.getMaxSize() == null) {
                space.setMaxSize(maxSize);
            }
            long maxCount = spaceLevelEnum.getMaxCount();
            if (space.getMaxCount() == null) {
                space.setMaxCount(maxCount);
            }
        }
    }

}




