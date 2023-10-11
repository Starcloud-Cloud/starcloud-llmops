package com.starcloud.ops.business.limits.job;

import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.framework.tenant.core.job.TenantJob;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefits.UserBenefitsDO;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户权益过期 - 定时处理
 *
 * @author Alan Cusack
 */
@Component
@TenantJob
@Slf4j
public class UserBenefitsTimeoutJob implements JobHandler {


    @Resource
    private UserBenefitsService userBenefitsService;

    /**
     * 执行任务
     *
     * @param param 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Override
    public String execute(String param) throws Exception {
        log.info("开始执行权益过期任务，获取已经过期权益");
        Long nums = userBenefitsService.userBenefitsExpired();
        log.info("处理租户【{}】下的用户权益过期，当前共有【{}】条权益过期", TenantContextHolder.getTenantId(), nums);
        return String.format("处理租户【%s】下的处理用户权益过期，当前共有%s条权益过期",TenantContextHolder.getTenantId(), nums);
    }
}
