package com.starcloud.ops.business.app.service.market;

import com.starcloud.ops.business.app.api.market.dto.AppMarketDTO;
import com.starcloud.ops.business.app.api.market.request.AppMarketAuditRequest;
import com.starcloud.ops.business.app.api.market.request.AppMarketPageQuery;
import com.starcloud.ops.business.app.api.market.request.AppMarketRequest;
import com.starcloud.ops.business.app.api.market.request.AppMarketUidVersionRequest;
import com.starcloud.ops.business.app.api.market.request.AppMarketUpdateRequest;
import com.starcloud.ops.business.app.api.operate.request.AppOperateRequest;
import com.starcloud.ops.framework.common.api.dto.PageResp;

/**
 * 应用市场服务
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
public interface AppMarketService {

    /**
     * 分页查询应用市场列表
     *
     * @param query 查询条件
     * @return 应用市场列表
     */
    PageResp<AppMarketDTO> page(AppMarketPageQuery query);

    /**
     * 根据应用 uid 和 版本号 获取应用详情
     *
     * @param uid     应用 uid
     * @param version 应用版本号
     * @return 应用详情
     */
    AppMarketDTO getByUid(String uid, Integer version);

    /**
     * 创建应用市场的应用
     *
     * @param request 应用信息
     */
    void create(AppMarketRequest request);

    /**
     * 更新应用市场的应用
     *
     * @param request 应用信息
     */
    void modify(AppMarketUpdateRequest request);

    /**
     * 删除应用市场的应用
     *
     * @param uid     应用 uid
     * @param version 应用版本号
     */
    void deleteByUid(String uid, Integer version);

    /**
     * 下载安装应用
     *
     * @param request 安装请求
     */
    void install(AppMarketUidVersionRequest request);

    /**
     * 审核应用
     *
     * @param request 审核请求
     */
    void audit(AppMarketAuditRequest request);

    /**
     * 应用操作
     *
     * @param request 操作请求
     */
    void operate(AppOperateRequest request);
}
