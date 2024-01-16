package com.starcloud.ops.business.trade.job.sign;

import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.framework.tenant.core.job.TenantJob;
import com.starcloud.ops.business.trade.service.sign.TradeSignQueryService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 交易订单的自动收货 Job
 *
 * @author 芋道源码
 */
@Component
public class TradeSignAutoPayJob implements JobHandler {

    @Resource
    private TradeSignQueryService tradeSignQueryService;

    @Override
    @TenantJob
    public String execute(String param) {

        int count = tradeSignQueryService.executeAutoTradeSignPay();
        return String.format("签约自动支付 %s 笔订单", count);
    }

}
