package com.starcloud.ops.business.trade.job.order;

import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import com.starcloud.ops.business.trade.service.order.TradeOrderQueryService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 交易订单每天自动通知
 *
 * @author Alan Cusack
 */
@Component
public class TradeOrderAutoNotifyJob implements JobHandler {

    @Resource
    private TradeOrderQueryService queryService;


    /**
     * 指定查询3 天内的数据
     */
    private final Long TIME_NUM = 3L;

    @Override
    public String execute(String param) {
        AtomicInteger count = new AtomicInteger();
        TenantUtils.execute(2L, () -> count.set(queryService.orderAutoNotify(TIME_NUM))
        );

        return String.format("三天内有效订单为 %s 个", count);
    }

}
