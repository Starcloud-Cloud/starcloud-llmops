package com.starcloud.ops.business.app.service.market;

import com.starcloud.ops.business.app.api.favorite.vo.response.AppFavoriteRespVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppInstallReqVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketAuditReqVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketPageQuery;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketReqVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketUpdateReqVO;
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
     * 分页查询应用市场列表
     *
     * @param query 查询条件
     * @return 应用市场列表
     */
    PageResp<AppMarketRespVO> page(AppMarketPageQuery query, boolean isAdmin);

    /**
     * 获取应用详情
     *
     * @param uid     应用 uid
     * @param version 应用版本号, 非必填
     * @return 应用详情
     */
    AppMarketRespVO get(String uid, Integer version);

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
     * @param uid     应用 uid
     * @param version 应用版本号
     */
    void deleteByUidAndVersion(String uid, Integer version);

    /**
     * 下载安装应用
     *
     * @param request 安装请求
     */
    void install(AppInstallReqVO request);

    /**
     * 审核应用
     *
     * @param request 审核请求
     */
    void audit(AppMarketAuditReqVO request);

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
