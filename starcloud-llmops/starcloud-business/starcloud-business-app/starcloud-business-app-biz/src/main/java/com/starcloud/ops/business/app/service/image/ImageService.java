package com.starcloud.ops.business.app.service.image;

import com.starcloud.ops.business.app.api.image.dto.ImageMetaDTO;
import com.starcloud.ops.business.app.api.image.dto.UploadImageInfoDTO;
import com.starcloud.ops.business.app.controller.admin.image.vo.ImageReqVO;
import com.starcloud.ops.business.app.controller.admin.image.vo.ImageRespVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-12
 */
public interface ImageService {

    /**
     * 获取图片元数据
     *
     * @return 图片元数据
     */
    Map<String, List<ImageMetaDTO>> meta();

    /**
     * 上传图片，并且返回图片URL
     *
     * @param image 上传图片
     * @return 图片url
     */
    UploadImageInfoDTO upload(MultipartFile image);

    /**
     * 上传图片，图片大小不能超过1024 并且返回图片URL
     *
     * @param image 上传图片
     * @return 图片url
     */
    UploadImageInfoDTO uploadLimit1024(MultipartFile image);

    /**
     * 文本生成图片
     *
     * @param request 请求参数
     * @return 图片信息
     */
    ImageRespVO execute(ImageReqVO request);

}
