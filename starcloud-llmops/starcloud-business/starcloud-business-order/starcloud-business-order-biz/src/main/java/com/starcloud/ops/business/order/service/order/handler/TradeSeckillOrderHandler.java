package com.starcloud.ops.business.order.service.order.handler;

import org.springframework.stereotype.Component;

/**
 * 秒杀订单的 {@link TradeOrderHandler} 实现类
 *
 * @author HUIHUI
 */
@Component
@Deprecated
public class TradeSeckillOrderHandler implements TradeOrderHandler {

//    @Resource
//    private SeckillActivityApi seckillActivityApi;
//
//    @Override
//    public void beforeOrderCreate(TradeOrderDO order, List<TradeOrderItemDO> orderItems) {
//        if (!TradeOrderTypeEnum.isSeckill(order.getType())) {
//            return;
//        }
//        // 明确校验一下
//        Assert.isTrue(orderItems.size() == 1, "秒杀时，只允许选择一个商品");
//
//        // 扣减秒杀活动的库存
//        seckillActivityApi.updateSeckillStockDecr(order.getSeckillActivityId(),
//                orderItems.get(0).getSkuId(), orderItems.get(0).getCount());
//    }
//
//    @Override
//    public void afterCancelOrder(TradeOrderDO order, List<TradeOrderItemDO> orderItems) {
//        if (!TradeOrderTypeEnum.isSeckill(order.getType())) {
//            return;
//        }
//        // 明确校验一下
//        Assert.isTrue(orderItems.size() == 1, "秒杀时，只允许选择一个商品");
//
//        // 售后的订单项，已经在 afterCancelOrderItem 回滚库存，所以这里不需要重复回滚
//        orderItems = filterOrderItemListByNoneAfterSale(orderItems);
//        if (CollUtil.isEmpty(orderItems)) {
//            return;
//        }
//        afterCancelOrderItem(order, orderItems.get(0));
//    }
//
//    @Override
//    public void afterCancelOrderItem(TradeOrderDO order, TradeOrderItemDO orderItem) {
//        if (!TradeOrderTypeEnum.isSeckill(order.getType())) {
//            return;
//        }
//        // 恢复秒杀活动的库存
//        seckillActivityApi.updateSeckillStockIncr(order.getSeckillActivityId(),
//                orderItem.getSkuId(), orderItem.getCount());
//    }

}