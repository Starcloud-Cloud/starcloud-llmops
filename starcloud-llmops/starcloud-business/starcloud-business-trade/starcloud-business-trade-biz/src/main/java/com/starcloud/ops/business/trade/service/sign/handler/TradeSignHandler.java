package com.starcloud.ops.business.trade.service.sign.handler;

import com.starcloud.ops.business.trade.dal.dataobject.sign.TradeSignDO;
import com.starcloud.ops.business.trade.dal.dataobject.sign.TradeSignItemDO;

import java.util.List;

/**
 * 订单活动特殊逻辑处理器 handler 接口
 * 提供订单生命周期钩子接口；订单创建前、订单创建后、订单支付后、订单取消
 *
 * @author HUIHUI
 */
public interface TradeSignHandler {

    /**
     * 订单创建前 商品检测
     *
     * @param tradeSignDO 签约
     * @param signItemDOS 签约项
     */
    default void beforeSignValidate(TradeSignDO tradeSignDO, List<TradeSignItemDO>  signItemDOS) {}


}
