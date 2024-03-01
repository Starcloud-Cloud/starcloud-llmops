package com.starcloud.ops.business.trade.service.order.handler;

import com.starcloud.ops.business.product.api.sku.ProductSkuApi;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderDO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderItemDO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商品 SKU 库存的 {@link TradeOrderHandler} 实现类
 *
 * @author 芋道源码
 */
@Component
public class TradeProductSpuOrderHandler implements TradeOrderHandler {

    @Resource
    private ProductSkuApi productSkuApi;

    @Override
    public void beforeOrderValidate(TradeOrderDO order, List<TradeOrderItemDO> orderItems) {


        // 用户与商品检验
        // 1. 新用户检验
        // 2. 特定优惠券检验
        orderItems.forEach( orderItem->
                productSkuApi.canPlaceOrder(order.getUserId(),orderItem.getSkuId()));

    }
}
