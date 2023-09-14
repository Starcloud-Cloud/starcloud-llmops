package com.starcloud.ops.business.app.service.image.clipdrop.strategy;

import com.starcloud.ops.business.app.feign.request.clipdrop.ClipDropImageRequest;
import com.starcloud.ops.business.app.feign.response.ClipDropImage;
import com.starcloud.ops.business.app.feign.response.ImageResponse;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-11
 */
public interface ClipDropImageHandler {

    /**
     * 处理图片
     *
     * @param request 请求参数
     * @return 图片响应实体
     */
    ImageResponse<ClipDropImage> handle(ClipDropImageRequest request);


}
