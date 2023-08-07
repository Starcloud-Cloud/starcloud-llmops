package com.starcloud.ops.business.app.service.image;

import com.starcloud.ops.business.app.api.image.dto.ImageMetaDTO;
import com.starcloud.ops.business.app.api.image.vo.response.ImageMessageRespVO;
import com.starcloud.ops.business.app.api.image.vo.response.ImageRespVO;
import com.starcloud.ops.business.app.controller.admin.image.vo.ImageReqVO;

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
     * 查询历史图片列表
     *
     * @return 图片列表
     */
    ImageRespVO historyGenerateImages();

    /**
     * 文本生成图片
     *
     * @param request 请求参数
     * @return 图片信息
     */
    ImageMessageRespVO generateImage(ImageReqVO request);
}
