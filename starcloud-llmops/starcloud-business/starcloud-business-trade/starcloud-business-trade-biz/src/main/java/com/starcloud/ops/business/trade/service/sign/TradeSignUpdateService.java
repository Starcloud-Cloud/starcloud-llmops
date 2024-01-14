package com.starcloud.ops.business.trade.service.sign;

import cn.iocoder.yudao.framework.common.enums.TerminalEnum;
import com.starcloud.ops.business.trade.controller.admin.order.vo.TradeOrderDeliveryReqVO;
import com.starcloud.ops.business.trade.controller.admin.order.vo.TradeOrderRemarkReqVO;
import com.starcloud.ops.business.trade.controller.admin.order.vo.TradeOrderUpdateAddressReqVO;
import com.starcloud.ops.business.trade.controller.admin.order.vo.TradeOrderUpdatePriceReqVO;
import com.starcloud.ops.business.trade.controller.admin.sign.vo.AppTradeSignCreateReqVO;
import com.starcloud.ops.business.trade.controller.admin.sign.vo.AppTradeSignSettlementReqVO;
import com.starcloud.ops.business.trade.controller.app.order.vo.AppTradeOrderCreateReqVO;
import com.starcloud.ops.business.trade.controller.app.order.vo.AppTradeOrderSettlementReqVO;
import com.starcloud.ops.business.trade.controller.app.order.vo.AppTradeOrderSettlementRespVO;
import com.starcloud.ops.business.trade.controller.app.order.vo.item.AppTradeOrderItemCommentCreateReqVO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderDO;
import com.starcloud.ops.business.trade.dal.dataobject.sign.TradeSignDO;

import javax.validation.constraints.NotNull;

/**
 * 交易订单【写】Service 接口
 *
 * @author LeeYan9
 * @since 2022-08-26
 */
public interface TradeSignUpdateService {

    // =================== Order ===================

    /**
     * 获得订单结算信息
     *
     * @param userId          登录用户
     * @param settlementReqVO 订单结算请求
     * @return 订单结算结果
     */
    AppTradeOrderSettlementRespVO settlementSign(Long userId, AppTradeSignSettlementReqVO settlementReqVO);

    /**
     * 【会员】创建交易订单
     *
     * @param userId      登录用户
     * @param userIp      用户 IP 地址
     * @param createReqVO 创建交易订单请求模型
     * @param terminal    终端 {@link TerminalEnum}
     * @return 交易订单的
     */
    TradeSignDO createSign(Long userId, String userIp, AppTradeSignCreateReqVO createReqVO, Integer terminal);

    /**
     * 更新交易订单已支付
     *
     * @param id         交易订单编号
     * @param payOrderId 支付订单编号
     */
    void updateOrderPaid(Long id, Long payOrderId);


}
