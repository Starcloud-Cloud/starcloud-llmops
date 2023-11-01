package com.starcloud.ops.business.app.service.market;

import com.starcloud.ops.business.app.api.market.vo.request.*;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketGroupCategoryRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.operate.request.AppOperateReqVO;
import com.starcloud.ops.framework.common.api.dto.PageResp;

import java.util.List;

/**
 * 应用市场服务
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
public interface AppMarketService {

    /**
     * 获取应用详情, 根据 ID 进行查询
     *
     * @param id 应用 ID
     * @return 应用详情
     */
    AppMarketRespVO get(Long id);

    /**
     * 获取应用详情
     *
     * @param uid 应用 uid
     * @return 应用详情
     */
    AppMarketRespVO get(String uid);

    /**
     * 根据条件查询应用市场列表
     *
     * @param query 查询条件
     * @return 应用市场列表
     */
    List<AppMarketRespVO> list(AppMarketListQuery query);

    /**
     * 根据分类Code查询应用市场列表
     *
     * @return 分组列表
     */
    List<AppMarketGroupCategoryRespVO> listGroupByCategory(AppMarketListGroupByCategoryQuery query);

    /**
     * 分页查询应用市场列表
     *
     * @param query 查询条件
     * @return 应用市场列表
     */
    PageResp<AppMarketRespVO> page(AppMarketPageQuery query);

    /**
     * 创建应用市场的应用
     *
     * @param request 应用信息
     */
    void create(AppMarketReqVO request);

    /**
     * 更新应用市场的应用
     *
     * @param request 应用信息
     */
    void modify(AppMarketUpdateReqVO request);

    /**
     * 删除应用市场的应用
     *
     * @param uid 应用 uid
     */
    void delete(String uid);

    /**
     * 应用操作
     *
     * @param request 操作请求
     */
    void operate(AppOperateReqVO request);
}
