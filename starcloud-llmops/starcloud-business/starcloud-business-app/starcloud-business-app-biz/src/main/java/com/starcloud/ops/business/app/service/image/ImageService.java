package com.starcloud.ops.business.app.service.image;

import com.starcloud.ops.business.app.api.image.dto.ImageDTO;
import com.starcloud.ops.business.app.api.image.request.ImageReqVO;

import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-12
 */
public interface ImageService {

    /**
     * 文字生成图片
     *
     * @param request 请求参数
     * @return 图片列表
     */
    List<ImageDTO> textToImage(ImageReqVO request);


}
