package com.starcloud.ops.business.app.service.market;

import com.starcloud.ops.business.app.api.market.vo.request.AppMarketListGroupByCategoryQuery;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketListQuery;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketOptionListQuery;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketPageQuery;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketQuery;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketReqVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketUpdateReqVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketGroupCategoryRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.operate.request.AppOperateReqVO;
import com.starcloud.ops.framework.common.api.dto.Option;
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
     * 获取应用详情
     *
     * @param uid 应用 uid
     * @return 应用详情
     */
    AppMarketRespVO get(String uid);

    /**
     * 获取应用详情
     *
     * @param query 查询条件
     * @return 应用详情
     */
    AppMarketRespVO getOne(AppMarketQuery query);

    /**
     * 获取应用详情并且增加查看量增加
     *
     * @param uid 应用 UID
     * @return 应用详情
     */
    AppMarketRespVO getAndIncreaseView(String uid);

    /**
     * 根据条件查询应用市场列表
     *
     * @param query 查询条件
     * @return 应用市场列表
     */
    List<AppMarketRespVO> list(AppMarketListQuery query);

    /**
     * 根据条件查询应用市场列表 Option
     *
     * @param query 查询条件
     * @return 应用市场列表 Option
     */
    List<Option> listOption(AppMarketOptionListQuery query);

    /**
     * 根据分类Code查询应用市场列表
     *
     * @return 分组列表
     */
    List<AppMarketGroupCategoryRespVO> listGroupByCategory(AppMarketListGroupByCategoryQuery query);

    /**
     * 根据分类Code查询应用市场列表
     *
     * @param query 查询条件
     * @return 分组列表
     */
    List<AppMarketGroupCategoryRespVO> listGroupTemplateByCategory(AppMarketListGroupByCategoryQuery query);

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
     * 创建应用市场的应用
     *
     * @param appMarketUid 应用信息
     */
    String createSameApp(String appMarketUid);

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
