package com.starcloud.ops.business.trade.job.sign;

import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import com.starcloud.ops.business.trade.service.sign.TradeSignQueryService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 交易订单每天自动通知
 *
 * @author Alan Cusack
 */
@Component
public class TradeSignAutoNotifyJob implements JobHandler {

    @Resource
    private TradeSignQueryService queryService;


    @Override
    public String execute(String param) {
        AtomicInteger count = new AtomicInteger();
        TenantUtils.execute(2L, () -> count.set(queryService.signAutoNotify())
        );
        return String.format("签约订单 %s 个", count);
    }

}
