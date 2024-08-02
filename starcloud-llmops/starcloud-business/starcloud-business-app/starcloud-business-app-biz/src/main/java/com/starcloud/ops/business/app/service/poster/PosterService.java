package com.starcloud.ops.business.app.service.poster;

import com.starcloud.ops.business.app.feign.dto.PosterImage;
import com.starcloud.ops.business.app.feign.dto.PosterTemplate;
import com.starcloud.ops.business.app.feign.dto.PosterTemplateType;
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
     * 获取模板
     *
     * @param code 模板ID
     * @return 模板
     */
    PosterTemplate getTemplate(String code);

    /**
     * 获取模板列表
     *
     * @return 模板列表
     */
    List<PosterTemplate> listTemplate();

    /**
     * 根据类型分组获取模板列表
     *
     * @return 模板列表
     */
    List<PosterTemplateType> listPosterTemplateType();

    /**
     * 生成海报
     *
     * @return 海报URL
     */
    List<PosterImage> poster(PosterRequest request);
}
