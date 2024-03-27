package com.starcloud.ops.business.trade.service.sign.handler;

import com.starcloud.ops.business.product.api.sku.ProductSkuApi;
import com.starcloud.ops.business.trade.dal.dataobject.sign.TradeSignDO;
import com.starcloud.ops.business.trade.dal.dataobject.sign.TradeSignItemDO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商品 SKU 是否支持签约校验的 {@link TradeSignHandler} 实现类
 *
 * @author 芋道源码
 */
@Component
public class TradeProductSkuSignHandler implements TradeSignHandler {

    @Resource
    private ProductSkuApi productSkuApi;

    @Override
    public void beforeSignValidate(TradeSignDO tradeSignDO, List<TradeSignItemDO>  signItems) {

        // 验证商品是否支持签约单
        signItems.forEach( signItem->
                productSkuApi.isValidSubscriptionSupported(signItem.getSkuId()));

    }
}
