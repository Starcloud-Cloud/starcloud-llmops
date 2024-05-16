package com.starcloud.ops.business.trade.service.sign;

import com.starcloud.ops.business.trade.dal.dataobject.sign.TradeSignDO;

/**
 * 交易订单【读】 Service 接口
 *
 * @author 芋道源码
 */
public interface TradeSignQueryService {

    // =================== Order ===================

    /**
     * 获得指定编号的交易订单
     *
     * @param id 交易订单编号
     * @return 交易订单
     */
    TradeSignDO getSign(Long id);

    /**
     * 获得指定用户，指定的交易订单
     *
     * @param userId 用户编号
     * @param id     交易订单编号
     * @return 交易订单
     */
    TradeSignDO getSign(Long userId, Long id);

    /**
     * 自动执行签约扣款
     *
     * @return 执行数量
     */
    int executeAutoTradeSignPay();

    /**
     * 获取全量签约订单 并且发送钉钉通知
     * @return 签约单数量
     */
    int signAutoNotify();
}
