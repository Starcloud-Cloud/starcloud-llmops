package com.starcloud.ops.business.limits.job;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 用户权益过期 - 定时处理
 *
 * @author Alan Cusack
 */
@Slf4j
@Component
@EnableScheduling
public class UserBenefitsTimeoutJob {


    @Resource
    private UserBenefitsService userBenefitsService;


    // /**
    //  * 一分钟执行一次,这里选择每2分钟的秒执行，是为了避免整点任务过多的问题
    //  */
    // @Scheduled(cron = "0 0/5 * * * ? ")
    // public void userBenefitsExpired() {
    //     try {
    //         log.info("开始执行权益过期任务，获取已经过期权益");
    //         execute();
    //         log.error("[权益过期结束][执行成功]");
    //     } catch (Exception ex) {
    //         log.error("[权益过期失败][执行异常]", ex);
    //     }
    // }


    public String execute() throws Exception {
        // 设置当前租户信息
        TenantContextHolder.setTenantId(2L);

        Long nums = userBenefitsService.userBenefitsExpired();
        log.info("处理租户【{}】下的用户权益过期，当前共有【{}】条权益过期", TenantContextHolder.getTenantId(), nums);
        return String.format("处理租户【%s】下的处理用户权益过期，当前共有%s条权益过期", TenantContextHolder.getTenantId(), nums);
    }
}
