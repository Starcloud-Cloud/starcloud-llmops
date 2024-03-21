package com.starcloud.ops.business.app.service.poster;

import com.starcloud.ops.business.app.feign.dto.PosterTemplate;
import com.starcloud.ops.business.app.feign.dto.PosterTemplateJson;
import com.starcloud.ops.business.app.feign.dto.PosterTemplateTypeDTO;
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
     * @param templateId 模板ID
     * @return 模板
     */
    PosterTemplateJson getTemplate(String templateId);

    /**
     * 获取模板列表
     *
     * @return 模板列表
     */
    List<PosterTemplate> templates();

    /**
     * 根据类型分组获取模板列表
     *
     * @return 模板列表
     */
    List<PosterTemplateTypeDTO> templateGroupByType();

    /**
     * 生成海报
     *
     * @return 海报URL
     */
    String poster(PosterRequest request);
}
