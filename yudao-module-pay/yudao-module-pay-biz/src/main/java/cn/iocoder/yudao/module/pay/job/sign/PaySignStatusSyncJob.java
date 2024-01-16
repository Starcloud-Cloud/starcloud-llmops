package cn.iocoder.yudao.module.pay.job.sign;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.framework.tenant.core.job.TenantJob;
import cn.iocoder.yudao.module.pay.service.sign.PaySignService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 签约状态的同步 Job
 *
 * 由于支付订单的状态，是由支付渠道异步通知进行同步，考虑到异步通知可能会失败（小概率），所以需要定时进行同步。
 *
 * @author 芋道源码
 */
@Component
public class PaySignStatusSyncJob implements JobHandler {

    @Resource
    private PaySignService paySignService;

    @Override
    @TenantJob
    public String execute(String param) {
        int count = paySignService.syncSignStatus();
        return StrUtil.format("同步支付订单 {} 个", count);
    }

}
