package com.starcloud.ops.business.trade.api.order;

import com.starcloud.ops.business.trade.api.order.dto.TradeOrderRespDTO;

import java.util.Collection;
import java.util.List;

/**
 * 订单 API 接口
 *
 * @author HUIHUI
 */
public interface TradeOrderApi {

    /**
     * 获得订单列表
     *
     * @param ids 订单编号数组
     * @return 订单列表
     */
    List<TradeOrderRespDTO> getOrderList(Collection<Long> ids);

    /**
     * 获得订单
     *
     * @param id 订单编号
     * @return 订单
     */
    TradeOrderRespDTO getOrder(Long id);

    // TODO 芋艿：需要优化下；

    /**
     * 取消支付订单
     *
     * @param userId  用户编号
     * @param orderId 订单编号
     */
    void cancelPaidOrder(Long userId, Long orderId);

    /**
     * 获取成功下单数量
     *
     * @param userId 用户编号
     * @return 成功下单数量
     */
    Long getSuccessOrderCount(Long userId);

}
