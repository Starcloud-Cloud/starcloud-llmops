package com.starcloud.ops.business.app.service.template;

import com.starcloud.ops.business.app.api.template.dto.TemplateDTO;
import com.starcloud.ops.business.app.api.template.request.TemplatePageQuery;
import com.starcloud.ops.business.app.api.template.request.TemplateRequest;
import com.starcloud.ops.business.app.api.template.request.TemplateUpdateRequest;
import com.starcloud.ops.framework.common.api.dto.PageResp;

import java.util.List;

/**
 * 模版管理服务
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
public interface TemplateService {

    /**
     * 查询推荐的模版列表
     *
     * @return 模版列表
     */
    List<TemplateDTO> listRecommendedTemplates();

    /**
     * 分页查询模版列表
     *
     * @param query 查询条件
     * @return 模版列表
     */
    PageResp<TemplateDTO> page(TemplatePageQuery query);

    /**
     * 分页查询下载的模版列表
     *
     * @param query 查询条件
     * @return 模版列表
     */
    PageResp<TemplateDTO> pageDownloadTemplates(TemplatePageQuery query);

    /**
     * 分页查询我的模版列表
     *
     * @param query 查询条件
     * @return 模版列表
     */
    PageResp<TemplateDTO> pageMyTemplate(TemplatePageQuery query);

    /**
     * 根据模版 ID 获取模版详情
     *
     * @param id 模版 ID
     * @return 模版详情
     */
    TemplateDTO getById(Long id);

    /**
     * 创建模版
     *
     * @param request 模版信息
     * @return 是否创建成功
     */
    Boolean create(TemplateRequest request);

    /**
     * 复制模版
     *
     * @param request 模版信息
     * @return 是否复制成功
     */
    Boolean copy(TemplateRequest request);

    /**
     * 更新模版
     *
     * @param request 模版信息
     * @return 是否更新成功
     */
    Boolean modify(TemplateUpdateRequest request);

    /**
     * 删除模版
     *
     * @param id 模版ID
     * @return 是否删除成功
     */
    Boolean delete(Long id);

    /**
     * 校验模版是否已经下载过
     *
     * @param marketKey 模版市场特有的 key，唯一。
     * @return 是否已经下载
     */
    Boolean verifyHasDownloaded(String marketKey);

}
