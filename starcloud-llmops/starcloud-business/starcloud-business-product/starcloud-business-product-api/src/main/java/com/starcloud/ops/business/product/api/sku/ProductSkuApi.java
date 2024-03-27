package com.starcloud.ops.business.product.api.sku;

import com.starcloud.ops.business.product.api.sku.dto.ProductSkuRespDTO;
import com.starcloud.ops.business.product.api.sku.dto.ProductSkuUpdateStockReqDTO;

import java.util.Collection;
import java.util.List;

/**
 * 商品 SKU API 接口
 *
 * @author LeeYan9
 * @since 2022-08-26
 */
public interface ProductSkuApi {

    /**
     * 查询 SKU 信息
     *
     * @param id SKU 编号
     * @return SKU 信息
     */
    ProductSkuRespDTO getSku(Long id);

    /**
     * 批量查询 SKU 数组
     *
     * @param ids SKU 编号列表
     * @return SKU 数组
     */
    List<ProductSkuRespDTO> getSkuList(Collection<Long> ids);

    /**
     * 批量查询 SKU 数组
     *
     * @param spuIds SPU 编号列表
     * @return SKU 数组
     */
    List<ProductSkuRespDTO> getSkuListBySpuId(Collection<Long> spuIds);

    /**
     * 更新 SKU 库存（增加 or 减少）
     *
     * @param updateStockReqDTO 更新请求
     */
    void updateSkuStock(ProductSkuUpdateStockReqDTO updateStockReqDTO);

    /**
     * 验证订单是否可以下单 @针对 ProductSkuDO.getOrderLimitConfig() 进行验证
     *
     * @param userId 用户编号
     * @param skuId  SKU 编号
     */
    void canPlaceOrder(Long userId, Long skuId);

    /**
     * 验证商品是否支持签约单 @Link 针对 ProductSkuDO.getSubscribeConfig() 进行验证
     *
     * @param skuId  SKU 编号
     */
    void isValidSubscriptionSupported(Long skuId);



}
