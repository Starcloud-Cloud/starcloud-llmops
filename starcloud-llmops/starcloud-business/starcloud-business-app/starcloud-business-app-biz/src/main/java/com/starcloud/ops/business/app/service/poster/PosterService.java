package com.starcloud.ops.business.app.service.poster;

import com.starcloud.ops.business.app.feign.dto.PosterTemplateDTO;
import com.starcloud.ops.business.app.feign.request.poster.PosterRequest;

import java.util.List;

/**
 * 海报服务
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
public interface PosterService {

    /**
     * 获取模板列表
     *
     * @return 模板列表
     */
    List<PosterTemplateDTO> templates();

    /**
     * 生成海报
     *
     * @return 海报URL
     */
    String poster(PosterRequest request);
}