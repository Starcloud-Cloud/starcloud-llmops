package com.starcloud.ops.business.trade.service.order.handler;


import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.collection.SetUtils;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.product.api.spu.ProductSpuApi;
import com.starcloud.ops.business.product.api.spu.dto.ProductSpuRespDTO;
import com.starcloud.ops.business.promotion.api.coupon.CouponApi;
import com.starcloud.ops.business.promotion.api.coupon.dto.CouponUseReqDTO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderDO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderItemDO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

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
               List<Long> ids1 = JSON.parseArray(JSON.toJSONString(spu.getGiveCouponTemplateIds()), Long.class);
               ids1.forEach(coupon->  couponApi.addCoupon(coupon,SetUtils.asSet(itemDO.getUserId())));
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
