package com.starcloud.ops.business.trade.service.order.handler;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.system.api.sms.SmsSendApi;
import cn.iocoder.yudao.module.system.api.sms.dto.send.SmsSendSingleToUserReqDTO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderDO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderItemDO;
import com.starcloud.ops.business.trade.service.order.TradeOrderQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 会员积分、等级的 {@link TradeOrderHandler} 实现类
 *
 * @author owen
 */
@Slf4j
@Component
public class TradeNotifyOrderHandler implements TradeOrderHandler {
    @Resource
    private SmsSendApi smsSendApi;

    @Resource
    private TradeOrderQueryService tradeOrderQueryService;

    @Override
    public void afterPayOrderLast(TradeOrderDO tradeOrderDO, List<TradeOrderItemDO> orderItems) {

        try {
            Map<String, Object> templateParams = tradeOrderQueryService.buildTradeNotifyMsg(tradeOrderDO, orderItems);
            log.info("【准备发送订单通知消息，消息参数为:{}】", templateParams);
            // 发送消息通知
            smsSendApi.sendSingleSmsToAdmin(new SmsSendSingleToUserReqDTO().setUserId(1L).setMobile("17835411844").setTemplateCode("DING_TALK_PAY_NOTIFY_01").setTemplateParams(templateParams));
        } catch (RuntimeException e) {
            log.error("【发送订单通知消息失】,错误原因为 errMsg{},当前订单为{}】", e.getMessage(), JSONUtil.toJsonStr(tradeOrderDO), e);
        }
    }

}
