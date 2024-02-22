package com.starcloud.ops.business.trade.service.rights;


import com.starcloud.ops.business.product.api.sku.ProductSkuApi;
import com.starcloud.ops.business.product.api.sku.dto.ProductSkuRespDTO;
import com.starcloud.ops.business.product.api.spu.ProductSpuApi;
import com.starcloud.ops.business.product.api.spu.dto.ProductSpuRespDTO;
import com.starcloud.ops.business.product.api.spu.dto.SubscribeConfigDTO;
import com.starcloud.ops.business.trade.service.price.bo.TradePriceCalculateReqBO;
import com.starcloud.ops.business.trade.service.rights.bo.TradeRightsCalculateRespBO;
import com.starcloud.ops.business.user.api.rights.dto.AdminUserRightsCommonDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static com.starcloud.ops.business.product.enums.ErrorCodeConstants.SKU_NOT_EXISTS;
import static com.starcloud.ops.business.product.enums.ErrorCodeConstants.SKU_STOCK_NOT_ENOUGH;

/**
 * 价格计算 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class TradeRightsServiceImpl implements TradeRightsService {

    @Resource
    private ProductSkuApi productSkuApi;
    @Resource
    private ProductSpuApi productSpuApi;

    @Override
    public TradeRightsCalculateRespBO calculateRights(TradePriceCalculateReqBO calculateReqBO) {
        // 1.1 获得商品 SKU 数组
        List<ProductSkuRespDTO> skuList = checkSkuList(calculateReqBO);
        // 1.2 获得商品 SPU 数组
        List<ProductSpuRespDTO> spuList = checkSpuList(skuList);

        // 2.0 设置权益相关字段
        TradeRightsCalculateRespBO calculateRespBO = new TradeRightsCalculateRespBO();
         List<AdminUserRightsCommonDTO> giveRights = new ArrayList<>();
        for (ProductSpuRespDTO productSpuRespDTO : spuList) {
            giveRights.add(productSpuRespDTO.getGiveRights());
        }
        calculateRespBO.setGiveRights(giveRights);
        return calculateRespBO;
    }

    /**
     * 价格计算
     *
     * @param calculateReqBO 计算信息
     * @return 计算结果
     */
    @Override
    public SubscribeConfigDTO calculateSignConfigs(TradePriceCalculateReqBO calculateReqBO) {
        // 1.1 获得商品 SKU 数组
        List<ProductSkuRespDTO> skuList = checkSkuList(calculateReqBO);
        // 1.2 获得商品 SPU 数组
        List<ProductSpuRespDTO> spuList = checkSpuList(skuList);

        // 2.0 设置权益相关字段
        SubscribeConfigDTO subscribeConfigDTO = new SubscribeConfigDTO();
        subscribeConfigDTO =spuList.get(0).getSubscribeConfig() ;
        return subscribeConfigDTO;
    }

    private List<ProductSkuRespDTO> checkSkuList(TradePriceCalculateReqBO reqBO) {
        // 获得商品 SKU 数组
        Map<Long, Integer> skuIdCountMap = convertMap(reqBO.getItems(),
                TradePriceCalculateReqBO.Item::getSkuId, TradePriceCalculateReqBO.Item::getCount);
        List<ProductSkuRespDTO> skus = productSkuApi.getSkuList(skuIdCountMap.keySet());

        // 校验商品 SKU
        skus.forEach(sku -> {
            Integer count = skuIdCountMap.get(sku.getId());
            if (count == null) {
                throw exception(SKU_NOT_EXISTS);
            }
            if (count > sku.getStock()) {
                throw exception(SKU_STOCK_NOT_ENOUGH);
            }
        });
        return skus;
    }

    private List<ProductSpuRespDTO> checkSpuList(List<ProductSkuRespDTO> skuList) {
        // 获得商品 SPU 数组
        return productSpuApi.validateSpuList(convertSet(skuList, ProductSkuRespDTO::getSpuId));
    }

}
