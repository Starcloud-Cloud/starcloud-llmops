package com.starcloud.ops.business.app.service;

import com.starcloud.ops.business.app.api.market.dto.AppMarketDTO;
import com.starcloud.ops.business.app.api.market.request.AppMarketPageQuery;
import com.starcloud.ops.business.app.api.market.request.AppMarketRequest;
import com.starcloud.ops.business.app.api.market.request.AppMarketUpdateRequest;
import com.starcloud.ops.business.app.api.operate.request.AppOperateRequest;
import com.starcloud.ops.framework.common.api.dto.PageResp;

/**
 * 模版市场服务
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
public interface AppMarketService {

    /**
     * 分页查询模版市场列表
     *
     * @param query 查询条件
     * @return 模版市场列表
     */
    PageResp<AppMarketDTO> page(AppMarketPageQuery query);

    /**
     * 根据模版 ID 获取模版详情
     *
     * @param id 模版 ID
     * @return 模版详情
     */
    AppMarketDTO getById(Long id);

    /**
     * 根据模版 uid 获取模版详情
     *
     * @param uid 模版 uid
     * @return 模版详情
     */
    AppMarketDTO getByUid(String uid);

    /**
     * 创建模版市场的模版
     *
     * @param request 模版信息
     * @return 是否创建成功
     */
    Boolean create(AppMarketRequest request);

    /**
     * 更新模版市场的模版
     *
     * @param request 模版信息
     * @return 是否更新成功
     */
    Boolean modify(AppMarketUpdateRequest request);

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

    /**
     * 模版操作
     *
     * @param request 操作请求
     * @return 是否操作成功
     */
    Boolean operate(AppOperateRequest request);
}
