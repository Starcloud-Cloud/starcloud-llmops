package com.starcloud.ops.business.product.service.favorite;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.product.controller.app.favorite.vo.AppFavoritePageReqVO;
import com.starcloud.ops.business.product.dal.dataobject.favorite.ProductFavoriteDO;

import javax.validation.Valid;

/**
 * 商品收藏 Service 接口
 *
 * @author jason
 */
public interface ProductFavoriteService {

    /**
     * 创建商品收藏
     *
     * @param userId 用户编号
     * @param spuId SPU 编号
     */
    Long createFavorite(Long userId, Long spuId);

    /**
     * 取消商品收藏
     *
     * @param userId 用户编号
     * @param spuId SPU 编号
     */
    void deleteFavorite(Long userId, Long spuId);

    /**
     * 分页查询用户收藏列表
     *
     * @param userId 用户编号
     * @param reqVO 请求 vo
     */
    PageResult<ProductFavoriteDO> getFavoritePage(Long userId, @Valid AppFavoritePageReqVO reqVO);

    /**
     * 获取收藏过商品
     *
     * @param userId 用户编号
     * @param spuId SPU 编号
     */
    ProductFavoriteDO getFavorite(Long userId, Long spuId);

    /**
     * 获取用户收藏数量
     *
     * @param userId 用户编号
     * @return 数量
     */
    Long getFavoriteCount(Long userId);

}