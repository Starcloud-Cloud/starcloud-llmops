package com.starcloud.ops.business.trade.service.order.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;


import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.starcloud.ops.business.product.api.sku.ProductSkuApi;
import com.starcloud.ops.business.product.api.sku.dto.ProductSkuRespDTO;
import com.starcloud.ops.business.product.api.spu.ProductSpuApi;
import com.starcloud.ops.business.product.api.spu.dto.ProductSpuRespDTO;
import com.starcloud.ops.business.trade.convert.order.TradeOrderConvert;
import com.starcloud.ops.business.trade.dal.dataobject.brokerage.BrokerageUserDO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderDO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderItemDO;
import com.starcloud.ops.business.trade.enums.brokerage.BrokerageRecordBizTypeEnum;
import com.starcloud.ops.business.trade.service.brokerage.BrokerageRecordService;
import com.starcloud.ops.business.trade.service.brokerage.BrokerageUserService;
import com.starcloud.ops.business.trade.service.brokerage.bo.BrokerageAddReqBO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;

/**
 * 订单分销的 {@link TradeOrderHandler} 实现类
 *
 * @author 芋道源码
 */
@Component
public class TradeBrokerageOrderHandler implements TradeOrderHandler {

    @Resource
    private AdminUserService adminUserService;
    @Resource
    private ProductSpuApi productSpuApi;
    @Resource
    private ProductSkuApi productSkuApi;

    @Resource
    private BrokerageRecordService brokerageRecordService;
    @Resource
    private BrokerageUserService brokerageUserService;

    @Override
    public void beforeOrderCreate(TradeOrderDO order, List<TradeOrderItemDO> orderItems) {
        // 设置订单推广人
        BrokerageUserDO brokerageUser = brokerageUserService.getBrokerageUser(order.getUserId());
        if (brokerageUser != null && brokerageUser.getBindUserId() != null) {
            order.setBrokerageUserId(brokerageUser.getBindUserId());
        }
    }

    @Override
    public void afterPayOrder(TradeOrderDO order, List<TradeOrderItemDO> orderItems) {
        if (order.getBrokerageUserId() == null) {
            return;
        }
        addBrokerage(order.getUserId(), orderItems);
    }

    @Override
    public void afterCancelOrder(TradeOrderDO order, List<TradeOrderItemDO> orderItems) {
        // 如果是未支付的订单，不会产生分销结果，所以直接 return
        if (!order.getPayStatus()) {
            return;
        }
        if (order.getBrokerageUserId() == null) {
            return;
        }

        // 售后的订单项，已经在 afterCancelOrderItem 回滚库存，所以这里不需要重复回滚
        orderItems = filterOrderItemListByNoneAfterSale(orderItems);
        if (CollUtil.isEmpty(orderItems)) {
            return;
        }
        orderItems.forEach(orderItem -> afterCancelOrderItem(order, orderItem));
    }

    @Override
    public void afterCancelOrderItem(TradeOrderDO order, TradeOrderItemDO orderItem) {
        if (order.getBrokerageUserId() == null) {
            return;
        }
        cancelBrokerage(order.getId(), orderItem.getOrderId());
    }

    /**
     * 创建分销记录
     * <p>
     * 目前是支付成功后，就会创建分销记录。
     * <p>
     * 业内还有两种做法，可以根据自己的业务调整：
     * 1. 确认收货后，才创建分销记录
     * 2. 支付 or 下单成功时，创建分销记录（冻结），确认收货解冻或者 n 天后解冻
     *
     * @param userId  用户编号
     * @param orderItems 订单项
     */
    protected void addBrokerage(Long userId, List<TradeOrderItemDO> orderItems) {
        AdminUserDO user = adminUserService.getUser(userId);
        Assert.notNull(user);
        ProductSpuRespDTO spu = productSpuApi.getSpu(orderItems.get(0).getSpuId());
        Assert.notNull(spu);
        ProductSkuRespDTO sku = productSkuApi.getSku(orderItems.get(0).getSkuId());

        // 每一个订单项，都会去生成分销记录
        List<BrokerageAddReqBO> addList = convertList(orderItems,
                item -> TradeOrderConvert.INSTANCE.convert(user, item, spu, sku));
        brokerageRecordService.addBrokerage(userId, BrokerageRecordBizTypeEnum.ORDER, addList);
    }

    protected void cancelBrokerage(Long userId, Long orderItemId) {
        brokerageRecordService.cancelBrokerage(userId, BrokerageRecordBizTypeEnum.ORDER, String.valueOf(orderItemId));
    }

}