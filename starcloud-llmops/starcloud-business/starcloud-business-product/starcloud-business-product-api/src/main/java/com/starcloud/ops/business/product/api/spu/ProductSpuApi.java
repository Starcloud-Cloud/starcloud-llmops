package com.starcloud.ops.business.product.api.spu;

import com.starcloud.ops.business.product.api.spu.dto.ProductSpuRespDTO;

import java.util.Collection;
import java.util.List;

/**
 * 商品 SPU API 接口
 *
 * @author LeeYan9
 * @since 2022-08-26
 */
public interface ProductSpuApi {

    /**
     * 批量查询 SPU 数组
     *
     * @param ids SPU 编号列表
     * @return SPU 数组
     */
    List<ProductSpuRespDTO> getSpuList(Collection<Long> ids);

    /**
     * 批量查询 SPU 数组，并且校验是否 SPU 是否有效。
     *
     * 如下情况，视为无效：
     * 1. 商品编号不存在
     * 2. 商品被禁用
     *
     * @param ids SPU 编号列表
     * @return SPU 数组
     */
    List<ProductSpuRespDTO> validateSpuList(Collection<Long> ids);

    /**
     * 获得 SPU
     *
     * @return SPU
     */
    ProductSpuRespDTO getSpu(Long id);




    /**
     * 验证商品是否必须含有优惠券下单
     *
     * @param spuId    skuId
     * @param couponId 优惠券 ID
     */
    void validateSpuAndCoupon(Long spuId, Long couponId);


    void validateSpuRegisterLimit(Long userId,Long spuId);

}
