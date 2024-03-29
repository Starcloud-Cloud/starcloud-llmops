package com.starcloud.ops.business.product.api.sku;

import com.starcloud.ops.business.product.api.sku.dto.ProductSkuRespDTO;
import com.starcloud.ops.business.product.api.sku.dto.ProductSkuUpdateStockReqDTO;
import com.starcloud.ops.business.product.convert.sku.ProductSkuConvert;
import com.starcloud.ops.business.product.dal.dataobject.sku.ProductSkuDO;
import com.starcloud.ops.business.product.service.sku.ProductSkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 商品 SKU API 实现类
 *
 * @author LeeYan9
 * @since 2022-09-06
 */
@Slf4j
@Service
@Validated
public class ProductSkuApiImpl implements ProductSkuApi {

    @Resource
    private ProductSkuService productSkuService;

    @Override
    public ProductSkuRespDTO getSku(Long id) {
        ProductSkuDO sku = productSkuService.getSku(id);
        return ProductSkuConvert.INSTANCE.convert02(sku);
    }

    @Override
    public List<ProductSkuRespDTO> getSkuList(Collection<Long> ids) {
        List<ProductSkuDO> skus = productSkuService.getSkuList(ids);
        return ProductSkuConvert.INSTANCE.convertList04(skus);
    }

    @Override
    public List<ProductSkuRespDTO> getSkuListBySpuId(Collection<Long> spuIds) {
        List<ProductSkuDO> skus = productSkuService.getSkuListBySpuId(spuIds);
        return ProductSkuConvert.INSTANCE.convertList04(skus);
    }

    @Override
    public void updateSkuStock(ProductSkuUpdateStockReqDTO updateStockReqDTO) {
        productSkuService.updateSkuStock(updateStockReqDTO);
    }

    /**
     * 验证订单是否可以下单
     *
     * @param userId 用户编号
     * @param skuId  SKU 编号
     */
    @Override
    public void canPlaceOrder(Long userId, Long skuId) {
        productSkuService.canPlaceOrder(userId, skuId);
    }

    /**
     * 验证商品是否支持签约 @Link 针对 ProductSkuDO.getSubscribeConfig() 进行验证
     *
     * @param skuId SKU 编号
     */
    @Override
    public void isValidSubscriptionSupported(Long skuId) {
        productSkuService.isValidSubscriptionSupported(skuId);
    }


}
