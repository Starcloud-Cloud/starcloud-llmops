package com.starcloud.ops.business.order.service.order.handler;

import org.springframework.stereotype.Component;

/**
 * 优惠劵的 {@link TradeOrderHandler} 实现类
 *
 * @author 芋道源码
 */
@Component
@Deprecated
public class TradeCouponOrderHandler implements TradeOrderHandler {

//    @Resource
//    private CouponApi couponApi;
//
//    @Override
//    public void afterOrderCreate(TradeOrderDO order, List<TradeOrderItemDO> orderItems) {
//        if (order.getCouponId() == null || order.getCouponId() <= 0) {
//            return;
//        }
//        // 不在前置扣减的原因，是因为优惠劵要记录使用的订单号
//        couponApi.useCoupon(new CouponUseReqDTO().setId(order.getCouponId()).setUserId(order.getUserId())
//                .setOrderId(order.getId()));
//    }
//
//    @Override
//    public void afterCancelOrder(TradeOrderDO order, List<TradeOrderItemDO> orderItems) {
//        if (order.getCouponId() == null || order.getCouponId() <= 0) {
//            return;
//        }
//        // 退回优惠劵
//        couponApi.returnUsedCoupon(order.getCouponId());
//    }

}
