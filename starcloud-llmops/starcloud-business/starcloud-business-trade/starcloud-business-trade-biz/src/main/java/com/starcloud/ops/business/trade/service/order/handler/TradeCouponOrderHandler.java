package com.starcloud.ops.business.trade.service.order.handler;


import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.collection.SetUtils;
import com.starcloud.ops.business.product.api.sku.ProductSkuApi;
import com.starcloud.ops.business.product.api.spu.ProductSpuApi;
import com.starcloud.ops.business.product.api.spu.dto.ProductSpuRespDTO;
import com.starcloud.ops.business.promotion.api.coupon.CouponApi;
import com.starcloud.ops.business.promotion.api.coupon.dto.CouponUseReqDTO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderDO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderItemDO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 优惠劵的 {@link TradeOrderHandler} 实现类
 *
 * @author 芋道源码
 */
@Component
public class TradeCouponOrderHandler implements TradeOrderHandler {

    @Resource
    private CouponApi couponApi;

    @Resource
    private ProductSpuApi productSpuApi;


    @Override
    public void afterOrderCreate(TradeOrderDO order, List<TradeOrderItemDO> orderItems) {
        if (order.getCouponId() == null || order.getCouponId() <= 0) {
            return;
        }
        // 不在前置扣减的原因，是因为优惠劵要记录使用的订单号
        couponApi.useCoupon(new CouponUseReqDTO().setId(order.getCouponId()).setUserId(order.getUserId())
                .setOrderId(order.getId()));
    }

    public void afterPayOrder(TradeOrderDO order, List<TradeOrderItemDO> orderItems) {
       orderItems.stream().forEach(itemDO->{
           ProductSpuRespDTO spu = productSpuApi.getSpu(itemDO.getSpuId());
           if (CollUtil.isNotEmpty(spu.getGiveCouponTemplateIds())){
               List<String> collect = spu.getGiveCouponTemplateIds().stream().map(Object::toString).collect(Collectors.toList());

               collect.forEach(coupon->{
                   couponApi.addCoupon(Long.parseLong(coupon),SetUtils.asSet(itemDO.getUserId()));});
           }
       });
    }

    @Override
    public void afterCancelOrder(TradeOrderDO order, List<TradeOrderItemDO> orderItems) {
        if (order.getCouponId() == null || order.getCouponId() <= 0) {
            return;
        }
        // 退回优惠劵
        couponApi.returnUsedCoupon(order.getCouponId());
    }

}
