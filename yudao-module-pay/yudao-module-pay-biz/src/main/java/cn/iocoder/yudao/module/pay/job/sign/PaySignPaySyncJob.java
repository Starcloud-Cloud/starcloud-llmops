package cn.iocoder.yudao.module.pay.job.sign;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.date.DateUtils;
import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.framework.tenant.core.job.TenantJob;
import cn.iocoder.yudao.module.pay.service.sign.PaySignService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 签约订单的定时支付的 Job
 *
 *
 * @author Cusack Alan
 */
@Component
public class PaySignPaySyncJob implements JobHandler {

    /**
     * 同步创建时间在 N 分钟之前的订单
     *
     * 为什么同步 10 分钟之前的订单？
     *  因为一个订单发起支付，到支付成功，大多数在 10 分钟内，需要保证轮询到。
     *  如果设置为 30、60 或者更大时间范围，会导致轮询的订单太多，影响性能。当然，你也可以根据自己的业务情况来处理。
     */
    private static final Duration CREATE_TIME_DURATION_BEFORE = Duration.ofMinutes(10);

    @Resource
    private PaySignService paySignService;

    @Override
    @TenantJob
    public String execute(String param) {


        int count = paySignService.syncSignPay();
        return StrUtil.format("同步支付订单 {} 个", count);
    }

}
