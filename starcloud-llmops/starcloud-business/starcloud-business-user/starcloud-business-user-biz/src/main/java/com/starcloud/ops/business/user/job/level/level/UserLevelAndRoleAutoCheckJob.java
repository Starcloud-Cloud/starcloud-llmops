package com.starcloud.ops.business.user.job.level.level;

import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.framework.tenant.core.job.TenantJob;
import com.starcloud.ops.business.user.service.level.AdminUserLevelService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 用户等级等级和用户角色自动检测 任务
 */
@Component
public class UserLevelAndRoleAutoCheckJob implements JobHandler {


    @Resource
    private AdminUserLevelService adminUserLevelService;
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
        int count = adminUserLevelService.expireLevel();
        return String.format("过期等级 %s 个", count);
    }
}
