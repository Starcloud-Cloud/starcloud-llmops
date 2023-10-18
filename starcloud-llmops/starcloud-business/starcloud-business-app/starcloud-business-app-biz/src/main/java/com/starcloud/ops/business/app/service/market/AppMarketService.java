package com.starcloud.ops.business.app.service.market;

import com.starcloud.ops.business.app.api.favorite.vo.response.AppFavoriteRespVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketListGroupByCategoryQuery;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketListQuery;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketPageQuery;
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
     * 分页查询应用市场列表
     *
     * @param query 查询条件
     * @return 应用市场列表
     */
    PageResp<AppMarketRespVO> page(AppMarketPageQuery query);

    /**
     * 根据分类Code查询应用市场列表
     *
     * @return 分组列表
     */
    List<AppMarketGroupCategoryRespVO> listGroupByCategory(AppMarketListGroupByCategoryQuery query);

    /**
     * 获取优化提示应用列表
     *
     * @param query 查询条件
     * @return 应用列表
     */
    List<Option> listMarketAppOption(AppMarketListQuery query);

    /**
     * 获取应用详情
     *
     * @param uid 应用 uid
     * @return 应用详情
     */
    AppMarketRespVO get(String uid);

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

    /**
     * 应用市场应用收藏列表
     *
     * @param userId 用户 uid
     * @return 收藏列表
     */
    List<AppFavoriteRespVO> listFavorite(String userId);

    /**
     * 获取用户收藏的应用的详情
     *
     * @param userId 用户 id
     * @param uid    应用 uid
     * @return 收藏应用
     */
    AppFavoriteRespVO getFavoriteApp(String userId, String uid);

    /**
     * 将应用加入到收藏夹
     *
     * @param userId 用户 id
     * @param uid    应用 uid
     */
    void favorite(String userId, String uid);

    /**
     * 取消收藏
     *
     * @param userId 用户 id
     * @param uid    应用 uid
     */
    void cancelFavorite(String userId, String uid);
}
