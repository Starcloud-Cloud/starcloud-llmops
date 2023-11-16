package com.starcloud.ops.business.order.job.sign;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import com.starcloud.ops.business.order.dal.dataobject.sign.PaySignDO;
import com.starcloud.ops.business.order.service.sign.PaySignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 */
@Slf4j
@Component
@EnableScheduling
public class SignPayJob {

    @Resource
    private PaySignService signService;




    /**
     * 一分钟执行一次,这里选择每2分钟的秒执行，是为了避免整点任务过多的问题
     */
    @Scheduled(cron = "0 0/2 * * * ? ")
    public void ableToPayRecords() {
        try {
            log.info("开始执行签约自动扣款任务，获取已经代扣款的签约记录");
            execute();
            log.error("[签约自动扣款任务][执行成功]");
        } catch (Exception ex) {
            log.error("[签约自动扣款任务][执行异常]", ex);
        }
    }


    public String execute() throws Exception {
        // 设置当前租户信息
        TenantContextHolder.setTenantId(2L);
        // 获取当天的扣款订单
        List<PaySignDO> ableToPayRecords = signService.getAbleToPayRecords();

        if (CollUtil.isNotEmpty(ableToPayRecords)) {
            ableToPayRecords.stream().forEach(paySignDO -> signService.processSigningPayment(paySignDO));

        }
        log.info("处理租户【{}】下的签约自动扣款任务，当前执行了共有【{}】条签约自动扣款任务", TenantContextHolder.getTenantId(), ableToPayRecords.size());
        return String.format("处理租户【%s】下的处理用户权益过期，当前共有%s条签约自动扣款任务", TenantContextHolder.getTenantId(), ableToPayRecords.size());
    }
}
