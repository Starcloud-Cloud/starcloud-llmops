package com.starcloud.ops.business.user.job.rights;

import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.framework.tenant.core.job.TenantJob;
import com.starcloud.ops.business.user.service.rights.AdminUserRightsService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 用户等级过期权益
 */
@Component
public class AdminUserRightsAutoExpireJob implements JobHandler {

    @Resource
    private AdminUserRightsService adminUserRightsService;
    /**
     * 执行任务
     *
     * @param param 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Override
    @TenantJob
    public String execute(String param) throws Exception {
        int count = adminUserRightsService.expireRights();
        return String.format("过期权益 %s 个", count);
    }
}
