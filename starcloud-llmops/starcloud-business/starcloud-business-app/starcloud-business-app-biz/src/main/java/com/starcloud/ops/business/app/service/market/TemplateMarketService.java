package com.starcloud.ops.business.app.service.market;

import com.starcloud.ops.business.app.api.market.dto.TemplateMarketDTO;
import com.starcloud.ops.business.app.api.market.request.TemplateMarketPageQuery;
import com.starcloud.ops.business.app.api.market.request.TemplateMarketRequest;
import com.starcloud.ops.business.app.api.market.request.TemplateMarketUpdateRequest;
import com.starcloud.ops.framework.common.api.dto.PageResp;

/**
 * 模版市场服务
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
public interface TemplateMarketService {

    /**
     * 分页查询模版市场列表
     *
     * @param query 查询条件
     * @return 模版市场列表
     */
    PageResp<TemplateMarketDTO> page(TemplateMarketPageQuery query);

    /**
     * 根据模版 ID 获取模版详情
     *
     * @param id 模版 ID
     * @return 模版详情
     */
    TemplateMarketDTO getById(Long id);

    /**
     * 根据模版 uid 获取模版详情
     *
     * @param uid 模版 uid
     * @return 模版详情
     */
    TemplateMarketDTO getByUid(String uid);

    /**
     * 创建模版市场的模版
     *
     * @param request 模版信息
     * @return 是否创建成功
     */
    Boolean create(TemplateMarketRequest request);

    /**
     * 更新模版市场的模版
     *
     * @param request 模版信息
     * @return 是否更新成功
     */
    Boolean modify(TemplateMarketUpdateRequest request);

    /**
     * 删除模版市场的模版
     *
     * @param id 模版 ID
     * @return 是否删除成功
     */
    Boolean delete(Long id);

    /**
     * 删除模版市场的模版
     *
     * @param uid 模版 uid
     * @return 是否删除成功
     */
    Boolean deleteByUid(String uid);
}
