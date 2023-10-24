package com.starcloud.ops.business.app.service.favorite;

import com.starcloud.ops.business.app.api.favorite.vo.query.AppFavoriteListReqVO;
import com.starcloud.ops.business.app.api.favorite.vo.query.AppFavoritePageReqVO;
import com.starcloud.ops.business.app.api.favorite.vo.request.AppFavoriteCancelReqVO;
import com.starcloud.ops.business.app.api.favorite.vo.request.AppFavoriteCreateReqVO;
import com.starcloud.ops.business.app.api.favorite.vo.response.AppFavoriteRespVO;
import com.starcloud.ops.framework.common.api.dto.PageResp;

import java.util.List;

/**
 * 应用收藏服务
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-10-24
 */
public interface AppFavoriteService {

    /**
     * 获取用户收藏的应用的详情
     *
     * @param uid 收藏 UID
     * @return 收藏应用
     */
    AppFavoriteRespVO getMarketInfo(String uid);

    /**
     * 应用市场应用收藏列表
     *
     * @param query 搜索条件
     * @return 收藏列表
     */
    List<AppFavoriteRespVO> list(AppFavoriteListReqVO query);

    /**
     * 应用市场应用收藏分页列表
     *
     * @param query 搜索条件
     * @return 收藏分页列表
     */
    PageResp<AppFavoriteRespVO> page(AppFavoritePageReqVO query);

    /**
     * 将应用加入到收藏夹
     *
     * @param request 请求参数
     */
    void create(AppFavoriteCreateReqVO request);

    /**
     * 取消收藏
     *
     * @param request 请求参数
     */
    void cancel(AppFavoriteCancelReqVO request);

    /**
     * 删除收藏的应用
     *
     * @param uid 应用市场Uid
     */
    void delete(String uid);

}
