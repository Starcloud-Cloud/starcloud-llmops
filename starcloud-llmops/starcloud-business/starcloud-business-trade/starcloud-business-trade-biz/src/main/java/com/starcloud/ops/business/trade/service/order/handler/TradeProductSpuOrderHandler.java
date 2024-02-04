package com.starcloud.ops.business.trade.service.order.handler;

import com.starcloud.ops.business.product.api.spu.ProductSpuApi;
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
    private ProductSpuApi productSpuApi;

    @Override
    public void beforeOrderValidate(TradeOrderDO order, List<TradeOrderItemDO> orderItems) {
        // 新用户检测
        orderItems.forEach( orderItem->
                productSpuApi.validateSpuRegisterLimit(order.getUserId(),orderItem.getSpuId()));

        // 下单检测
        orderItems.forEach( orderItem->
               productSpuApi.validateSpuAndCoupon(orderItem.getSpuId(),order.getCouponId(),order.getUserId()));


    }
}
