package com.yupi.yupipicture.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yupipicture.model.dto.picture.PictureQueryRequest;
import com.yupi.yupipicture.model.dto.picture.PictureReviewRequest;
import com.yupi.yupipicture.model.dto.picture.PictureUploadByBatchRequest;
import com.yupi.yupipicture.model.dto.picture.PictureUploadRequest;
import com.yupi.yupipicture.model.dto.user.UserQueryRequest;
import com.yupi.yupipicture.model.entity.Picture;
import com.yupi.yupipicture.model.entity.User;
import com.yupi.yupipicture.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
* @author 达令
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-02-23 16:13:36
*/
public interface PictureService extends IService<Picture> {


    /**
     * 获取图片包装类(单条)
     * @param picture
     * @param request
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);



    /**
     * 获取图片包装类(分页)
     * @param picturePage
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 图片数据校验方法
     */
    void validPicture(Picture picture);


    /**
     * 上传图片
     *
     * @param inputSource
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(Object inputSource,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);


    /**
     * 获取查询条件
     * @param pictureQueryRequest
     * @return
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 图片审核
     *
     * @param pictureReviewRequest
     * @param loginUser
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    void fillReviewParams(Picture picture, User loginUser);

    /**
     * 批量抓取和创建图片
     *
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return 成功创建的图片数
     */
    Integer uploadPictureByBatch(
            PictureUploadByBatchRequest pictureUploadByBatchRequest,
            User loginUser
    );

    /**
     * 清理图片文件
     * @param oldPicture
     */
    void clearPictureFile(Picture oldPicture);
}
